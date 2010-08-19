package com.citysearch.webwidget.facade;

import org.apache.log4j.Logger;

public class MantelOffersFacade extends AbstractOffersFacade {
	private Logger log = Logger.getLogger(getClass());

	protected MantelOffersFacade(String contextPath, int displaySize) {
		super(contextPath, displaySize);
	}
}
