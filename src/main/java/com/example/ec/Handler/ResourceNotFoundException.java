package com.example.ec.Handler;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String message, Exception e) {
		super(message);
		System.err.println(e);
	}
}
