package com.amediamanager.exceptions;

/**
 * Exception that is raised when a User with an existing e-mail
 * address is added to the data store.
 */
public class UserDoesNotExistException extends RuntimeException { 
	private static final long serialVersionUID = 1L;
}
