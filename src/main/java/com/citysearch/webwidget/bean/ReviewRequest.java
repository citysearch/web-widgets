package com.citysearch.webwidget.bean;
//TODO: javadocs
public class ReviewRequest {
	private String where;
	private String what;
	private String tag_id;
	private String tag_name;
	private String publisher;
	private String api_key;
	private boolean customer_only;
	private int rating;
	private int days;
	private int max;
	private String placement;
	
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
	public String getTag_id() {
		return tag_id;
	}
	public void setTag_id(String tagId) {
		tag_id = tagId;
	}
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tagName) {
		tag_name = tagName;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getApi_key() {
		return api_key;
	}
	public void setApi_key(String apiKey) {
		api_key = apiKey;
	}
	public boolean isCustomer_only() {
		return customer_only;
	}
	public void setCustomer_only(boolean customerOnly) {
		customer_only = customerOnly;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public String getPlacement() {
		return placement;
	}
	public void setPlacement(String placement) {
		this.placement = placement;
	}
}
