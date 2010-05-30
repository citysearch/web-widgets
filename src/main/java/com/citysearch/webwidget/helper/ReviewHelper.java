package com.citysearch.webwidget.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class ReviewHelper {

	public final static String PROPERTY_REVIEW_URL = "reviews.url";

	private static final String ELEMENT_REVIEW_URL = "review_url";
	private static final String businessName = "business_name";
	private static final String listingId = "listing_id";
	private static final String reviewId = "review_id";
	private static final String reviewTitle = "review_title";
	private static final String reviewText = "review_text";
	private static final String pros = "pros";
	private static final String cons = "cons";
	private static final String reviewRating = "review_rating";
	private static final String reviewDate = "review_date";
	private static final String reviewAuthor = "review_author";
	private static final String dateFormat = "reviewdate.format";
	private static final String reviewElemName = "review";
	private static final int minRating = 6;

	private Logger log = Logger.getLogger(getClass());

	/**
	 * Constructs the Reviews API query string with all the supplied parameters
	 * 
	 * @return
	 * @throws CitysearchException
	 */
	private String getQueryString(ReviewRequest request)
			throws CitysearchException {
		// Reflection Probably???
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, request.getApiKey()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHERE, request.getWhere()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, request.getWhat()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.TAG_ID, request.getTagId()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.TAG_NAME, request.getTagName()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		if (request.isCustomerOnly()) {
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.CUSTOMER_ONLY, String.valueOf(request
							.isCustomerOnly())));
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		}
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.RATING, request.getRating()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.DAYS, request.getDays()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.MAX, request.getMax()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PLACEMENT, request.getPlacement()));
		return strBuilder.toString();
	}

	/**
	 * Validates the request. If any of the parameters are missing, throws
	 * Citysearch Exception
	 * 
	 * @throws CitysearchException
	 */
	public void validateRequest(ReviewRequest request)
			throws CitysearchException {
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();
		if (StringUtils.isBlank(request.getApiKey())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.API_KEY_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getPublisher())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getWhere())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.WHERE_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getClientIP())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.CLIENT_IP_ERROR_CODE));
		}
		if (!errors.isEmpty()) {
			throw new CitysearchException(this.getClass().getName(),
					"validateRequest", "Invalid parameters.", errors);
		}
	}

	/**
	 * Connects to the Reviews API and processes the response sent by API
	 * 
	 * @return
	 * @throws CitysearchException
	 */
	public Review getLatestReview(ReviewRequest request)
			throws CitysearchException {
		validateRequest(request);
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_REVIEW_URL)
				+ getQueryString(request);
		Document responseDocument = null;
		try {
			responseDocument = HelperUtil.getAPIResponse(urlString);
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getLatestReview", ihe.getMessage());
		}
		Review reviewObj = parseXML(responseDocument);
		if (reviewObj == null) {
			throw new CitysearchException(this.getClass().getName(),
					"getLatestReview", "No latest review found.");
		}
		return reviewObj;
	}

	/**
	 * Parses the Reviews xml. Returns Review object with values from api
	 * 
	 * @param doc
	 * @return
	 * @throws CitysearchException
	 */
	public Review parseXML(Document doc) throws CitysearchException {
		Review review = null;
		if (doc != null && doc.hasRootElement()) {
			Element rootElement = doc.getRootElement();
			List<Element> reviewsList = rootElement.getChildren(reviewElemName);
			SimpleDateFormat formatter = new SimpleDateFormat(PropertiesLoader
					.getAPIProperties().getProperty(dateFormat));
			SortedMap<Date, Review> reviewMap = new TreeMap<Date, Review>();
			for (int i = 0; i < reviewsList.size(); i++) {
				Element reviewElem = reviewsList.get(i);
				if (reviewElem != null) {
					String rating = reviewElem.getChildText(reviewRating);
					if (NumberUtils.toInt(rating) >= minRating) {
						String dateStr = reviewElem.getChildText(reviewDate);
						Date date = HelperUtil.parseDate(dateStr, formatter);
						if (date != null) {
							reviewMap.put(date, getReviewInstance(reviewElem));
						}
					}
				}
			}
			review = reviewMap.get(reviewMap.lastKey());
		}
		return review;
	}

	/**
	 * Parses the review element and set the required values in the Review bean
	 * 
	 * @param review
	 * @param reviewsElem
	 */
	private Review getReviewInstance(Element reviewElem)
			throws CitysearchException {
		Review review = new Review();
		review.setBusinessName(reviewElem.getChildText(businessName));
		review.setListingId(reviewElem.getChildText(listingId));
		review.setReviewTitle(reviewElem.getChildText(reviewTitle));
		review.setReviewAuthor(reviewElem.getChildText(reviewAuthor));
		review.setReviewText(reviewElem.getChildText(reviewText));
		review.setPros(reviewElem.getChildText(pros));
		review.setCons(reviewElem.getChildText(cons));
		String ratingVal = reviewElem.getChildText(reviewRating);
		double rating = NumberUtils.toDouble(ratingVal) / 2;
		review.setRating(HelperUtil.getRatingsList(ratingVal));
		review.setReviewRating(String.valueOf(rating));
		review.setReviewId(reviewElem.getChildText(reviewId));
		review.setReviewUrl(reviewElem.getChildText(ELEMENT_REVIEW_URL));

		String rDateStr = reviewElem.getChildText(reviewDate);
		SimpleDateFormat formatter = new SimpleDateFormat(PropertiesLoader
				.getAPIProperties().getProperty(dateFormat));
		Date date = HelperUtil.parseDate(rDateStr, formatter);
		long now = Calendar.getInstance().getTimeInMillis();
		review.setTimeSinceReviewString(DurationFormatUtils
				.formatDurationWords(now - date.getTime(), true, true));

		return review;
	}

}
