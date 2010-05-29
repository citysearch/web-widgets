package com.citysearch.webwidget.bean;

//TODO: javadocs
public class ReviewRequest extends AbstractRequest {
	
	private String where;
	private String what;
	private String tagId;
	private String tagName;
	private int rating;
	private int days;
	private int max;
	private String placement;
	private boolean customerOnly;
	private String tag_name;
	
	public boolean isCustomerOnly() {
		return customerOnly;
	}

	public void setCustomerOnly(boolean customerOnly) {
		this.customerOnly = customerOnly;
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

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
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

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTag_name() {
        return tag_name;
    }
}
