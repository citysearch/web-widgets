package com.citysearch.webwidget.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * The abstract class that contains the common Request field across APIs
 * 
 * @author Aspert Benjamin
 * 
 */
public abstract class AbstractRequest {
	private Logger log = Logger.getLogger(getClass());

	protected String publisher;
	protected boolean customerOnly;
	protected String format;
	protected String adUnitName;
	protected String adUnitSize;
	protected Integer displaySize;
	protected String clientIP;
	protected String dartClickTrackUrl;
	protected String callBackFunction;
	protected String callBackUrl;
	protected String where;
	protected String what;
	protected String latitude;
	protected String longitude;
	protected String radius;

	public String getCallBackFunction() {
		return callBackFunction;
	}

	public void setCallBackFunction(String callBackFunction) {
		this.callBackFunction = callBackFunction;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getDartClickTrackUrl() {
		return dartClickTrackUrl;
	}

	public void setDartClickTrackUrl(String dartClickTrackUrl) {
		this.dartClickTrackUrl = dartClickTrackUrl;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public boolean isCustomerOnly() {
		return customerOnly;
	}

	public void setCustomerOnly(boolean customerOnly) {
		this.customerOnly = customerOnly;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getAdUnitName() {
		return adUnitName;
	}

	public void setAdUnitName(String adUnitName) {
		this.adUnitName = adUnitName;
	}

	public String getAdUnitSize() {
		return adUnitSize;
	}

	public void setAdUnitSize(String adUnitSize) {
		this.adUnitSize = adUnitSize;
	}

	public Integer getDisplaySize() {
		return displaySize;
	}

	public void setDisplaySize(Integer displaySize) {
		this.displaySize = displaySize;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	/**
	 * Method to validate required parameters for a request.
	 * 
	 * @throws CitysearchException
	 *             , InvalidRequestParametersException
	 */
	public void validate() throws InvalidRequestParametersException,
			CitysearchException {
		log.info("Start AbstractRequest validate()");
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();

		if (StringUtils.isBlank(getPublisher())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}

		if (StringUtils.isBlank(getWhat())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.WHAT_ERROR_CODE));
		}

		if ((StringUtils.isBlank(getLatitude()) || StringUtils
				.isBlank(getLongitude()))
				&& StringUtils.isBlank(getWhere())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.WHERE_ERROR_CODE));
		}
		if (!StringUtils.isBlank(getLatitude())
				&& StringUtils.isBlank(getLongitude())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.LONGITUDE_ERROR));
		} else if (StringUtils.isBlank(getLatitude())
				&& !StringUtils.isBlank(getLongitude())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.LATITUDE_ERROR));
		}

		if (StringUtils.isBlank(getClientIP())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.CLIENT_IP_ERROR_CODE));
		}
		if (!errors.isEmpty()) {
			throw new InvalidRequestParametersException(this.getClass()
					.getName(), "AbstractRequest.validate()",
					"Invalid parameters.", errors);
		}
		log.info("End AbstractRequest validate()");
	}

	public String getQueryString() throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();
		// Don't add the publisher param here. Because Some API's requires
		// publishercode and some requires publisher.
		// Let the helper handle it.
		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, getWhat()));

		if (!StringUtils.isBlank(getLatitude())
				&& !StringUtils.isBlank(getLongitude())) {
			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.LATITUDE, getLatitude()));

			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.LONGITUDE, getLongitude()));

			String radius = (StringUtils.isBlank(getRadius())) ? String
					.valueOf(CommonConstants.DEFAULT_RADIUS) : getRadius();
			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.RADIUS, radius));
		} else {
			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.WHERE, getWhere()));
		}
		return apiQueryString.toString();
	}
	
	public String getAdUnitIdentifier()
	{
		StringBuilder str = new StringBuilder();
		str.append(adUnitName);
		str.append(".");
		str.append(adUnitSize);
		return str.toString().toUpperCase();
	}
}
