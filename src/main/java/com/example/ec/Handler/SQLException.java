package com.example.ec.Handler;

public class SQLException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SQLException(String message) {
		super(message);
	}

	public SQLException(String message, Exception e) {
		super(message);
		System.err.println(e);
	}
}
