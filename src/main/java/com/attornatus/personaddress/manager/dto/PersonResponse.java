package com.attornatus.personaddress.manager.dto;

import java.time.LocalDate;
import java.util.List;

import com.attornatus.personaddress.manager.model.entity.Person;

public record PersonResponse(Long id, String fullName, LocalDate birthDate) {

	public static PersonResponse convert(Person person) {
		return new PersonResponse(person.getId(),
				person.getFullName(),
				person.getBirthDate());
	}

	public static List<PersonResponse> convert(List<Person> personList) {
		return personList.stream().map(PersonResponse::convert).toList();
	}
}
