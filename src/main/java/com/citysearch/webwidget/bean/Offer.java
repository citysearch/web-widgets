package com.citysearch.webwidget.bean;

import java.util.List;

public class Offer {

    private String attributionSrc;
    private List<Integer> listingRating;
    private int reviewCount;
    private String profileUrl;
    private String profileTrackingUrl;
    private String phone;
    private String imageUrl;
    private String listingId;
    private String listingName;
    private String offerId;
    private String offerDescription;
    private String offerTitle;
    private String offerShortDescription;
    private String offerShortTitle;
    private String referenceId;
    private String latitude;
    private String longitude;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String callBackFunction;
    private String callBackUrl;
    private String couponUrl;
    private String distance;
    private String location;
    private Profile profile;
    
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCouponUrl() {
        return couponUrl;
    }

    public void setCouponUrl(String couponUrl) {
        this.couponUrl = couponUrl;
    }

    public String getCallBackFunction() {
        return callBackFunction;
    }

    public void setCallBackFunction(String callBackFunction) {
        this.callBackFunction = callBackFunction;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getProfileTrackingUrl() {
        return profileTrackingUrl;
    }

    public void setProfileTrackingUrl(String profileTrackingUrl) {
        this.profileTrackingUrl = profileTrackingUrl;
    }

    public String getAttributionSrc() {
        return attributionSrc;
    }

    public void setAttributionSrc(String attributionSrc) {
        this.attributionSrc = attributionSrc;
    }

    public List<Integer> getListingRating() {
        return listingRating;
    }

    public void setListingRating(List<Integer> listingRating) {
        this.listingRating = listingRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getListingName() {
        return listingName;
    }

    public void setListingName(String listingName) {
        this.listingName = listingName;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getOfferShortDescription() {
        if (offerShortDescription != null && offerShortDescription.length() > 36)
            offerShortDescription = offerShortDescription.substring(0, 36) + "...";

        return offerShortDescription;
    }

    public void setOfferShortDescription(String offerShortDescription) {
        this.offerShortDescription = offerShortDescription;
    }

    public String getOfferShortTitle() {
        if (offerShortTitle != null && offerShortTitle.length() > 20)
            offerShortTitle = offerShortTitle.substring(0, 20) + "...";

        return offerShortTitle;
    }

    public void setOfferShortTitle(String offerShortTitle) {
        this.offerShortTitle = offerShortTitle;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("=reviewCount=" + reviewCount);
        sb.append("=attributionSrc=" + attributionSrc);
        sb.append("=csRating=" + listingRating);
        sb.append("=imgUrl=" + imageUrl);
        sb.append("=listingId=" + listingId);
        sb.append("=listingName=" + listingName);
        sb.append("=offerDesc=" + offerDescription);
        sb.append("=offerId=" + offerId);
        sb.append("=offerTtl=" + offerTitle);
        sb.append("=refId=" + referenceId);
        sb.append("=lat=" + latitude);
        sb.append("=longs=" + longitude);
        sb.append("=street=" + street);
        sb.append("=city=" + city);
        sb.append("=state=" + state);
        sb.append("=zip=" + zip);
        sb.append("=distance=" + distance);

        return sb.toString();
    }
}
