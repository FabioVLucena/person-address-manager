package com.attornatus.personaddress.manager.model.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String fullName;
	
	private LocalDate birthDate;
	
	public Person() {
		super();
	}

	public Person(Long id) {
		super();
		this.id = id;
	}
	
	public Person(Long id, String fullName, LocalDate birthDate) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.birthDate = birthDate;
	}

	public Person(String fullName, LocalDate birthDate) {
		super();
		this.fullName = fullName;
		this.birthDate = birthDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
}
