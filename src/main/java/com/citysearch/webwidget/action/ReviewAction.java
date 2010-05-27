package com.citysearch.webwidget.action;

import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.bean.ReviewResponse;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

//TODO: Javadocs
public class ReviewAction implements ModelDriven<ReviewRequest> {
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
	
	public String execute()
	{
		return Action.SUCCESS;
	}
}
