package com.attornatus.personaddress.manager.dto;

import java.util.List;

import com.attornatus.personaddress.manager.model.entity.Address;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.model.entity.State;

public record AddressResponse(Long id, String location, String cep,
		String number, Boolean main, PersonResponse person, CityResponse city) {

	public static AddressResponse convert(Address address) {
		Person person = address.getPerson();
		City city = address.getCity();
		State state = city.getState();
		
		PersonResponse personRes = 
				new PersonResponse(person.getId(), person.getFullName(), person.getBirthDate());
		
		StateResponse stateRes = 
				new StateResponse(state.getId(), state.getName(), state.getAcronym());
		
		CityResponse cityRes = 
				new CityResponse(city.getId(), city.getName(), city.getAcronym(), stateRes);
		
		return new AddressResponse(address.getId(), address.getLocation(), address.getCep(),
				address.getNumber(), address.getMain(), personRes, cityRes);
	}
	
	public static List<AddressResponse> convert(List<Address> addressList) {
		return addressList.stream().map(AddressResponse::convert).toList();
	}
}
