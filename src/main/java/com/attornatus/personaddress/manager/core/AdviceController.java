package com.attornatus.personaddress.manager.core;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.attornatus.personaddress.manager.dto.ErrorResponse;
import com.attornatus.personaddress.manager.exception.NotFoundException;

@RestControllerAdvice
public class AdviceController {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse treatmentMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		Set<String> errorStrList = ex.getBindingResult()
				.getAllErrors()
				.stream()
				.map(errorStr -> errorStr.getDefaultMessage())
				.collect(Collectors.toSet());
		
		return ErrorResponse.convert(errorStrList);
	}
	
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse treatmentNotFoundException(NotFoundException ex) {
		String errorStr = ex.getMessage();
		
		return ErrorResponse.convert(errorStr);
	}
}
