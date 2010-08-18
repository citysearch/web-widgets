package com.citysearch.webwidget.facade;

import com.citysearch.webwidget.exception.CitysearchException;

public class ReviewFacadeFactory {
	
	public static AbstractReviewFacade getFacade(String publisher,
			String contextPath, int displaySize) throws CitysearchException {
		return new MantelReviewFacade(contextPath, displaySize);
	}
}
