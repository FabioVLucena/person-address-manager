package com.attornatus.personaddress.manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.attornatus.personaddress.manager.dto.AddressRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Address;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.model.service.IAddressService;
import com.attornatus.personaddress.manager.repository.AddressRepository;

@Service
public class AddressService implements IAddressService {

	private AddressRepository addressRepository;
	
	private PersonService personService;
	
	private CityService cityService;
	
	@Autowired
	public AddressService(AddressRepository addressRepository,
			PersonService personService,
			CityService cityService) {
		
		this.addressRepository = addressRepository;
		this.personService = personService;
		this.cityService = cityService;
	}
	
	@Override
	public Address getAddressById(Long addressId) throws NotFoundException {
		return this.addressRepository.findById(addressId)
				.orElseThrow(() -> new NotFoundException("Address with id: " + addressId + " not found!"));
	}

	@Override
	public Address getMainAddressByPersonId(Long personId) throws NotFoundException {
		return this.addressRepository.getMainAddressByPersonId(personId)
				.orElseThrow(() -> new NotFoundException("Person with id: " + personId + " does not have a main address!"));
	}

	@Override
	public List<Address> findAddressesByPersonId(Long personId) {
		return this.addressRepository.findAddressesByPersonId(personId);
	}

	@Override
	public Address createAddress(Long personId, AddressRequest req) throws NotFoundException {
		Boolean isMain = req.main() == null ? false : req.main(); 
		
		Person person = this.personService.getPersonById(personId);
		
		Address address = new Address(person, req.location(), req.cep(),
				req.number(), isMain);

		if (req.cityId() != null) {
			City city = this.cityService.getCityById(req.cityId());
			address.setCity(city);
		}
		
		if (isMain == true) {
			disableOldMainAddress(personId);
		}
		
		return this.addressRepository.save(address);
	}

	@Override
	public Address updateAddress(Long addressId, AddressRequest req) throws NotFoundException {
		Boolean isMain = req.main() == null ? false : req.main();
		
		Address address = getAddressById(addressId);
		
		address.setLocation(req.location());
		address.setCep(req.cep());
		address.setNumber(req.number());
		address.setMain(isMain);

		if (req.cityId() != null) {
			City city = this.cityService.getCityById(req.cityId());
			address.setCity(city);
		}
		
		if (isMain == true) {
			disableOldMainAddress(address.getPerson().getId());
		}
		
		return this.addressRepository.save(address);
	}

	@Override
	public Address setMainAddress(Long addressId) throws NotFoundException {
		Address address = getAddressById(addressId);
		address.setMain(true);

		disableOldMainAddress(address.getPerson().getId());
		
		return address;
	}

	/*
	 * Found the main address
	 * and set main to false
	 */
	@Override
	public void disableOldMainAddress(Long personId) {
		try {
			Address address = getMainAddressByPersonId(personId);
			address.setMain(false);
			
			this.addressRepository.save(address);
		} catch (NotFoundException e) {
			
		}
	}
}
