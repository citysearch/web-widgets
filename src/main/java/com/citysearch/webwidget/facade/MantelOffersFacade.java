package com.citysearch.webwidget.facade;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;

public class MantelOffersFacade extends AbstractOffersFacade {
	private Logger log = Logger.getLogger(getClass());

	protected MantelOffersFacade(String contextPath, int displaySize) {
		super(contextPath, displaySize);
	}

	public List<Offer> getOffers(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		return super.getOffers(request);
	}
}
