package com.attornatus.personaddress.manager.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.dto.PersonResponse;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.model.service.IPersonService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

	private IPersonService personService;
	
	@Autowired
	public PersonController(IPersonService personService) {
		this.personService = personService;
	}
	
	@GetMapping
	public ResponseEntity<List<PersonResponse>> findAllPersons() {
		List<Person> personList = this.personService.findAllPersons(); 
		
		List<PersonResponse> res = PersonResponse.convert(personList); 
		
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<PersonResponse>> findPersonsBySearch(@RequestParam String fullName) {
		List<Person> personList = this.personService.findPersonsByFullNameLike(fullName);
		
		List<PersonResponse> res = PersonResponse.convert(personList); 
		
		return ResponseEntity.ok(res);
	}
	
	
	@GetMapping("/{personId}")
	public ResponseEntity<PersonResponse> getPersonById(@PathVariable Long personId) throws NotFoundException {
		Person person = this.personService.getPersonById(personId);
		
		PersonResponse res = PersonResponse.convert(person);
		
		return ResponseEntity.ok(res);
	}

	@PostMapping
	public ResponseEntity<PersonResponse> createPerson(@RequestBody @Valid PersonRequest req) {
		Person person = this.personService.createPerson(req);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/v1/persons/{id}")
				.buildAndExpand(person.getId())
				.toUri();
		
		PersonResponse res = PersonResponse.convert(person);
		
		return ResponseEntity.created(uri).body(res);
	}
	
	@PutMapping("/{personId}")
	public ResponseEntity<PersonResponse> updatePerson(@PathVariable Long personId, @RequestBody @Valid PersonRequest req) throws NotFoundException {
		Person person = this.personService.updatePerson(personId, req);
		
		PersonResponse res = PersonResponse.convert(person);
		
		return ResponseEntity.ok(res);
	}
}
