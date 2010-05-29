package com.citysearch.webwidget.action;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.bean.ReviewResponse;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.helper.ProfileHelper;
import com.citysearch.webwidget.helper.ReviewHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

//TODO: Javadocs
public class ReviewAction implements ModelDriven<ReviewRequest> {
	private Logger log = Logger.getLogger(getClass());
	private ReviewRequest reviewRequest = new ReviewRequest();
	private ReviewResponse reviewResponse;

	public ReviewRequest getReviewRequest() {
		return reviewRequest;
	}

	public void setReviewRequest(ReviewRequest reviewRequest) {
		this.reviewRequest = reviewRequest;
	}

	public ReviewResponse getReviewResponse() {
		return reviewResponse;
	}

	public void setReviewResponse(ReviewResponse reviewResponse) {
		this.reviewResponse = reviewResponse;
	}
	
	public ReviewRequest getModel() {
		return reviewRequest;
	}
	
	public String execute() throws CitysearchException
	{
		ReviewHelper helper = new ReviewHelper(reviewRequest);
		try
		{
			reviewResponse = helper.getReviews();
			ProfileHelper profHelper = new ProfileHelper(reviewRequest, reviewResponse.getReviews());
			reviewResponse = profHelper.getProfileForReviews(reviewResponse.getReviews());
		}
		catch (CitysearchException cse)
		{
			log.error(cse.getDetailedMessage());
			throw cse;
		}
		return Action.SUCCESS;
	}
}
