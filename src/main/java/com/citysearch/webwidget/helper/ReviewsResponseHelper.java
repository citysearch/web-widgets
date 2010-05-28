package com.citysearch.webwidget.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Address;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.PropertiesLoader;

public class ReviewsResponseHelper extends ResponseHelper{

    private Logger log = Logger.getLogger(getClass());
    private static final String name = "name";
    private static final String listingId = "listing_id";
    private static final String businessListingId = "id";
    private static final String reviewId = "review_id";
    private static final String reviewTitle = "review_title";
    private static final String reviewText = "review_text";
    private static final String pros = "pros";
    private static final String cons = "cons";
    private static final String reviewRating = "review_rating";
    private static final String reviewDate = "review_date";
    private static final String reviewAuthor = "review_author";
    private static final String dateFormat = "reviewdate.format";
    private static final String invalidDate = "invalid.date";
    private static final String street = "street";
    private static final String city = "city";
    private static final String state = "state";
    private static final String postalCode = "postal_code";
    private static final String address = "address";
    private static final String contactInfo = "contact_info";
    private static final String phone = "display_phone";
    private static final String urls = "urls";
    private static final String profileURL = "profile_url";
    private static final String sendToFriendURL = "send_to_friend_url";
    private static final String images = "images";
    private static final String image = "image";
    private static final String imageURL = "image_url";
    private static final String reviews = "reviews";
    private static final String review = "review";
    private static final int minRating = 6;
    
    /**
     * Parses the Reviews xml. Returns string array with listing_id and review_id 
     * with latest date
     * @param doc
     * @return
     * @throws CitysearchException
     */
    public String[] parseXML(Document doc) throws CitysearchException{
    	String values[] = null;
    	if (doc != null && doc.hasRootElement()) {
	        Element rootElement = doc.getRootElement();
	        if(rootElement != null){
	            List<Element> reviewsList = rootElement.getChildren();
	            SimpleDateFormat formatter  = new SimpleDateFormat(PropertiesLoader.getAPIProperties().getProperty(dateFormat));
	            SortedMap<Date,String[]> reviewMap = new TreeMap<Date,String[]>();
	            for(int i = 0;i < reviewsList.size();i++){
	                Element reviewElem = reviewsList.get(i);
	                if(reviewElem != null){
	                	String rating = reviewElem.getChildText(reviewRating);
	                	if(NumberUtils.toInt(rating) >= minRating){
		                	String dateStr = reviewElem.getChildText(reviewDate);
		                	Date date = parseDate(dateStr, formatter);
		                	if(date != null){
		                		values = new String[2];
		                		values[0] = reviewElem.getChildText(listingId);
		                		values[1] = reviewElem.getChildText(reviewId);
		                		reviewMap.put(date, values);
		                	}
	                	}
	                 }
	            }
	            values = reviewMap.get(reviewMap.lastKey());
	        }
    	}
        return values;
    }
    
    /**
     * Parses the Review Element and creates value object
     * @param reviewElem
     * @throws CitysearchException 
     */
    private Date parseDate(String dateStr, SimpleDateFormat formatter) throws CitysearchException {
    	Date date = null;
    	try {
    		date = formatter.parse(dateStr);
		} catch (ParseException excep) {
			String message = PropertiesLoader.getErrorProperties().getProperty(invalidDate);
			log.error(message, excep);
			throw new CitysearchException(null, null);
		}
       return date; 
    }

    public Review getReviewDetails(Document doc, String reviewIdVal){
    	Review review = null;
    	if(doc != null && doc.hasRootElement()){
    		Element locationElem = doc.getRootElement().getChild(locationTag);
    		if(locationElem != null){
    			review = new Review();
    			review.setListing_id(locationElem.getChildText(businessListingId));
    			review.setBusiness_name(locationElem.getChildText(name));
    			review.setAddress(getAddress(locationElem.getChild(address)));
    			review.setPhone(getPhone(locationElem.getChild(contactInfo)));
    			Element url = locationElem.getChild(urls);
    			if(url != null){
    				review.setProfile_url(url.getChildText(profileURL));
    				review.setSend_to_friend_url(url.getChildText(sendToFriendURL));
    			}
    			review.setImage_url(getImage(locationElem.getChild(images)));
    			Element reviewsElem = locationElem.getChild(reviews);
    			review = processReviews(review, reviewsElem, reviewIdVal);
    		}
    	}
    	return review;
    }

    private Address getAddress(Element addressElem){
    	Address address = null;
    	if(addressElem != null){
    		address = new Address();
    		address.setStreet(addressElem.getChildText(street));
    		address.setCity(addressElem.getChildText(city));
    		address.setState(addressElem.getChildText(state));
    		address.setPostal_code(addressElem.getChildText(postalCode));
    	}
    	return address;
    }
    
    private String getPhone(Element contactInfo) {
		if(contactInfo != null){
			return contactInfo.getChildText(phone);
		}
		return null;
	}
    
    private String getImage(Element images) {
    	String imageurl = null;
    	if(images != null){
			List<Element> imageList = images.getChildren(image);
			int size = imageList.size();
			for(int index = 0;index < size; index++){
				Element image = imageList.get(index);
				if(image != null){
					imageurl = image.getChildText(imageURL);
					if(StringUtils.isNotBlank(imageurl)){
						break;
					}
				}
			}
	    }
    	if(StringUtils.isBlank(imageurl)){
    		//Add code for fetching stock photo
    	}
    	return imageURL;
	}
    
    /**
     * Gets the review element with the review id and set the required values
     * in the Review bean 
     * @param review
     * @param reviewsElem
     */
    private Review processReviews(Review reviewObj, Element reviewsElem, String reviewIdVal) {
		if(reviewsElem != null){
			List<Element> reviewsElemList = reviewsElem.getChildren(review);
			int size = reviewsElemList.size();
			for(int index = 0; index < size; index++){
				Element reviewElem = reviewsElemList.get(index);
				if(reviewElem != null){
					if(StringUtils.equals(reviewIdVal, reviewElem.getChildText(reviewId))){
						reviewObj.setReview_title(reviewElem.getChildText(reviewTitle));
						reviewObj.setReview_author(reviewElem.getChildText(reviewAuthor));
						reviewObj.setReview_text(reviewElem.getChildText(reviewText));
						reviewObj.setPros(reviewElem.getChildText(pros));
						reviewObj.setCons(reviewElem.getChildText(cons));
						String ratingVal = reviewElem.getChildText(reviewRating);
						double rating = NumberUtils.toDouble(ratingVal)/2;
						reviewObj.setRating(getRatingsList(ratingVal));
						reviewObj.setReview_rating(String.valueOf(rating));
						break;
					}
				}
			}
		}
		return reviewObj;
	}
    
    @Override
    protected HashMap<String, String> processElement(Element element) {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
