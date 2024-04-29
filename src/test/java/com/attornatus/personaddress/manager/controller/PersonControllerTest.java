package com.attornatus.personaddress.manager.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.attornatus.personaddress.manager.dto.AddressRequest;
import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Address;
import com.attornatus.personaddress.manager.model.entity.City;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.model.entity.State;
import com.attornatus.personaddress.manager.service.AddressService;
import com.attornatus.personaddress.manager.service.CityService;
import com.attornatus.personaddress.manager.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PersonService personService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private CityService cityService;
    
    @Test
    void shouldntReturnPersonsWithStartNameCharlie() throws Exception {
    	String expectedFullName = "Charlie";
    	
    	when(personService.findPersonsByFullNameLike(expectedFullName)).thenReturn(List.of());
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/search")
    			.param("fullName", expectedFullName)
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$[0]").doesNotExist());
    }
    
    @Test
    void shouldReturnPersonsWithStartNameCharlie() throws Exception {
    	String firstName = "Charlie";
    	
    	List<Person> personList = List.of(
    			new Person(1L, "Charlie Topson", LocalDate.of(1985, 8, 20)),
    			new Person(1L, "Charlie do Bronx", LocalDate.of(1985, 8, 20))
    			); 
    	
    	when(personService.findPersonsByFullNameLike(firstName)).thenReturn(personList);
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/search")
    			.param("fullName", firstName)
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$", hasSize(2)));
    }
    
    @Test
    void shouldntReturnPersonById() throws Exception {
    	Long personId = 1L;
    	
    	when(personService.getPersonById(personId))
    	.thenThrow(new NotFoundException("Person with id: " + personId + " not found!"));

    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/{personId}", personId)
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPersonById() throws Exception {
    	Long expectedPersonId = 1L;
    	String expectedFullName = "Uncle bob";
    	LocalDate expectedBirthDate = LocalDate.now();
    	String expectedBirthDateStr =
    			expectedBirthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	
    	Person person = new Person(expectedPersonId, expectedFullName, expectedBirthDate);
    	
    	when(personService.getPersonById(expectedPersonId)).thenReturn(person);
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/{personId}", expectedPersonId)
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id").value(expectedPersonId))
    	.andExpect(jsonPath("$.fullName").value(expectedFullName))
    	.andExpect(jsonPath("$.birthDate").value(expectedBirthDateStr));;
    }
    
    @Test
    void shouldntCreatePersonWithInvalidParameters() throws Exception {
    	PersonRequest req = new PersonRequest("", null);

    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
    			   .contentType(MediaType.APPLICATION_JSON)
    			   .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreatePersonWithValidParameters() throws Exception {
    	Long expectedPersonId = 1L;
    	String expectedFullName = "Uncle bob";
    	LocalDate expectedBirthDate = LocalDate.now();
    	
    	Person person = new Person(expectedPersonId, expectedFullName, expectedBirthDate);

    	PersonRequest req = new PersonRequest(expectedFullName, expectedBirthDate);
    	
    	String expectedBirthDateStr =
    			expectedBirthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); 

    	when(personService.createPerson(req)).thenReturn(person);
    	
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.id").value(expectedPersonId))
    	.andExpect(jsonPath("$.fullName").value(expectedFullName))
    	.andExpect(jsonPath("$.birthDate").value(expectedBirthDateStr));
    }

    @Test
    void shouldntUpdatePersonWithInvalidParameters() throws Exception {
    	Long personId = 1L;
    	
    	PersonRequest req = new PersonRequest("", null);

    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}", personId)
    			   .contentType(MediaType.APPLICATION_JSON)
    			   .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldntUpdatePersonWithInvalidId() throws Exception {
    	Long personId = 1L;
    	
    	PersonRequest req = new PersonRequest("Charlie Topson", LocalDate.of(1985, 8, 20));
    	
    	when(personService.updatePerson(personId, req))
    	.thenThrow(new NotFoundException("Person with id: " + personId + " not found!"));

    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}", personId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isNotFound());
    }
    
    @Test
    void shouldUpdatePersonWithValidParameters() throws Exception {
    	Long personId = 1L;
    	String fullName = "Uncle charlie";
    	LocalDate birthDate = LocalDate.now().plusDays(-10);
    	String birthDateStr = birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
    	
    	Person expectedPerson = new Person(personId, fullName, birthDate);
    	
    	PersonRequest req = new PersonRequest(fullName, birthDate);
    	
    	when(personService.updatePerson(personId, req))
    	.thenReturn(expectedPerson);
    	
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}", personId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id").value(personId))
    	.andExpect(jsonPath("$.fullName").value(fullName))
    	.andExpect(jsonPath("$.birthDate").value(birthDateStr));
    }
    
    @Test
    void shouldntCreateAddressWithInvalidParameters() throws Exception {
    	Long personId = 1L;
    	
    	AddressRequest req = new AddressRequest("", "", "245", true, personId);

    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/persons/{personId}/addresses", personId)
    			   .contentType(MediaType.APPLICATION_JSON)
    			   .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateAddress() throws Exception {
    	Long personId = 1L;
    	Long cityId = 1L;
    	
        AddressRequest req = new AddressRequest("Gamescon Avenue", "25675", "898", true, cityId);

        Person person = new Person(personId, "John Snow", LocalDate.of(2024, 1, 12));
        City city = new City(cityId, "Pimenta Bueno", "RO", new State());
        
        Address address = new Address(1L, person, city,
        		req.location(), req.cep(), req.number(),
        		req.main());
        
    	when(addressService.createAddress(personId, req)).thenReturn(address);
        
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/persons/{personId}/addresses", personId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.id").isNotEmpty())
    	.andExpect(jsonPath("$.location").value(req.location()))
    	.andExpect(jsonPath("$.cep").value(req.cep()))
    	.andExpect(jsonPath("$.number").value(req.number()))
    	.andExpect(jsonPath("$.main").value(req.main()))
    	.andExpect(jsonPath("$.person.id").value(personId))
    	.andExpect(jsonPath("$.city.id").value(cityId));
    }
    
    @Test
    void shouldntUpdateAddressWithInvalidParameters() throws Exception {
    	Long personId = 1L;
    	Long addressId = 1L;
    	
    	AddressRequest req = new AddressRequest("", "", "245", true, personId);

    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}/addresses/{addressId}", personId, addressId)
    			   .contentType(MediaType.APPLICATION_JSON)
    			   .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateAddress() throws Exception {
    	Long addressId = 1L;
    	Long personId = 1L;
    	Long cityId = 1L;
    	
    	AddressRequest req = new AddressRequest("Colorado Avenue", "899975", "068", true, cityId);

    	Address address = new Address(addressId, new Person(personId), new City(cityId),
    			req.location(), req.cep(), req.number(), true);

    	when(addressService.updateAddress(addressId, req)).thenReturn(address);
    	
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}/addresses/{addressId}", personId, addressId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id").isNotEmpty())
    	.andExpect(jsonPath("$.location").value(req.location()))
    	.andExpect(jsonPath("$.cep").value(req.cep()))
    	.andExpect(jsonPath("$.number").value(req.number()))
    	.andExpect(jsonPath("$.main").value(req.main()))
    	.andExpect(jsonPath("$.person.id").value(personId))
    	.andExpect(jsonPath("$.city.id").value(cityId));
    }

    @Test
    void shouldntReturnAddressesByPersonId() throws Exception {
    	Long personId = 1L;
    	
    	when(addressService.findAddressesByPersonId(personId)).thenReturn(List.of());
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/{personId}/addresses", personId)
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$[0]").doesNotExist());
    }
    
    @Test
    void shouldReturnAddressesByPersonId() throws Exception {
    	Long personId = 1L;
    	
    	List<Address> addressList = List.of(
    			new Address(new Person(personId), "Colorado Avenue", "899975", "068", false),
    			new Address(new Person(personId), "Richardson Avenue", "82385", "128", false),
    			new Address(new Person(personId), "Pacpi Avenue", "25675", "898", true)
    			); 
    	
    	when(addressService.findAddressesByPersonId(personId)).thenReturn(addressList);
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/{personId}/addresses", personId)
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$", hasSize(3)));
    }
    
    @Test
    void shouldntSetMainAddress() throws Exception {
    	Long personId = 1L;
    	Long addressId = 1L;
    	
    	when(addressService.setMainAddress(addressId))
    	.thenThrow(new NotFoundException("Address with id: " + addressId + " not found!"));
    	
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}/addresses/{addressId}/main", personId, addressId)
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNotFound());
    }

    @Test
    void shouldSetMainAddress() throws Exception {
    	Long personId = 1L;
        Long addressId = 1L;
        
        Address address = new Address(addressId, new Person(personId), null,
        		"Pacpi Avenue", "25675", "898", true);
        
        when(addressService.setMainAddress(addressId)).thenReturn(address);

    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}/addresses/{addressId}/main", personId, addressId)
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.main").value(true));
    }
    
    @Test
    void shouldntFindMainAddressByPersonId() throws Exception {
    	Long personId = 1L;
    	
    	when(addressService.getMainAddressByPersonId(personId))
    	.thenThrow(new NotFoundException("Person with id: " + personId + " does not have a main address!"));
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/" + personId +"/addresses/main")
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNotFound());
    }
    
    @Test
    void shouldFindMainAddressByPersonId() throws Exception {
    	Long personId = 1L;
    	
    	Address address = new Address(1L, new Person(personId), new City(),
    			"Colorado Avenue", "899975", "068", true);
    	
    	when(addressService.getMainAddressByPersonId(personId)).thenReturn(address);
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/" + personId +"/addresses/main")
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$").exists())
    	.andExpect(jsonPath("$.main").value(true));
    }
}
