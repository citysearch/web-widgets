package com.citysearch.webwidget.bean;

//TODO: javadocs
public class Review {
	private String attribution_source;
	private String attribution_logo;
	private String attribution_text;
	private String business_name;
	private String listing_id;
	private String reference_id;
	private String review_id;
	private String review_url;
	private String review_title;
	private String review_author;
	private String review_author_url;
	private String review_text;
	private String pros;
	private String cons;
	private String review_date;
	private String review_rating;
	private String image_url;
	private String send_to_friend_url;
	private String profile_url;
	private Address address;
	private String phone;
	private int[] rating;
	
	public String getAttribution_source() {
		return attribution_source;
	}
	public void setAttribution_source(String attributionSource) {
		attribution_source = attributionSource;
	}
	public String getAttribution_logo() {
		return attribution_logo;
	}
	public void setAttribution_logo(String attributionLogo) {
		attribution_logo = attributionLogo;
	}
	public String getAttribution_text() {
		return attribution_text;
	}
	public void setAttribution_text(String attributionText) {
		attribution_text = attributionText;
	}
	public String getBusiness_name() {
		return business_name;
	}
	public void setBusiness_name(String businessName) {
		business_name = businessName;
	}
	public String getListing_id() {
		return listing_id;
	}
	public void setListing_id(String listingId) {
		listing_id = listingId;
	}
	public String getReference_id() {
		return reference_id;
	}
	public void setReference_id(String referenceId) {
		reference_id = referenceId;
	}
	public String getReview_id() {
		return review_id;
	}
	public void setReview_id(String reviewId) {
		review_id = reviewId;
	}
	public String getReview_url() {
		return review_url;
	}
	public void setReview_url(String reviewUrl) {
		review_url = reviewUrl;
	}
	public String getReview_title() {
		return review_title;
	}
	public void setReview_title(String reviewTitle) {
		review_title = reviewTitle;
	}
	public String getReview_author() {
		return review_author;
	}
	public void setReview_author(String reviewAuthor) {
		review_author = reviewAuthor;
	}
	public String getReview_author_url() {
		return review_author_url;
	}
	public void setReview_author_url(String reviewAuthorUrl) {
		review_author_url = reviewAuthorUrl;
	}
	public String getReview_text() {
		return review_text;
	}
	public void setReview_text(String reviewText) {
		review_text = reviewText;
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
	public String getReview_date() {
		return review_date;
	}
	public void setReview_date(String reviewDate) {
		review_date = reviewDate;
	}
	public String getReview_rating() {
		return review_rating;
	}
	public void setReview_rating(String reviewRating) {
		review_rating = reviewRating;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public void setSend_to_friend_url(String send_to_friend_url) {
		this.send_to_friend_url = send_to_friend_url;
	}
	public String getSend_to_friend_url() {
		return send_to_friend_url;
	}
	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}
	public String getProfile_url() {
		return profile_url;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public Address getAddress() {
		return address;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhone() {
		return phone;
	}
	public void setRating(int[] rating) {
		this.rating = rating;
	}
	public int[] getRating() {
		return rating;
	}
	
}
