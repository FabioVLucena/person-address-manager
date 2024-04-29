package com.attornatus.personaddress.manager.dto;

import com.attornatus.personaddress.manager.model.entity.State;

public record StateResponse(Long id, String name, String acronym) {

	public static StateResponse convert(State state) {
		return new StateResponse(state.getId(),
				state.getName(),
				state.getAcronym());
	}
	
}
