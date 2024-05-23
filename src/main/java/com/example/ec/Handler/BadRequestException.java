package com.example.ec.Handler;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Exception e) {
		super(message);
		System.err.println(e);
	}
}
