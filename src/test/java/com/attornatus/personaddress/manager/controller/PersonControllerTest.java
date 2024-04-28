package com.attornatus.personaddress.manager.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.service.AddressService;
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
    
    @Test
    void shouldntReturnPersonsWithStartNameCharlie() throws Exception {
    	String expectedFullName = "Charlie";
    	
    	when(personService.findPersonsByFullNameLike(expectedFullName)).thenReturn(new ArrayList<Person>());
    	
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
    	
    	when(personService.updatePerson(personId, req)).thenThrow(new NotFoundException("Person with id: " + personId + " not found!"));

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
    	
    	when(personService.updatePerson(personId, req)).thenReturn(expectedPerson);
    	
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}", personId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id").value(personId))
    	.andExpect(jsonPath("$.fullName").value(fullName))
    	.andExpect(jsonPath("$.birthDate").value(birthDateStr));
    }
}
