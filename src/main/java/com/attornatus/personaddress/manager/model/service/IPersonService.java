package com.attornatus.personaddress.manager.model.service;

import java.util.List;

import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Person;

public interface IPersonService {

	Person getPersonById(Long personId) throws NotFoundException;
	
	List<Person> findPersonsByFullNameLike(String fullName);
	
	List<Person> findAllPersons();
	
	Person createPerson(PersonRequest req);
	
	Person updatePerson(Long personId, PersonRequest req) throws NotFoundException;

}
