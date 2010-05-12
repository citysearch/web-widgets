package com.citysearch.exception;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

public class CitySearchException extends ServletException {

    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(getClass());

    public CitySearchException() {
        super();
        log.error(this.getStackTrace());
    }

    public CitySearchException(String message) {
        super(message);
        log.error(this.getStackTrace());
    }

}
