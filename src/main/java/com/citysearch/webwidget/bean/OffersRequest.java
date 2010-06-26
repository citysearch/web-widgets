package com.citysearch.webwidget.bean;

public class OffersRequest extends AbstractRequest {

    private String version;
    private String placement;

    private String what;
    private String where;
    private String tag;
    private String latitude;
    private String longitude;
    private String page;
    private String rpp;
    private String expiresBefore;
    private String customerHasbudget;
    private String radius;
    private String callbackFunction;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

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

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getCallbackFunction() {
        return callbackFunction;
    }

    public void setCallbackFunction(String callbackFunction) {
        this.callbackFunction = callbackFunction;
    }
}
