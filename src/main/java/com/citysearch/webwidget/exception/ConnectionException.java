package com.citysearch.webwidget.exception;

/**
 * Custom Exception class for Connection failures
 * 
 * @author Aspert  Benjamin
 * 
 */
public class ConnectionException extends Exception {
    public ConnectionException() {
        super();
    }

    public ConnectionException(String message) {
        super(message);
    }
}
