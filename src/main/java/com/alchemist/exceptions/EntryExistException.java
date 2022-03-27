package com.alchemist.exceptions;

public class EntryExistException extends Exception {
	private static final long serialVersionUID = 1L;

	public EntryExistException(String errorMessage) {
		super(errorMessage);
	}
}
