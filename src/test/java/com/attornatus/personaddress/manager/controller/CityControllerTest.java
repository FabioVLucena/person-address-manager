package com.attornatus.personaddress.manager.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.State;
import com.attornatus.personaddress.manager.service.CityService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CityController.class)
public class CityControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private CityService cityService;
	
    @Test
    void shouldntReturnCities() throws Exception {
    	when(cityService.findAllCities())
    	.thenReturn(new ArrayList<City>());
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/cities")
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$[0]").doesNotExist());
    }

    @Test
    void shouldReturnCities() throws Exception {
    	List<City> cityList = List.of(
    			new City(1L, "Pimenta Bueno", "PB", new State()),
    			new City(2L, "Cacoal", "CO", new State()),
    			new City(3L, "Porto Velho", "PVH", new State())
    			);
    	
    	when(cityService.findAllCities())
    	.thenReturn(cityList);
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/cities")
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$", hasSize(3)));
    }
    
    @Test
    void shouldntReturnCityById() throws Exception {
    	Long cityId = 1L;
    	
    	when(cityService.getCityById(cityId))
    	.thenThrow(new NotFoundException("City with id: " + cityId + " not found!"));
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/cities/{cityId}", cityId)
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCityById() throws Exception {
    	Long cityId = 1L;
    	String expectedName = "Pimenta Bueno";
    	String expectedAcronym = "PB";
    	
    	City city = new City(cityId,
    			expectedName,
    			expectedAcronym,
    			new State());
    	
    	when(cityService.getCityById(cityId))
    	.thenReturn(city);
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/cities/{cityId}", cityId)
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id").value(cityId))
    	.andExpect(jsonPath("$.name").value(expectedName))
    	.andExpect(jsonPath("$.acronym").value(expectedAcronym));
    }
}
