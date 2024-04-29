package com.attornatus.personaddress.manager.model.service;

import java.util.List;

import com.attornatus.personaddress.manager.dto.AddressRequest;
import com.attornatus.personaddress.manager.exception.NotFoundException;
import com.attornatus.personaddress.manager.model.entity.Address;

public interface IAddressService {

	Address getAddressById(Long addressId) throws NotFoundException;
	
	Address getMainAddressByPersonId(Long personId) throws NotFoundException;

	List<Address> findAddressesByPersonId(Long personId);
	
	Address createAddress(Long personId, AddressRequest req) throws NotFoundException;

	Address updateAddress(Long addressId, AddressRequest req) throws NotFoundException;
	
	Address setMainAddress(Long addressId) throws NotFoundException;
	
	void disableOldMainAddress(Long personId);
	
}
