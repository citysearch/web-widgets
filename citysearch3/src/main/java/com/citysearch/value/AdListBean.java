package com.citysearch.value;

public class AdListBean 
{
	
	private String name;
	private String location;
	private int reviewCount;
	private double ratings;
	private double distance;
	private String listingId;
	private String category;
	private int[] rating;
	private String adDisplayURL;
	private String adImageURL;
	private String phone;

	public AdListBean(){
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setLocation(String location){
		this.location = location;
	}

	public String getLocation(){
		return location;
	}

	public void setRating(int[] rating){
		this.rating = rating;
	}

	public int[] getRating(){
		return rating;
	}
	
	public void setRatings(double ratings){
		this.ratings = ratings;
	}

	public double getRatings(){
		return ratings;
	}

	public void setReviewCount(int reviewCount){
		this.reviewCount = reviewCount;
	}

	public int getReviewCount(){
		return reviewCount;
	}

	public void setDistance(double distance){
		this.distance = distance;
	}

	public double getDistance(){
		return distance;
	}

	public void setListingId(String listingId){
		this.listingId = listingId;
	}

	public String getListingId(){
		return listingId;
	}
	
	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
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
	
	
}
