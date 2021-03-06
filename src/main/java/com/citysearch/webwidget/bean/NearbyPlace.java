package com.citysearch.webwidget.bean;

import java.util.List;

/**
 * This class contains all the fields required for PFP API response and implements sorting method
 * for sorting the results from API
 * 
 * @author Aspert Benjamin
 * 
 */
public class NearbyPlace implements Comparable<NearbyPlace> {
    private String name;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String location;
    private int reviewCount;
    private double ratings;
    private double distance;
    private String listingId;
    private String category;
    private List<Integer> rating;
    private String adDisplayURL;
    private String adImageURL;
    private String phone;
    private String callBackFunction;
    private String callBackUrl;
    private String listingUrl;
    private String offers;
    private String description;
    private String adDestinationUrl;
    private Profile profile;
    private String adDisplayTrackingURL;
    private String adDestinationTrackingUrl;
    private String businessUrl;
    private String businessTrackingUrl;

    public String getBusinessUrl() {
        return businessUrl;
    }

    public void setBusinessUrl(String businessUrl) {
        this.businessUrl = businessUrl;
    }

    public String getBusinessTrackingUrl() {
        return businessTrackingUrl;
    }

    public void setBusinessTrackingUrl(String businessTrackingUrl) {
        this.businessTrackingUrl = businessTrackingUrl;
    }

    public String getAdDisplayTrackingURL() {
        return adDisplayTrackingURL;
    }

    public void setAdDisplayTrackingURL(String adDisplayTrackingURL) {
        this.adDisplayTrackingURL = adDisplayTrackingURL;
    }

    public String getAdDestinationTrackingUrl() {
        return adDestinationTrackingUrl;
    }

    public void setAdDestinationTrackingUrl(String adDestinationTrackingUrl) {
        this.adDestinationTrackingUrl = adDestinationTrackingUrl;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Integer> getRating() {
        return rating;
    }

    public void setRating(List<Integer> rating) {
        this.rating = rating;
    }

    public String getListingUrl() {
        return listingUrl;
    }

    public void setListingUrl(String listingUrl) {
        this.listingUrl = listingUrl;
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

    public NearbyPlace() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public double getRatings() {
        return ratings;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getListingId() {
        return listingId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setAdDisplayURL(String adDisplayURL) {
        this.adDisplayURL = adDisplayURL;
    }

    public String getAdDisplayURL() {
        return adDisplayURL;
    }

    public void setAdImageURL(String adImageURL) {
        this.adImageURL = adImageURL;
    }

    public String getAdImageURL() {
        return adImageURL;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public boolean getIsValidCallbackFunction() {
        if (this.callBackFunction != null && this.callBackFunction.trim().length() > 0) {
            return true;
        }
        return false;
    }

    public boolean getIsValidLocation() {
        if (this.location != null && this.location.trim().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Sorts the objects based on ratings first.If,ratings are equal then sorts on distance.If
     * distance is also equal then sorts on Number of Reviews
     */
    public int compareTo(NearbyPlace beanTwo) {
        int result = Double.compare(beanTwo.getRatings(), this.getRatings());
        if (result == 0) {
            result = Double.compare(this.getDistance(), beanTwo.getDistance());
            if (result == 0) {
                Integer beanTwoCount = Integer.valueOf(beanTwo.getReviewCount());
                Integer beanOneCount = Integer.valueOf(this.getReviewCount());
                beanTwoCount.compareTo(beanOneCount);
            }
        }
        return result;
    }

    public void setOffers(String offers) {
        this.offers = offers;
    }

    public String getOffers() {
        return offers;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getAdDestinationUrl() {
        return adDestinationUrl;
    }

    public void setAdDestinationUrl(String adDestinationUrl) {
        this.adDestinationUrl = adDestinationUrl;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
