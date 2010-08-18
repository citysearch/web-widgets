package com.citysearch.webwidget.facade;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.helper.ReviewHelper;

public class AbstractReviewFacade {
	private Logger log = Logger.getLogger(getClass());

	protected String contextPath;
	protected int displaySize;

	protected AbstractReviewFacade(String contextPath, int displaySize) {
		this.contextPath = contextPath;
		this.displaySize = displaySize;
	}

	protected Review getLatestReview(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		ReviewHelper helper = new ReviewHelper(contextPath);
		return helper.getLatestReview(request);
	}
}
