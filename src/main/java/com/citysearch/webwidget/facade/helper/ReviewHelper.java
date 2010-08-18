package com.citysearch.webwidget.facade.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.ReviewResponse;
import com.citysearch.webwidget.api.proxy.ReviewProxy;
import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;
import com.citysearch.webwidget.util.Utils;

public class ReviewHelper {
	private Logger log = Logger.getLogger(getClass());
	private static final String DATE_FORMAT = "reviewdate.format";
	private static final int MINIMUM_RATING = 6;

	private String contextPath;

	public ReviewHelper(String contextPath) {
		this.contextPath = contextPath;
	}

	public static Review toReview(RequestBean request,
			ReviewResponse reviewResponse, String path, String adUnitIdentifier)
			throws CitysearchException {
		Review review = new Review();

		String businessName = reviewResponse.getBusinessName();
		review.setBusinessName(businessName);
		StringBuilder nameLengthProp = new StringBuilder(adUnitIdentifier);
		nameLengthProp.append(".");
		nameLengthProp.append(CommonConstants.NAME_LENGTH);
		businessName = Utils.getAbbreviatedString(businessName,
				nameLengthProp.toString());
		review.setShortBusinessName(businessName);

		String reviewTitle = reviewResponse.getReviewTitle();
		review.setReviewTitle(reviewTitle);
		StringBuilder titleLengthProp = new StringBuilder(adUnitIdentifier);
		titleLengthProp.append(".");
		titleLengthProp.append(CommonConstants.REVIEW_TITLE_LENGTH);
		reviewTitle = Utils.getAbbreviatedString(reviewTitle,
				titleLengthProp.toString());
		review.setShortTitle(reviewTitle);

		String reviewText = reviewResponse.getReviewText();
		review.setReviewText(reviewText);
		StringBuilder textLengthProp = new StringBuilder(adUnitIdentifier);
		textLengthProp.append(".");
		textLengthProp.append(CommonConstants.REVIEW_TEXT_LENGTH);
		reviewText = Utils.getAbbreviatedString(reviewText, textLengthProp
				.toString());
		review.setShortReviewText(reviewText);

		textLengthProp = new StringBuilder(adUnitIdentifier);
		textLengthProp.append(".");
		textLengthProp.append(CommonConstants.REVIEW_TEXT_SMALL_LENGTH);
		reviewText = Utils.getAbbreviatedString(reviewText, textLengthProp
				.toString());
		review.setSmallReviewText(reviewText);

		String pros = reviewResponse.getPros();
		review.setPros(pros);
		StringBuilder prosLengthProp = new StringBuilder(adUnitIdentifier);
		prosLengthProp.append(".");
		prosLengthProp.append(CommonConstants.REVIEW_PROS_LENGTH);
		pros = Utils.getAbbreviatedString(pros, prosLengthProp.toString());
		review.setShortPros(pros);

		String cons = reviewResponse.getCons();
		review.setCons(cons);
		StringBuilder consLengthProp = new StringBuilder(adUnitIdentifier);
		consLengthProp.append(".");
		consLengthProp.append(CommonConstants.REVIEW_CONS_LENGTH);
		cons = Utils.getAbbreviatedString(cons, consLengthProp.toString());
		review.setShortCons(cons);

		review.setListingId(reviewResponse.getListingId());
		review.setReviewAuthor(reviewResponse.getAuthor());
		String ratingVal = reviewResponse.getRating();
		double rating = NumberUtils.toDouble(ratingVal) / 2;
		review.setRating(Utils.getRatingsList(ratingVal));
		review.setReviewRating(String.valueOf(rating));
		review.setReviewId(reviewResponse.getReviewId());
		review.setReviewUrl(reviewResponse.getReviewUrl());

		String rDateStr = reviewResponse.getReviewDate();
		SimpleDateFormat formatter = new SimpleDateFormat(PropertiesLoader
				.getAPIProperties().getProperty(DATE_FORMAT));
		Date date = Utils.parseDate(rDateStr, formatter);
		long now = Calendar.getInstance().getTimeInMillis();
		review.setTimeSinceReviewString(DurationFormatUtils
				.formatDurationWords(now - date.getTime(), true, true));

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		review.setReviewDate(df.format(date));

		return review;
	}

	private Review setProfileFieldsOnReview(RequestBean request, Review review,
			Profile profile) throws CitysearchException {
		review.setAddress(profile.getAddress());
		review.setPhone(profile.getPhone());
		review.setProfileUrl(profile.getProfileUrl());
		review.setSendToFriendUrl(profile.getSendToFriendUrl());
		review.setImageUrl(profile.getImageUrl());

		String sendToFriendTrackingUrl = Utils.getTrackingUrl(profile
				.getSendToFriendUrl(), null, request.getCallBackUrl(), request
				.getDartClickTrackUrl(), review.getListingId(), profile
				.getPhone(), request.getPublisher(), request.getAdUnitName(),
				request.getAdUnitSize());
		review.setSendToFriendTrackingUrl(sendToFriendTrackingUrl);

		String profileTrackingUrl = Utils.getTrackingUrl(profile
				.getProfileUrl(), null, request.getCallBackUrl(), request
				.getDartClickTrackUrl(), review.getListingId(), profile
				.getPhone(), request.getPublisher(), request.getAdUnitName(),
				request.getAdUnitSize());
		review.setProfileTrackingUrl(profileTrackingUrl);

		String adDisplayTrackingUrl = Utils.getTrackingUrl(review
				.getReviewUrl(), null, request.getCallBackUrl(), request
				.getDartClickTrackUrl(), review.getListingId(), profile
				.getPhone(), request.getPublisher(), request.getAdUnitName(),
				request.getAdUnitSize());
		review.setReviewTrackingUrl(adDisplayTrackingUrl);

		String callBackFn = Utils.getCallBackFunctionString(request
				.getCallBackFunction(), review.getListingId(), profile
				.getPhone());
		review.setCallBackFunction(callBackFn);

		return review;
	}

	public Review getLatestReview(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("ReviewHelper.getLatestReview:: before validate");
		request.validate();
		log.info("ReviewHelper.getLatestReview:: after validate");

		ReviewProxy proxy = new ReviewProxy();
		ReviewResponse reviewResponse = proxy.getLatestReview(request,
				MINIMUM_RATING);

		Review review = toReview(request, reviewResponse, contextPath, request
				.getAdUnitIdentifier());

		ProfileHelper profileHelper = new ProfileHelper(
				contextPath);
		Profile profile = profileHelper.getProfile(request);

		setProfileFieldsOnReview(request, review, profile);

		return review;
	}
}
