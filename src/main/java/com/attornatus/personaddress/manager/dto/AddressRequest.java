package com.attornatus.personaddress.manager.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddressRequest(
		@NotNull(message = "Location cannot be null!")
		@NotEmpty(message = "Location cannot be empty!")
		String location,
		
		@NotNull(message = "CEP cannot be null!")
		@NotEmpty(message = "CEP cannot be empty!")
		String cep,
		
		String number,
		
		Boolean main,
		
		Long cityId) {

}
