package com.attornatus.personaddress.manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.service.ICityService;
import com.attornatus.personaddress.manager.repository.CityRepository;

@Service
public class CityService implements ICityService {

	private CityRepository cityRepository;
	
	@Autowired
	public CityService(CityRepository cityRepository) {
		this.cityRepository = cityRepository;
	}
	
	@Override
	public City getCityById(Long cityId) throws NotFoundException {
		return this.cityRepository.findById(cityId)
				.orElseThrow(() -> new NotFoundException("City with id: " + cityId + " not found!"));
	}

	@Override
	public List<City> findAllCities() {
		return this.cityRepository.findAll();
	}
}
