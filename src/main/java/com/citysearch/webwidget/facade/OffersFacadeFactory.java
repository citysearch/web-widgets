package com.citysearch.webwidget.facade;

import com.citysearch.webwidget.exception.CitysearchException;

public class OffersFacadeFactory {
	public static AbstractOffersFacade getFacade(String publisher,
			String contextPath, int displaySize) throws CitysearchException {
		if (displaySize == 2) {
			return new ConquestOffersFacade(contextPath, displaySize);
		} else {
			return new MantelOffersFacade(contextPath, displaySize);
		}
	}
}
