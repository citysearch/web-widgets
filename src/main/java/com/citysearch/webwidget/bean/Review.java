package com.citysearch.webwidget.bean;

import java.util.List;

/**
 * Response bean for Reviews API
 * 
 * @author Aspert Benjamin
 * 
 */
public class Review {
    private String attributionSource;
    private String attributionLogo;
    private String attributionText;
    private String businessName;
    private String shortBusinessName;
    private String listingId;
    private String referenceId;
    private String reviewId;
    private String reviewUrl;
    private String reviewTitle;
    private String shortTitle;
    private String reviewAuthor;
    private String reviewAuthorUrl;
    private String reviewText;
    private String shortReviewText;
    private String smallReviewText;
    private String pros;
    private String shortPros;
    private String cons;
    private String shortCons;
    private String reviewSate;
    private String reviewRating;
    private String imageUrl;
    private String sendToFriendUrl;
    private String profileUrl;
    private Address address;
    private String phone;
    private List<Integer> rating;
    private String timeSinceReviewString;
    private String reviewDate;

    public String getSmallReviewText() {
        return smallReviewText;
    }

    public void setSmallReviewText(String smallReviewText) {
        this.smallReviewText = smallReviewText;
    }

    public String getShortBusinessName() {
        return shortBusinessName;
    }

    public void setShortBusinessName(String shortBusinessName) {
        this.shortBusinessName = shortBusinessName;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getShortPros() {
        return shortPros;
    }

    public void setShortPros(String shortPros) {
        this.shortPros = shortPros;
    }

    public String getShortCons() {
        return shortCons;
    }

    public void setShortCons(String shortCons) {
        this.shortCons = shortCons;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public List<Integer> getRating() {
        return rating;
    }

    public void setRating(List<Integer> rating) {
        this.rating = rating;
    }

    public String getAttributionSource() {
        return attributionSource;
    }

    public void setAttributionSource(String attributionSource) {
        this.attributionSource = attributionSource;
    }

    public String getAttributionLogo() {
        return attributionLogo;
    }

    public void setAttributionLogo(String attributionLogo) {
        this.attributionLogo = attributionLogo;
    }

    public String getAttributionText() {
        return attributionText;
    }

    public void setAttributionText(String attributionText) {
        this.attributionText = attributionText;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getShortReviewText() {
        return shortReviewText;
    }

    public void setShortReviewText(String shortReviewText) {
        this.shortReviewText = shortReviewText;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewAuthorUrl() {
        return reviewAuthorUrl;
    }

    public void setReviewAuthorUrl(String reviewAuthorUrl) {
        this.reviewAuthorUrl = reviewAuthorUrl;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getPros() {
        return pros;
    }

    public void setPros(String pros) {
        this.pros = pros;
    }

    public String getCons() {
        return cons;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public String getReviewSate() {
        return reviewSate;
    }

    public void setReviewSate(String reviewSate) {
        this.reviewSate = reviewSate;
    }

    public String getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(String reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSendToFriendUrl() {
        return sendToFriendUrl;
    }

    public void setSendToFriendUrl(String sendToFriendUrl) {
        this.sendToFriendUrl = sendToFriendUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTimeSinceReviewString() {
        return timeSinceReviewString;
    }

    public void setTimeSinceReviewString(String timeSinceReviewString) {
        this.timeSinceReviewString = timeSinceReviewString;
    }

}
