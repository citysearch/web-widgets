package com.citysearch.webwidget.bean;

/**
 * Request object for PFP API
 * 
 * @author Aspert Benjamin
 * 
 */
public class NearbyPlacesRequest extends AbstractRequest {
	private String tags;
	private boolean validUrl;
	private boolean includeSearch;

	public NearbyPlacesRequest() {
		super();
	}

	public NearbyPlacesRequest(AbstractRequest request) {
		super();
		setPublisher(request.getPublisher());
		setDartClickTrackUrl(request.getDartClickTrackUrl());
		setCallBackFunction(request.getCallBackFunction());
		setCallBackUrl(request.getCallBackUrl());
		setAdUnitName(request.getAdUnitName());
		setAdUnitSize(request.getAdUnitSize());
	}

	public boolean isIncludeSearch() {
		return includeSearch;
	}

	public void setIncludeSearch(boolean includeSearch) {
		this.includeSearch = includeSearch;
	}

	public boolean isValidUrl() {
		return validUrl;
	}

	public void setValidUrl(boolean validUrl) {
		this.validUrl = validUrl;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
