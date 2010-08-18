package com.citysearch.webwidget.facade.helper;

import com.citysearch.webwidget.api.bean.LocationProfile;
import com.citysearch.webwidget.api.bean.ReviewResponse;
import com.citysearch.webwidget.api.proxy.ProfileProxy;
import com.citysearch.webwidget.bean.Address;
import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.Utils;

public class ProfileHelper {
	private String contextPath;

	public ProfileHelper(String contextPath) {
		this.contextPath = contextPath;
	}

	private Profile toProfile(LocationProfile location)
			throws CitysearchException {
		Profile profile = new Profile();
		profile.setListingId(location.getListingId());
		profile.setProfileUrl(location.getProfileUrl());
		profile.setSendToFriendUrl(location.getSendToFriendUrl());
		profile.setReviewsUrl(location.getReviewsUrl());
		profile.setWebsiteUrl(location.getWebsiteUrl());
		profile.setMenuUrl(location.getMenuUrl());
		profile.setReservationUrl(location.getReservationUrl());
		profile.setMapUrl(location.getMapUrl());
		profile.setReviewCount(location.getReviewCount());
		profile.setImageUrl(location.getImageUrl());
		profile.setPhone(location.getPhone());

		Address address = new Address();
		address.setStreet(location.getStreet());
		address.setCity(location.getCity());
		address.setState(location.getState());
		address.setPostalCode(location.getPostalCode());
		profile.setAddress(address);
		return profile;
	}

	private Profile toProfileAndLatestReview(RequestBean request,
			LocationProfile location) throws CitysearchException {
		Profile profile = toProfile(location);

		if (location.getReview() != null) {
			ReviewResponse reviewResponse = location.getReview();
			Review review = ReviewHelper.toReview(request, reviewResponse,
					contextPath, request.getAdUnitIdentifier());

			review.setCallBackFunction(request.getCallBackFunction());
			review.setCallBackUrl(request.getCallBackUrl());

			String adDisplayTrackingUrl = Utils.getTrackingUrl(review
					.getReviewUrl(), null, request.getCallBackUrl(), request
					.getDartClickTrackUrl(), profile.getListingId(), profile
					.getPhone(), request.getPublisher(), request
					.getAdUnitName(), request.getAdUnitSize());
			review.setReviewTrackingUrl(adDisplayTrackingUrl);

			String callBackFn = Utils.getCallBackFunctionString(request
					.getCallBackFunction(), profile.getListingId(), profile
					.getPhone());
			review.setCallBackFunction(callBackFn);

			profile.setReview(review);

			if (profile.getSendToFriendUrl() != null) {
				String sendToFriendTrackingUrl = Utils.getTrackingUrl(profile
						.getSendToFriendUrl(), null, request.getCallBackUrl(),
						request.getDartClickTrackUrl(), profile.getListingId(),
						profile.getPhone(), request.getPublisher(), request
								.getAdUnitName(), request.getAdUnitSize());
				profile.setSendToFriendTrackingUrl(sendToFriendTrackingUrl);
			}
		}

		return profile;
	}

	public Profile getProfile(RequestBean request) throws CitysearchException {
		ProfileProxy proxy = new ProfileProxy();
		LocationProfile profileResponse = proxy
				.getProfile(request, contextPath);
		return toProfile(profileResponse);
	}

	public Profile getProfileAndHighestReview(RequestBean request)
			throws CitysearchException {
		ProfileProxy proxy = new ProfileProxy();
		LocationProfile profileResponse = proxy.getProfileAndLatestReview(
				request, contextPath);
		return toProfileAndLatestReview(request, profileResponse);
	}
}
