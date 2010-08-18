package com.citysearch.webwidget.api.proxy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.api.bean.ReviewResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class ReviewProxy extends AbstractProxy {
	public final static String PROPERTY_REVIEW_URL = "reviews.url";
	private Logger log = Logger.getLogger(getClass());

	private static final String ELEMENT_REVIEW_URL = "review_url";
	private static final String BUSINESS_NAME = "business_name";
	private static final String LISTING_ID = "listing_id";
	private static final String REVIEW_ID = "review_id";
	private static final String REVIEW_TITLE = "review_title";
	private static final String REVIEW_TEXT = "review_text";
	private static final String PROS = "pros";
	private static final String CONS = "cons";
	private static final String REVIEW_RATING = "review_rating";
	private static final String REVIEW_DATE = "review_date";
	private static final String REVIEW_AUTHOR = "review_author";
	private static final String DATE_FORMAT = "reviewdate.format";
	private static final String REVIEW_ELEMENT = "review";

	protected String getQueryString(RequestBean request)
			throws CitysearchException {
		StringBuilder strBuilder = new StringBuilder(super
				.getQueryString(request));
		if (request.isCustomerOnly()) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.CUSTOMER_ONLY, String.valueOf(request
							.isCustomerOnly())));
		}
		if (!StringUtils.isEmpty(request.getRating())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.RATING, request.getRating()));
		}
		if (!StringUtils.isEmpty(request.getDays())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.DAYS, request.getDays()));
		}
		if (!StringUtils.isEmpty(request.getMax())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.MAX, request.getMax()));
		}
		if (!StringUtils.isEmpty(request.getPlacement())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.PLACEMENT, request.getPlacement()));
		}
		return strBuilder.toString();
	}

	private ReviewResponse parseXML(Document document, int minimumRating)
			throws CitysearchException {
		ReviewResponse review = null;
		if (document != null && document.hasRootElement()) {
			Element rootElement = document.getRootElement();
			List<Element> reviewsList = rootElement.getChildren(REVIEW_ELEMENT);
			SimpleDateFormat formatter = new SimpleDateFormat(PropertiesLoader
					.getAPIProperties().getProperty(DATE_FORMAT));
			SortedMap<Date, Element> reviewMap = new TreeMap<Date, Element>();
			for (int i = 0; i < reviewsList.size(); i++) {
				Element reviewElem = reviewsList.get(i);
				String rating = reviewElem.getChildText(REVIEW_RATING);
				if (NumberUtils.toInt(rating) >= minimumRating) {
					String dateStr = reviewElem.getChildText(REVIEW_DATE);
					Date date = HelperUtil.parseDate(dateStr, formatter);
					if (date != null) {
						reviewMap.put(date, reviewElem);
					}
				}
			}
			Element reviewElm = reviewMap.get(reviewMap.lastKey());
			review = toReviewResponse(reviewElm);
		}
		return review;
	}

	public static ReviewResponse toReviewResponse(Element reviewElem)
			throws CitysearchException {
		ReviewResponse review = new ReviewResponse();
		review.setBusinessName(reviewElem.getChildText(BUSINESS_NAME));
		review.setReviewTitle(reviewElem.getChildText(REVIEW_TITLE));
		review.setReviewText(reviewElem.getChildText(REVIEW_TEXT));
		review.setPros(reviewElem.getChildText(PROS));
		review.setCons(reviewElem.getChildText(CONS));
		review.setListingId(reviewElem.getChildText(LISTING_ID));
		review.setAuthor(reviewElem.getChildText(REVIEW_AUTHOR));
		review.setRating(reviewElem.getChildText(REVIEW_RATING));
		review.setReviewId(reviewElem.getChildText(REVIEW_ID));
		review.setReviewUrl(reviewElem.getChildText(ELEMENT_REVIEW_URL));
		review.setReviewDate(reviewElem.getChildText(REVIEW_DATE));
		return review;
	}

	public ReviewResponse getLatestReview(RequestBean request, int minimumRating)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("ReviewProxy.getLatestReview:: before validate");
		request.validate();
		log.info("ReviewProxy.getLatestReview:: after validate");
		// If Lat and Lon is set find the nearest postal code using search API
		// Reviews API does not support lat & lon directly
		if (!StringUtils.isBlank(request.getLatitude())
				&& !StringUtils.isBlank(request.getLongitude())) {
			log
					.info("ReviewHelper.getLatestReview:: Lat and Lon received. Find zip");
			SearchProxy searchApiProxy = new SearchProxy();
			String where = searchApiProxy.getClosestLocationPostalCode(request);
			request.setWhere(where);
			log.info("ReviewProxy.getLatestReview:: After finding zip");
		}
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_REVIEW_URL)
				+ getQueryString(request);
		log.info("ReviewProxy.getLatestReview:: Request URL " + urlString);
		Document responseDocument = null;
		try {
			responseDocument = HelperUtil.getAPIResponse(urlString, null);
			log
					.info("ReviewProxy.getLatestReview:: Successfull response received.");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getLatestReview", ihe);
		}
		ReviewResponse review = parseXML(responseDocument, minimumRating);
		return review;
	}
}
