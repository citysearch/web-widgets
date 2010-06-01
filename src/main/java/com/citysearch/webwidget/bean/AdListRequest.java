package com.citysearch.webwidget.bean;

public class AdListRequest extends AbstractRequest {
	private String what;
	private String where;
	private String sourceLat;
	private String sourceLon;
	private String tags;
	private String radius;
	public String getWhat() {
		return what;
	}
	public void setWhat(String what) {
		this.what = what;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	public String getSourceLat() {
		return sourceLat;
	}
	public void setSourceLat(String sourceLat) {
		this.sourceLat = sourceLat;
	}
	public String getSourceLon() {
		return sourceLon;
	}
	public void setSourceLon(String sourceLon) {
		this.sourceLon = sourceLon;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	
}
