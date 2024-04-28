package com.attornatus.personaddress.manager.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.dto.PersonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PersonControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldntReturnPersonsWithStartNameCharlie() throws Exception {
    	String expectedFullName = "Charlie";
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/search")
    			.param("fullName", expectedFullName)
    			.accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$[0]").doesNotExist());
    }
    
    @Test
    void shouldReturnPersonsWithStartNameCharlie() throws Exception {
    	String expectedFullName = "Charlie Jhoson";
    	LocalDate expectedBirthDate = LocalDate.now();
    	
    	PersonRequest req = new PersonRequest(expectedFullName, expectedBirthDate);

    	String firstName = "Charlie";
    	
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isOk());
    	
    	mvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/search")
    			.param("fullName", firstName)
			    .accept(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$[0]").exists())
    	.andExpect(jsonPath("$[0].id").isNotEmpty())
    	.andExpect(jsonPath("$[0].fullName").value(containsString(firstName)))
    	.andExpect(jsonPath("$[0].birthDate").isNotEmpty());
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
    	String expectedFullName = "Uncle bob";
    	LocalDate expectedBirthDate = LocalDate.now();
    	
    	String expectedBirthDateStr =
    			expectedBirthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); 
    	
    	PersonRequest req = new PersonRequest(expectedFullName, expectedBirthDate);
    	
    	mvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id").isNotEmpty())
    	.andExpect(jsonPath("$.fullName").value(expectedFullName))
    	.andExpect(jsonPath("$.birthDate").value(expectedBirthDateStr));
    }

    @Test
    void shouldntUpdatePersonWithInvalidParameters() throws Exception {
    	Long personId = 1L;
    	
    	PersonRequest req = new PersonRequest("", null);

    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/" + personId)
    			   .contentType(MediaType.APPLICATION_JSON)
    			   .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldUpdatePersonWithValidParameters() throws Exception {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	
    	String fullName = "Uncle charlie";
    	LocalDate birthDate = LocalDate.now().plusDays(-10);
    	String birthDateStr = birthDate.format(formatter); 
    	
    	PersonRequest req = new PersonRequest(fullName, birthDate);
    	
    	MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(req)))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.fullName").value(fullName))
    	.andExpect(jsonPath("$.birthDate").value(birthDateStr))
    	.andReturn();
    	
    	String jsonResponse = result.getResponse().getContentAsString();
    	
    	PersonResponse res = objectMapper.readValue(jsonResponse, PersonResponse.class);
    	
    	Long expectedPersonId = res.id();
    	String expectedFullName = "Uncle bob";
    	LocalDate expectedBirthDate = LocalDate.now();
    	String expectedBirthDateStr = expectedBirthDate.format(formatter); 
    	
    	PersonRequest bobReq = new PersonRequest(expectedFullName, expectedBirthDate);
    	
    	mvc.perform(MockMvcRequestBuilders.put("/api/v1/persons/{personId}", expectedPersonId)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(bobReq)))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.id").value(expectedPersonId))
    	.andExpect(jsonPath("$.fullName").value(expectedFullName))
    	.andExpect(jsonPath("$.birthDate").value(expectedBirthDateStr));
    }
}
