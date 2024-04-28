package com.attornatus.personaddress.manager.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PersonRequest(
		@NotNull(message = "Full name cannot be null!")
		@NotEmpty(message = "Full name cannot be empty!")
		String fullName, 

		@NotNull(message = "Birth date cannot be null!")
		LocalDate birthDate
		) {

}
