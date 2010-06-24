package com.citysearch.webwidget.bean;

/**
 * Request Bean for Profile API
 * 
 * @author Aspert Benjamin
 * 
 */
public class ProfileRequest extends AbstractRequest {
    private String listingId;
    private String infoUSAId;
    private String phone;
    private boolean allResults;
    private String reviewCount;
    private String placement;
    private String clientIP;
    private int nolog;
    private String callback;

    public ProfileRequest() {
        super();
    }

    public ProfileRequest(AbstractRequest request) {
        super();
        setPublisher(request.getPublisher());
        setDartClickTrackUrl(request.getDartClickTrackUrl());
        setCallBackFunction(request.getCallBackFunction());
        setCallBackUrl(request.getCallBackUrl());
        setAdUnitName(request.getAdUnitName());
        setAdUnitSize(request.getAdUnitSize());
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getInfoUSAId() {
        return infoUSAId;
    }

    public void setInfoUSAId(String infoUSAId) {
        this.infoUSAId = infoUSAId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAllResults() {
        return allResults;
    }

    public void setAllResults(boolean allResults) {
        this.allResults = allResults;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public int getNolog() {
        return nolog;
    }

    public void setNolog(int nolog) {
        this.nolog = nolog;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getCallback() {
        return callback;
    }

}
