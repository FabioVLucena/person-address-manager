package com.attornatus.personaddress.manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.model.service.IPersonService;
import com.attornatus.personaddress.manager.repository.PersonRepository;

@Service
public class PersonService implements IPersonService {

	private PersonRepository personRepository;
	
	@Autowired
	public PersonService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@Override
	public Person getPersonById(Long personId) throws NotFoundException {
		return this.personRepository.findById(personId)
				.orElseThrow(() -> new NotFoundException("Person with id: " + personId + " not found!"));
	}

	@Override
	public List<Person> findAllPersons() {
		return this.personRepository.findAll();
	}

	@Override
	public Person createPerson(PersonRequest req) {
		Person person = new Person(req.fullName(),
				req.birthDate());
		
		return this.personRepository.save(person);
	}

	@Override
	public Person updatePerson(Long personId, PersonRequest req) throws NotFoundException {
		Person person = getPersonById(personId);
		person.setFullName(req.fullName());
		person.setBirthDate(req.birthDate());
		
		return this.personRepository.save(person);
	}

	@Override
	public List<Person> findPersonsByFullNameLike(String fullName) {
		return this.personRepository.findAllByFullNameLike(fullName);
	}
}
