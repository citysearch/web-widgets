package com.citysearch.webwidget.facade;

import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;

public class MantelReviewFacade extends AbstractReviewFacade {
	protected MantelReviewFacade(String contextPath, int displaySize) {
		super(contextPath, displaySize);
	}

	public Review getLatestReview(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		return super.getLatestReview(request);
	}
}
