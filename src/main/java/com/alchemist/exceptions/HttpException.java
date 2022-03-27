package com.alchemist.exceptions;

public class HttpException extends Exception {
	private static final long serialVersionUID = 1L;
	private final int statusCode;
	
	public HttpException(String errorMessage, int statusCode) {
		super(errorMessage);
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}
