package com.company.manager.repositories.exceptions;

import java.io.Serializable;

public class NonExistingEntityException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8599026831038626118L;
	private final String message;
	
	public NonExistingEntityException(Class<?> clazz, Serializable entityId){
		this.message = "No entity found with id " + entityId.toString() + " of type " + clazz.getCanonicalName();
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
