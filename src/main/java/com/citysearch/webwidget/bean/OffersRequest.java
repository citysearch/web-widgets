package com.citysearch.webwidget.bean;

public class OffersRequest extends AbstractRequest {
    private String what;
    private String where;
    private String tag;
    private String latitude;
    private String longitude;
    private String page;
    private String rpp;
    private String expires_before;
    private boolean customer_hasbudget;
    private String radius;
    private String callback;

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
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
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
    public String getExpires_before() {
        return expires_before;
    }
    public void setExpires_before(String expiresBefore) {
        expires_before = expiresBefore;
    }
    public boolean isCustomer_hasbudget() {
        return customer_hasbudget;
    }
    public void setCustomer_hasbudget(boolean customerHasbudget) {
        customer_hasbudget = customerHasbudget;
    }
    public String getRadius() {
        return radius;
    }
    public void setRadius(String radius) {
        this.radius = radius;
    }
    public String getCallback() {
        return callback;
    }
    public void setCallback(String callback) {
        this.callback = callback;
    }
}
