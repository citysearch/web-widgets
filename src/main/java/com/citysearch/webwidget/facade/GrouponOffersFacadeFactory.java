package com.citysearch.webwidget.facade;

public class GrouponOffersFacadeFactory {
	public static AbstractGrouponOffersFacade getFacade(String publisher,
			int displaySize, String contextPath) {
		return new UrbanSpoonFacade(contextPath, displaySize);
	}
}
