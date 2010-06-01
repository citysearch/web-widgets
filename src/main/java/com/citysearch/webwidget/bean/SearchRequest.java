package com.citysearch.webwidget.bean;

public class SearchRequest extends AbstractRequest {
	private String where;
	private String what;
	private String latitude;
	private String longitude;
	private String radius = "25";// TODO:
	private String rpp = "20";// TODO:
	private String tags;

	public String getLatitude() {
		return latitude;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
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

	public String getRpp() {
		return rpp;
	}

	public void setRpp(String rpp) {
		this.rpp = rpp;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

}
