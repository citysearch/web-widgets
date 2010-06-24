package com.citysearch.webwidget.bean;

import java.util.List;

public class ConquestAdOffer{
	
    private String attributionSrc;
    private List<Integer> listingRating;
    private int reviewCount;    
    private String  profileUrl;    
    private String  profilePhone; 
	private String imgUrl;
    private String listingId;
    private String listingName;
    private String offerId;        
	private String offerTitle;
	private String offerDescription;	
	private String refId;
    private String latitude;
    private String longitude;    
    private String street;
    private String city;
    private String state;
    private String zip;    
    private String distance;

   
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


	public String getProfilePhone() {
		return profilePhone;
	}


	public void setProfilePhone(String profilePhone) {
		this.profilePhone = profilePhone;
	}


	public String getImgUrl() {
		return imgUrl;
	}


	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
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


	public String getRefId() {
		return refId;
	}


	public void setRefId(String refId) {
		this.refId = refId;
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


	public String getDistance() {
		return distance;
	}


	public void setDistance(String distance) {
		this.distance = distance;
	}


	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("==reviewCount==" + this.reviewCount );
		sb.append("==attributionSrc===" + this.attributionSrc );
		sb.append("==csRating==" + this.listingRating );
		sb.append("==imgUrl==" + this.imgUrl );
		sb.append("=listingId===" + this.listingId );
		sb.append("=listingName===" + this.listingName );
		sb.append("==offerId==" + this.offerId );
		sb.append("==offerTtl==" + this.offerTitle );
		sb.append("==offerDesc==" + this.offerDescription );
		sb.append("==refId==" + this.refId );
		sb.append("==lat==" + this.latitude );
		sb.append("==longs==" + this.longitude );
		sb.append("==street==" + this.street );
		sb.append("==city==" + this.city );
		sb.append("==state==" + this.state );
		sb.append("==zip==" + this.zip );	
		sb.append("==distance==" + this.distance );	

		return sb.toString();
	}
}
