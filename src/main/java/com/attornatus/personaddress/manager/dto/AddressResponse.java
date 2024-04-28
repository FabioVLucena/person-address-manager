package com.attornatus.personaddress.manager.dto;

import java.util.List;

import com.attornatus.personaddress.manager.model.entity.Address;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.Person;

public record AddressResponse(Long id, String location, String cep,
		String number, Boolean main, PersonResponse person, CityResponse city) {

	public static AddressResponse convert(Address address) {
		Person person = address.getPerson();
		City city = address.getCity();
		
		PersonResponse personRes = PersonResponse.convert(person);
		CityResponse cityRes = CityResponse.convert(city);
		
		return new AddressResponse(address.getId(), address.getLocation(), address.getCep(),
				address.getNumber(), address.getMain(), personRes, cityRes);
	}
	
	public static List<AddressResponse> convert(List<Address> addressList) {
		return addressList.stream().map(AddressResponse::convert).toList();
	}
}
