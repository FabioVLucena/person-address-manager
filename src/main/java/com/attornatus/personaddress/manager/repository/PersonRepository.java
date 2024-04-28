package com.attornatus.personaddress.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.attornatus.personaddress.manager.model.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

	@Query("SELECT p FROM Person p WHERE lower(p.fullName) like lower(concat('%', :fullName,'%'))")
	List<Person> findAllByFullNameLike(@Param("fullName") String fullName);
	
}
