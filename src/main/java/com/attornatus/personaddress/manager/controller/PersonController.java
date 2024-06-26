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

import com.attornatus.personaddress.manager.dto.AddressRequest;
import com.attornatus.personaddress.manager.dto.AddressResponse;
import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.dto.PersonResponse;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Address;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.model.service.IAddressService;
import com.attornatus.personaddress.manager.model.service.IPersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/persons")
@Tag(name = "Person and Address Management", description = "Endpoints for person and address management")
public class PersonController {

	private IPersonService personService;
	
	private IAddressService addressService;
	
	@Autowired
	public PersonController(IPersonService personService, IAddressService addressService) {
		this.personService = personService;
		this.addressService = addressService;
	}
	
	@Operation(summary = "Get the list of all persons")
	@GetMapping
	public ResponseEntity<List<PersonResponse>> findAllPersons() {
		List<Person> personList = this.personService.findAllPersons(); 
		
		List<PersonResponse> res = PersonResponse.convert(personList); 
		
		return ResponseEntity.ok(res);
	}
	
	@Operation(summary = "Get the list of all persons with name like search")
	@GetMapping("/search")
	public ResponseEntity<List<PersonResponse>> findPersonsBySearch(@RequestParam String fullName) {
		List<Person> personList = this.personService.findPersonsByFullNameLike(fullName);
		
		List<PersonResponse> res = PersonResponse.convert(personList); 
		
		return ResponseEntity.ok(res);
	}
	
	@Operation(summary = "Get a person by its id")
	@GetMapping("/{personId}")
	public ResponseEntity<PersonResponse> getPersonById(@PathVariable Long personId) throws NotFoundException {
		Person person = this.personService.getPersonById(personId);
		
		PersonResponse res = PersonResponse.convert(person);
		
		return ResponseEntity.ok(res);
	}

	@Operation(summary = "Creates a new person")
	@PostMapping
	public ResponseEntity<PersonResponse> createPerson(@RequestBody @Valid PersonRequest req) {
		Person person = this.personService.createPerson(req);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/v1/persons/{personId}")
				.buildAndExpand(person.getId())
				.toUri();
		
		PersonResponse res = PersonResponse.convert(person);
		
		return ResponseEntity.created(uri).body(res);
	}
	
	@Operation(summary = "Updates a person")
	@PutMapping("/{personId}")
	public ResponseEntity<PersonResponse> updatePerson(@PathVariable Long personId, @RequestBody @Valid PersonRequest req) throws NotFoundException {
		Person person = this.personService.updatePerson(personId, req);
		
		PersonResponse res = PersonResponse.convert(person);
		
		return ResponseEntity.ok(res);
	}
	
	@Operation(summary = "Get a list of all a person's addresses")	
	@GetMapping("/{personId}/addresses")
	public ResponseEntity<List<AddressResponse>> findAddressesByPersonId(@PathVariable Long personId) {
		List<Address> addressList = this.addressService.findAddressesByPersonId(personId);
		
		List<AddressResponse> res = AddressResponse.convert(addressList);
		
		return ResponseEntity.ok(res);
	}
	
	@Operation(summary = "Creates a new person address")
	@PostMapping("/{personId}/addresses")
	public ResponseEntity<AddressResponse> createAddress(@PathVariable Long personId, @RequestBody @Valid AddressRequest req) throws NotFoundException {
		Address address = this.addressService.createAddress(personId, req);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/v1/persons/{personId}/addresses/{addressId}")
				.buildAndExpand(address.getPerson().getId(), address.getId())
				.toUri();
		
		AddressResponse res = AddressResponse.convert(address);
		
		return ResponseEntity.created(uri).body(res);
	}

	@Operation(summary = "Updates a person address")
	@PutMapping("/{personId}/addresses/{addressId}")
	public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long addressId, @RequestBody @Valid AddressRequest req) throws NotFoundException {
		Address address = this.addressService.updateAddress(addressId, req);
		
		AddressResponse res = AddressResponse.convert(address);
		
		return ResponseEntity.ok(res);
	}
	
	@Operation(summary = "Change the person's main address")
	@PutMapping("/{personId}/addresses/{addressId}/main")
	public ResponseEntity<AddressResponse> setMainAddress(@PathVariable Long addressId) throws NotFoundException {
		Address address = this.addressService.setMainAddress(addressId);
		
		AddressResponse res = AddressResponse.convert(address);
		
		return ResponseEntity.ok(res);
	}
	
	@Operation(summary = "Get the person's main address")
	@GetMapping("/{personId}/addresses/main")
	public ResponseEntity<AddressResponse> getMainAddressByPersonId(@PathVariable Long personId) throws NotFoundException {
		Address address = this.addressService.getMainAddressByPersonId(personId);
		
		AddressResponse res = AddressResponse.convert(address);
		
		return ResponseEntity.ok(res);
	}
}
