package com.citysearch.webwidget.exception;

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

    public CitysearchException(String className, String methodName, String message, Throwable cause) {
        super(message, cause);
        this.className = className;
        this.methodName = methodName;
    }

    public CitysearchException(String className, String methodName, Throwable cause) {
        super(cause);
        this.className = className;
        this.methodName = methodName;
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
}
