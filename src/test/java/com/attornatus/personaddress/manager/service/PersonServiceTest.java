package com.attornatus.personaddress.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.attornatus.personaddress.manager.dto.PersonRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Person;
import com.attornatus.personaddress.manager.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	@Mock
	private PersonRepository personRepository;

	@InjectMocks
	private PersonService personService;

    @Test
    public void shouldntGetPersonById() {
    	Long personId = 1L;
    	
    	when(personRepository.findById(personId)).thenReturn(Optional.empty());
    	
    	assertThrows(NotFoundException.class, () -> personService.getPersonById(personId));
    }
    
    @Test
    public void shouldGetPersonById() {
    	Long personId = 1L;
    	
        Person expectedPerson = new Person(personId, "Uncle bob", LocalDate.of(2000, 5, 15));

        when(personRepository.findById(personId)).thenReturn(Optional.of(expectedPerson));

        try {
            Person person = personService.getPersonById(personId);

            assertNotNull(person);
            assertEquals(expectedPerson.getId(), person.getId());
            assertEquals(expectedPerson.getFullName(), person.getFullName());
            assertEquals(expectedPerson.getBirthDate(), person.getBirthDate());
        } catch (NotFoundException e) {
            fail("There should be a person");
        }
    }

    @Test
    public void shouldFindAllPersons() {
        List<Person> mockPersonList = new ArrayList<>();
        mockPersonList.add(new Person(1L, "Charlie Johson", LocalDate.of(1990, 5, 15)));
        mockPersonList.add(new Person(2L, "Uncle Bob", LocalDate.of(1985, 8, 20)));

        when(personRepository.findAll()).thenReturn(mockPersonList);

        List<Person> allPersons = personService.findAllPersons();

        assertNotNull(allPersons);
        assertFalse(allPersons.isEmpty());
        assertEquals(2, allPersons.size());
    }

    @Test
    public void shouldCreatePerson() {
        PersonRequest req = new PersonRequest("Uncle bob", LocalDate.of(1985, 8, 20));

        Person expectedPerson = new Person(1L, "Uncle bob", LocalDate.of(1985, 8, 20));
        
        when(personRepository.save(any())).thenReturn(expectedPerson);

        Person person = personService.createPerson(req);

        assertNotNull(person);
        assertEquals(expectedPerson.getId(), person.getId());
        assertEquals(expectedPerson.getFullName(), person.getFullName());
        assertEquals(expectedPerson.getBirthDate(), person.getBirthDate());
    }

    @Test
    public void shouldntUpdateInvalidPerson() {
    	Long personId = 1L;
    	
        PersonRequest req = new PersonRequest("Fabio Topson", LocalDate.of(1993, 5, 22));

        when(personRepository.findById(personId)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> personService.updatePerson(personId, req));
    }
    
    @Test
    public void shouldUpdatePerson() {
    	Long personId = 1L;
    	
        Person expectedPerson = new Person(personId, "Charlie Jonhson", LocalDate.of(1990, 5, 15));

        when(personRepository.findById(personId)).thenReturn(Optional.of(expectedPerson));
        when(personRepository.save(any())).thenReturn(expectedPerson);

        PersonRequest req = new PersonRequest("Charlie Topson", LocalDate.of(1990, 5, 20));

        try {
            Person person = personService.updatePerson(personId, req);

            assertNotNull(person);
            assertEquals(expectedPerson.getId(), person.getId());
            assertEquals(req.fullName(), person.getFullName());
            assertEquals(req.birthDate(), person.getBirthDate());
        } catch (NotFoundException e) {
            fail("There should be a person");
        }
    }

    @Test
    public void shouldFindPersonsByFullNameLike() {
        List<Person> mockPersonList = new ArrayList<>();
        mockPersonList.add(new Person(1L, "Fabio Vital", LocalDate.of(1990, 5, 15)));
        mockPersonList.add(new Person(2L, "Fabio Lucena", LocalDate.of(1985, 8, 20)));

        when(personRepository.findAllByFullNameLike("Fabio")).thenReturn(mockPersonList);

        List<Person> personsByFullNameLike = personService.findPersonsByFullNameLike("Fabio");

        assertNotNull(personsByFullNameLike);
        assertFalse(personsByFullNameLike.isEmpty());
        assertEquals(2, personsByFullNameLike.size());
        assertEquals("Fabio Vital", personsByFullNameLike.get(0).getFullName());
    }
}
