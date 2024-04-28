package com.attornatus.personaddress.manager.dto;

public record AddressRequest(String location, String cep, String number,
		Boolean main, Long cityId) {

}
