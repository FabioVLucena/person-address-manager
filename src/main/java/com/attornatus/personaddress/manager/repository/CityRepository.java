package com.attornatus.personaddress.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.attornatus.personaddress.manager.model.entity.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

}
