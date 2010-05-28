package com.citysearch.webwidget.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;

import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.bean.ReviewResponse;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

public class ReviewHelper extends AbstractHelper {
	
	public final static String PROPERTY_REVIEW_URL = "reviews.url";
	
	private ReviewRequest request;

	public ReviewHelper(ReviewRequest request) {
		this.request = request;
	}
	
	private String getQueryString() throws CitysearchException
	{
		//Reflection Probably???
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(constructQueryParam(APIFieldNameConstants.API_KEY, request.getApiKey()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.WHERE, request.getWhere()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.WHAT, request.getWhat()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.TAG_ID, request.getTagId()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.TAG_NAME, request.getTagName()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.CUSTOMER_ONLY, String.valueOf(request.isCustomerOnly())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.RATING, String.valueOf(request.getRating())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.DAYS, String.valueOf(request.getDays())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.MAX, String.valueOf(request.getMax())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.PLACEMENT, request.getPlacement()));
		return strBuilder.toString();
	}
	
	public void validateRequest() throws CitysearchException {
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();
		if (StringUtils.isBlank(request.getApiKey()))
		{
			errors.add(errorProperties.getProperty(CommonConstants.API_KEY_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getPublisher()))
		{
			errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getWhere()))
		{
			errors.add(errorProperties.getProperty(CommonConstants.WHERE_ERROR_CODE));
		}
		if (!errors.isEmpty())
		{
			throw new CitysearchException(this.getClass().getName(), "validateRequest", "Invalid parameters.", errors);
		}
	}

	public ReviewResponse getReviews() throws CitysearchException {
		validateRequest();
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_REVIEW_URL) + getQueryString();
		Document responseDocument = getAPIResponse(urlString);
		//Response processing here.
		return new ReviewResponse();
	}
}
