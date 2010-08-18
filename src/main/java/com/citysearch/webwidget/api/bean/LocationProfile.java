package com.citysearch.webwidget.api.bean;

public class LocationProfile {
	private String listingId;
	private String street;
	private String city;
	private String state;
	private String postalCode;
	private String phone;
	private String reviewCount;
	private String imageUrl;
	private String profileUrl;
	private String sendToFriendUrl;
	private String reviewsUrl;
	private String websiteUrl;
	private String menuUrl;
	private String mapUrl;
	private String reservationUrl;

	private ReviewResponse review;

	public String getListingId() {
		return listingId;
	}

	public void setListingId(String listingId) {
		this.listingId = listingId;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(String reviewCount) {
		this.reviewCount = reviewCount;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getSendToFriendUrl() {
		return sendToFriendUrl;
	}

	public void setSendToFriendUrl(String sendToFriendUrl) {
		this.sendToFriendUrl = sendToFriendUrl;
	}

	public String getReviewsUrl() {
		return reviewsUrl;
	}

	public void setReviewsUrl(String reviewsUrl) {
		this.reviewsUrl = reviewsUrl;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getMapUrl() {
		return mapUrl;
	}

	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}

	public String getReservationUrl() {
		return reservationUrl;
	}

	public void setReservationUrl(String reservationUrl) {
		this.reservationUrl = reservationUrl;
	}

	public ReviewResponse getReview() {
		return review;
	}

	public void setReview(ReviewResponse review) {
		this.review = review;
	}
}
