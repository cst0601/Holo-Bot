package com.alchemist.exceptions;

public class ArgumentParseException extends Exception {
	private static final long serialVersionUID = 1L;

	public ArgumentParseException(String errorMessage) {
		super(errorMessage);
	}
}
