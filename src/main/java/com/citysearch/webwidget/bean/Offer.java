package com.citysearch.webwidget.bean;

import java.util.List;

public class Offer{
	
    private String attributionSrc;
    private List<Integer> csRating;
    private int reviewCount;    
    private String  profileUrl;    
    private String  phone; 
	private String imgUrl;
    private String listingId;
    private String listingName;
    private String offerId;
    
    private String offerTitle;
	private String offerDesc;	
	private String fullOfferTitle;
	private String fullOfferDesc;
   
	private String refId;
    private String lat;
    private String longs;    
    private String street;
    private String city;
    private String state;
    private String zip;

    public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
    public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
    public List<Integer> getCsRating() {
		return csRating;
	}
	public void setCsRating(List<Integer> csRating) {
		this.csRating = csRating;
	}	
	public int getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}    
	public String getAttributionSrc() {
		return attributionSrc;
	}
	public void setAttributionSrc(String attributionSrc) {
		this.attributionSrc = attributionSrc;
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
	public String getOfferDesc() {
		if( offerDesc != null && offerDesc.length() > 36 )
			offerDesc = offerDesc.substring(0, 36) + "...";
		
		return offerDesc;
	}
	public void setOfferDesc(String offerDesc) {
		this.offerDesc = offerDesc;
	}
	public String getOfferId() {
		return offerId;
	}
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}
	public String getFullOfferTitle() {
		return fullOfferTitle;
	}
	public void setFullOfferTitle(String fullOfferTitle) {
		this.fullOfferTitle = fullOfferTitle;
	}
	public String getFullOfferDesc() {
		return fullOfferDesc;
	}
	public void setFullOfferDesc(String fullOfferDesc) {
		this.fullOfferDesc = fullOfferDesc;
	}
	public String getOfferTitle() {
		if( offerTitle != null && offerTitle.length() > 20 )
			offerTitle = offerTitle.substring(0, 20) + "...";
		
		return offerTitle;
	}
	public void setOfferTitle(String offerTitle) {
		this.offerTitle = offerTitle;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getLat() {
		return lat;
	}
	public void setLatitude(String lat) {
		this.lat = lat;
	}
	public String getLongs() {
		return longs;
	}
	public void setLongs(String longs) {
		this.longs = longs;
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
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("==reviewCount==" + reviewCount );
		sb.append("==attributionSrc===" + attributionSrc );
		sb.append("==csRating==" + csRating );
		sb.append("==imgUrl==" + imgUrl );
		sb.append("=listingId===" + listingId );
		sb.append("=listingName===" + listingName );
		sb.append("==offerId==" + offerId );
		sb.append("==offerTtl==" + offerTitle );
		sb.append("==offerDesc==" + offerDesc );
		sb.append("==offerTtl==" + fullOfferTitle );
		sb.append("==offerDesc==" + fullOfferDesc );		
		sb.append("==refId==" + refId );
		sb.append("==lat==" + lat );
		sb.append("==longs==" + longs );
		sb.append("==street==" + street );
		sb.append("==city==" + city );
		sb.append("==state==" + state );
		sb.append("==zip==" + zip );	
		
		return sb.toString();
	}
}