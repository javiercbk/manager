package com.company.manager.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Duplicated resource")
public class DuplicatedResourceException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2366059068365821322L;

}
