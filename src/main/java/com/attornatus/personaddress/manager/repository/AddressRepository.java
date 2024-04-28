package com.attornatus.personaddress.manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.attornatus.personaddress.manager.model.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	@Query("SELECT a FROM Address a WHERE a.main = true and a.person.id = :personId")
	Optional<Address> getMainAddressByPersonId(@Param("personId") Long personId);

	@Query("SELECT a FROM Address a WHERE a.person.id = :personId")
	List<Address> findAddressesByPersonId(@Param("personId") Long personId);
	
}
