package com.attornatus.personaddress.manager.dto;

import java.util.List;

import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.State;

public record CityResponse(Long id, String name, String acronym, StateResponse state) {
	
	public static CityResponse convert(City city) {
		State state = city.getState();
		
		StateResponse stateRes = StateResponse.convert(state); 
		
		return new CityResponse(city.getId(),
				city.getName(),
				city.getAcronym(),
				stateRes);
	}

	public static List<CityResponse> convert(List<City> cityList) {
		return cityList.stream().map(CityResponse::convert).toList();
	}
}
