package com.citysearch.webwidget.exception;

/**
 * Custom Exception class for handling Invalid(error) Http Response Codes
 * 
 * @author Aspert
 * 
 */
public class InvalidHttpResponseException extends Exception {
    private int responseCode;

    public InvalidHttpResponseException(int responseCode, String message) {
        super(message);
    }

    /**
     * Returns the status code returned by API and error message
     * 
     * @return String
     */
    public String getMessage() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(super.getMessage());
        strBuilder.append(" API Returned Code : ");
        strBuilder.append(responseCode);
        return strBuilder.toString();
    }
}
