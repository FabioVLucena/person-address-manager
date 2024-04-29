package com.attornatus.personaddress.manager.model.service;

import java.util.List;

import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.City;

public interface ICityService {

	City getCityById(Long cityId) throws NotFoundException;
	
	List<City> findAllCities();
	
}
