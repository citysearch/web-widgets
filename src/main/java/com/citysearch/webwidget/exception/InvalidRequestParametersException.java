package com.citysearch.webwidget.exception;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Custom exception thrown when there are request parameter validation error(s)
 * 
 * @author Aspert Benjamin
 * 
 */
public class InvalidRequestParametersException extends CitysearchException {
	private List<String> errors;

	public InvalidRequestParametersException(String className,
			String methodName, String message, List<String> errors) {
		super(className, methodName, message);
		this.errors = errors;
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

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	/**
	 * Returns the detailed message with the classname, methodname and list of
	 * all errors
	 * 
	 * @return String
	 */
	public String getDetailedMessage() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getClassName());
		strBuilder.append(".");
		strBuilder.append(getMethodName());
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
