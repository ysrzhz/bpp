package com.wasu.sid;

public class UrmException extends Exception{
	private static final long serialVersionUID = -5110502289963194526L;
	private String error;
	
	public UrmException(String error, String message) {
		super(message);
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
	public static void notNull(Object value, String code, String message) throws UrmException {
		assertFor(value != null && value.toString().trim().length() > 0, code, message);
	}
	
	public static void lengthBetween(String value,int minLength,int maxLength, String code, String message) throws Exception {
		assertFor(value != null && value.trim().length()>= minLength && value.trim().length() <= maxLength, code, message);
	}
	
	public static void assertFor(boolean condition, String code, String message) throws UrmException {
		if (!condition) {
			throw new UrmException(code, message);
		}
	}
}