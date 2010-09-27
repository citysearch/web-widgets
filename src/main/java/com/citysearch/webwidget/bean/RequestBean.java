package com.citysearch.webwidget.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;

public class RequestBean {
	private Logger log = Logger.getLogger(getClass());
	protected String publisher;
	protected boolean customerOnly;
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
	protected String listingId;
	protected String tags;
	// Review Specific Params
	protected String tagId;
	protected String tagName;
	protected String rating;
	protected String days;
	protected String max;
	protected String placement;

	// Offer specific Params
	protected String version;
	protected String tag;
	protected String page;
	protected String rpp;
	protected String expiresBefore;
	protected String customerHasbudget;
	protected boolean rotation;

	public boolean isRotation() {
		return rotation;
	}

	public void setRotation(boolean rotation) {
		this.rotation = rotation;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

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

	public String getListingId() {
		return listingId;
	}

	public void setListingId(String listingId) {
		this.listingId = listingId;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getPlacement() {
		return placement;
	}

	public void setPlacement(String placement) {
		this.placement = placement;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getRpp() {
		return rpp;
	}

	public void setRpp(String rpp) {
		this.rpp = rpp;
	}

	public String getExpiresBefore() {
		return expiresBefore;
	}

	public void setExpiresBefore(String expiresBefore) {
		this.expiresBefore = expiresBefore;
	}

	public String getCustomerHasbudget() {
		return customerHasbudget;
	}

	public void setCustomerHasbudget(String customerHasbudget) {
		this.customerHasbudget = customerHasbudget;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * Method to validate required parameters for a request.
	 * 
	 * @throws CitysearchException
	 *             , InvalidRequestParametersException
	 */
	public void validate() throws InvalidRequestParametersException,
			CitysearchException {
		log.info("Start RequestBean validate()");
		List<String> errors = new ArrayList<String>();

		if (StringUtils.isBlank(getPublisher())) {
			errors.add("Publisher code is required.");
		}

		if (StringUtils.isBlank(getWhat())) {
			errors.add("What is required.");
		}

		if ((StringUtils.isBlank(getLatitude()) || StringUtils
				.isBlank(getLongitude()))
				&& StringUtils.isBlank(getWhere())) {
			errors
					.add("Where is required if latitude & longitude is not provided.");
		}
		if (!StringUtils.isBlank(getLatitude())
				&& StringUtils.isBlank(getLongitude())) {
			errors.add("Invalid longitude.");
		} else if (StringUtils.isBlank(getLatitude())
				&& !StringUtils.isBlank(getLongitude())) {
			errors.add("Invalid latitude.");
		}

		if (StringUtils.isBlank(getClientIP())) {
			errors.add("Client IP is required.");
		}
		if (!errors.isEmpty()) {
			throw new InvalidRequestParametersException(this.getClass()
					.getName(), "RequestBean.validate()",
					"Invalid parameters.", errors);
		}
		log.info("End RequestBean validate()");
	}

	public String getAdUnitIdentifier() {
		StringBuilder str = new StringBuilder();
		str.append(adUnitName);
		str.append(".");
		str.append(adUnitSize);
		return str.toString().toUpperCase();
	}

	public String getPlacementString() {
		StringBuilder str = new StringBuilder();
		str.append(publisher);
		str.append("_");
		str.append("contentads");
		str.append("_");
		str.append(adUnitName);
		str.append("_");
		str.append(adUnitSize);
		return str.toString().toLowerCase();
	}
}
