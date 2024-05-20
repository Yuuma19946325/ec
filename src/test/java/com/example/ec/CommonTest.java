package com.example.ec;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonTest {

	public static String asJsonString(final Object obj) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void name() {

	}
}
