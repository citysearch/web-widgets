package com.citysearch.webwidget.action;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.ProfileRequest;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.helper.ProfileHelper;
import com.citysearch.webwidget.helper.ReviewHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

//TODO: Javadocs
public class ReviewAction implements ModelDriven<ReviewRequest> {
	private Logger log = Logger.getLogger(getClass());
	private ReviewRequest reviewRequest = new ReviewRequest();
	private Review review;

	public ReviewRequest getReviewRequest() {
		return reviewRequest;
	}

	public void setReviewRequest(ReviewRequest reviewRequest) {
		this.reviewRequest = reviewRequest;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public ReviewRequest getModel() {
		return reviewRequest;
	}
	
	public String execute() throws CitysearchException
	{
		ReviewHelper helper = new ReviewHelper();
		try
		{
			review = helper.getLatestReview(reviewRequest);
			
			ProfileRequest request = new ProfileRequest();
	        request.setApiKey(reviewRequest.getApiKey());
	        request.setPublisher(reviewRequest.getPublisher());
	        request.setClientIP(reviewRequest.getClientIP());
	        request.setListingId(review.getListingId());
	        
	        ProfileHelper profHelper = new ProfileHelper();
			Profile profile = profHelper.getProfile(request);
			if (profile != null)
			{
				review.setAddress(profile.getAddress());
				review.setPhone(profile.getPhone());
				review.setProfileUrl(profile.getProfileUrl());
				review.setSendToFriendUrl(profile.getSendToFriendUrl());
				review.setImageUrl(profile.getImageUrl());
			}
		}
		catch (CitysearchException cse)
		{
			log.error(cse.getDetailedMessage());
			throw cse;
		}
		return Action.SUCCESS;
	}
}
