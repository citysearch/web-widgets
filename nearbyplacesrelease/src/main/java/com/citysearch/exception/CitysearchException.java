package com.citysearch.exception;

import javax.servlet.ServletException;

public class CitysearchException extends ServletException {

    private static final long serialVersionUID = 1L;

    public CitysearchException() {
        super();
    }

    public CitysearchException(String message) {
        super(message);
    }

}
