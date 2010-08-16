package com.citysearch.webwidget.facade;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.CommonConstants;

public class NearByPlacesFacadeFactory {
	public static AbstractNearByPlacesFacade getFacade(String publisher,
			String contextPath) throws CitysearchException {
		if (publisher == null) {
			throw new CitysearchException("NearByPlacesFacadeFactory",
					"getFacade", "Invalid Publisher Code.");
		} else if (publisher
				.equalsIgnoreCase(CommonConstants.PUBLISHER_INSIDERPAGES)) {
			return new InsiderPagesNearByPlacesFacade(contextPath);
		} else if (publisher
				.equalsIgnoreCase(CommonConstants.PUBLISHER_PROJECT_YELLOW)
				|| publisher
						.equalsIgnoreCase(CommonConstants.PUBLISHER_CITYSEARCH)) {
			return new ConquestNearByPlacesFacade(contextPath);
		}
		return null;
	}
}
