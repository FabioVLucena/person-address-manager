package com.attornatus.personaddress.manager.dto;

import java.util.Arrays;
import java.util.Set;

public record ErrorResponse(Set<String> error) {

	public static ErrorResponse convert(String error) {
		return new ErrorResponse(Set.copyOf(Arrays.asList(error)));
	}

	public static ErrorResponse convert(Set<String> errorList) {
		return new ErrorResponse(errorList);
	}
}
