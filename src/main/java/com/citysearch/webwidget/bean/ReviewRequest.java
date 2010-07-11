package com.citysearch.webwidget.bean;

/**
 * Request bean for Reviews API
 * 
 * @author Aspert Benjamin
 * 
 */
public class ReviewRequest extends AbstractRequest {
    private String tagId;
    private String tagName;
    private String rating;
    private String days;
    private String max;
    private String placement;
    private boolean customerOnly;

    public ReviewRequest() {
        super();
    }

    public ReviewRequest(AbstractRequest request) {
        super();
        setPublisher(request.getPublisher());
        setDartClickTrackUrl(request.getDartClickTrackUrl());
        setCallBackFunction(request.getCallBackFunction());
        setCallBackUrl(request.getCallBackUrl());
        setAdUnitName(request.getAdUnitName());
        setAdUnitSize(request.getAdUnitSize());
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

    public boolean isCustomerOnly() {
        return customerOnly;
    }

    public void setCustomerOnly(boolean customerOnly) {
        this.customerOnly = customerOnly;
    }
}
