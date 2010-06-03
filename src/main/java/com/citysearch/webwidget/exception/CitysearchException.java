package com.citysearch.webwidget.exception;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Custom Exception class for all APIs This exception is thrown whenever there is an error in the
 * application Logs the detailed message in the logs
 * 
 * @author Aspert
 * 
 */
public class CitysearchException extends Exception {
    private static final long serialVersionUID = 1L;
    private String className;
    private String methodName;
    private List<String> errors;

    public CitysearchException(String className, String methodName) {
        super();
        this.className = className;
        this.methodName = methodName;
    }

    public CitysearchException(String className, String methodName, String message) {
        super(message);
        this.className = className;
        this.methodName = methodName;
    }

    public CitysearchException(String className, String methodName, List<String> errors) {
        super();
        this.errors = errors;
        this.className = className;
        this.methodName = methodName;
    }

    public CitysearchException(String className, String methodName, String message,
            List<String> errors) {
        super(message);
        this.errors = errors;
        this.className = className;
        this.methodName = methodName;
    }

    /**
     * Returns the list of errors
     * 
     * @return List
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Adds the error message to the error list
     * 
     * @param message
     */
    public void addError(String message) {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        errors.add(message);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Returns the detailed message with the classname, methodname and list of all errors
     * 
     * @return String
     */
    public String getDetailedMessage() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(className);
        strBuilder.append(".");
        strBuilder.append(methodName);
        strBuilder.append("::");
        strBuilder.append(getMessage());
        if (errors != null && !errors.isEmpty()) {
            strBuilder.append('\n');
            Iterator<String> it = errors.iterator();
            while (it.hasNext()) {
                strBuilder.append(it.next());
                strBuilder.append('\n');
            }
        }
        return strBuilder.toString();
    }
}
