package com.attornatus.personaddress.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.State;
import com.attornatus.personaddress.manager.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
public class CityServiceTest {

	@Mock
	private CityRepository cityRepository;

	@InjectMocks
    private CityService cityService;

    @Test
    public void shouldntGetCityById() {
    	Long cityId = 1L;
    	
    	when(cityRepository.findById(cityId)).thenReturn(Optional.empty());
    	
    	assertThrows(NotFoundException.class, () -> cityService.getCityById(cityId));
    }
    
    @Test
    public void shouldGetCityById() throws NotFoundException {
        Long cityId = 1L;
        
        City expectedCity = new City(cityId,
        		"Rio de Janeiro",
        		"RJ",
        		new State());
        
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(expectedCity));

        City city = cityService.getCityById(cityId);

        assertNotNull(city);
        assertEquals(expectedCity.getId(), city.getId());
        assertEquals(expectedCity.getName(), city.getName());
        assertEquals(expectedCity.getAcronym(), city.getAcronym());
    }
}
