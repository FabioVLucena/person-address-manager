package com.attornatus.personaddress.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attornatus.personaddress.manager.dto.CityResponse;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.service.ICityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/cities")
@Tag(name = "City Search", description = "Endpoints for city search")
public class CityController {
	
	private ICityService cityService;
	
	@Autowired
	public CityController(ICityService cityService) {
		this.cityService = cityService;
	}

	@Operation(summary = "Get the list of all cities")
	@GetMapping
	public ResponseEntity<List<CityResponse>> findAllCities() {
		List<City> cityList = this.cityService.findAllCities();

		List<CityResponse> res = CityResponse.convert(cityList); 
		
		return ResponseEntity.ok(res);
	}

	@Operation(summary = "Get a city by its id")
	@GetMapping("/{cityId}")
	public ResponseEntity<CityResponse> getCityById(@PathVariable Long cityId) throws NotFoundException {
		City city = this.cityService.getCityById(cityId);
		
		CityResponse res = CityResponse.convert(city);
		
		return ResponseEntity.ok(res);
	}
}
