package com.citysearch.webwidget.api.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
	private BigDecimal latitude;
	private BigDecimal longitude;
	private List<SearchLocation> locations;

	public SearchResponse() {
		locations = new ArrayList<SearchLocation>();
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public List<SearchLocation> getLocations() {
		return locations;
	}

	public void setLocations(List<SearchLocation> locations) {
		this.locations = locations;
	}
}
