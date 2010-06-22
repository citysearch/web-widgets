package com.citysearch.webwidget.bean;

/**
 * The abstract class that contains the common Request field across APIs
 * 
 * @author Aspert Benjamin
 * 
 */
public abstract class AbstractRequest {
    protected String publisher;
    protected boolean customerOnly;
    protected String format;
    protected String adUnitName;
    protected String adUnitSize;
    protected Integer displaySize;
    protected String clientIP;
    protected String dartClickTrackUrl;

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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
}
