package com.citysearch.webwidget.exception;


public class NoDataFoundException extends CitysearchException {
    public NoDataFoundException(String className, String methodName, String message) {
        super(className, methodName, message);
    }
}
