package com.citysearch.webwidget.exception;

public class InvalidHttpResponseException extends Exception {
	private int responseCode;

	public InvalidHttpResponseException(int responseCode, String message) {
		super(message);
	}

	@Override
	public String getMessage() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(super.getMessage());
		strBuilder.append(" API Returned Code : ");
		strBuilder.append(responseCode);
		return strBuilder.toString();
	}
}
