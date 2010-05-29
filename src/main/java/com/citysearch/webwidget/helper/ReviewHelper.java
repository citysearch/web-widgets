package com.citysearch.webwidget.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.bean.ReviewResponse;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

public class ReviewHelper extends AbstractHelper {
	
	public final static String PROPERTY_REVIEW_URL = "reviews.url";
	private static final String businessName = "business_name";
    private static final String listingId = "listing_id";
    private static final String reviewId = "review_id";
    private static final String reviewTitle = "review_title";
    private static final String reviewText = "review_text";
    private static final String pros = "pros";
    private static final String cons = "cons";
    private static final String reviewRating = "review_rating";
    private static final String reviewDate = "review_date";
    private static final String reviewAuthor = "review_author";
    private static final String dateFormat = "reviewdate.format";
    private static final String reviewElemName = "review";
    private static final int minRating = 6;
    private Logger log = Logger.getLogger(getClass());
    	
	private ReviewRequest request;

	public ReviewHelper(ReviewRequest request) {
		this.request = request;
	}
	
	/**
	 * Constructs the Reviews API query string with all the supplied parameters
	 * @return
	 * @throws CitysearchException
	 */
	private String getQueryString() throws CitysearchException
	{
		//Reflection Probably???
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(constructQueryParam(APIFieldNameConstants.API_KEY, request.getApi_key()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.WHERE, request.getWhere()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.WHAT, request.getWhat()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.TAG_ID, request.getTagId()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.TAG_NAME, request.getTag_name()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.CUSTOMER_ONLY, String.valueOf(request.isCustomerOnly())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.RATING, String.valueOf(request.getRating())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.DAYS, String.valueOf(request.getDays())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.MAX, String.valueOf(request.getMax())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(constructQueryParam(APIFieldNameConstants.PLACEMENT, request.getPlacement()));
		return strBuilder.toString();
	}
	
	/**
	 * Validates the request. If any of the parameters are missing,
	 * throws Citysearch Exception
	 * @throws CitysearchException
	 */
	public void validateRequest() throws CitysearchException {
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();
		if (StringUtils.isBlank(request.getApi_key()))
		{
			errors.add(errorProperties.getProperty(CommonConstants.API_KEY_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getPublisher()))
		{
			errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getWhere()))
		{
			errors.add(errorProperties.getProperty(CommonConstants.WHERE_ERROR_CODE));
		}
		if (!errors.isEmpty())
		{
			throw new CitysearchException(this.getClass().getName(), "validateRequest", "Invalid parameters.", errors);
		}
	}

	/**
	 * Connects to the Reviews API and processes the response sent by API
	 * @return
	 * @throws CitysearchException
	 */
	public ReviewResponse getReviews() throws CitysearchException {
		validateRequest();
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_REVIEW_URL) + getQueryString();
		log.info(urlString);
		Document responseDocument = getAPIResponse(urlString);
		Review reviewObj = parseXML(responseDocument);
		return new ReviewResponse(reviewObj);
	}
	
	
	 /**
     * Parses the Reviews xml. Returns Review object with values from api
     * 
     * @param doc
     * @return
     * @throws CitysearchException
     */
    public Review parseXML(Document doc) throws CitysearchException {
        Review review = null;
        if (doc != null && doc.hasRootElement()) {
            Element rootElement = doc.getRootElement();
            if (rootElement != null) {
                try{
                    List<Element> reviewsList = rootElement.getChildren(reviewElemName);
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            PropertiesLoader.getAPIProperties().getProperty(dateFormat));
                    SortedMap<Date, Review> reviewMap = new TreeMap<Date, Review>();
                    for (int i = 0; i < reviewsList.size(); i++) {
                        Element reviewElem = reviewsList.get(i);
                        if (reviewElem != null) {
                            String rating = reviewElem.getChildText(reviewRating);
                            if (NumberUtils.toInt(rating) >= minRating) {
                                String dateStr = reviewElem.getChildText(reviewDate);
                                Date date = parseDate(dateStr, formatter);
                                if (date != null) {
                                    review = new Review();
                                    review = processReviews(review, reviewElem, date);
                                    reviewMap.put(date, review);
                                }
                            }
                        }
                    }
                    review = reviewMap.get(reviewMap.lastKey());
                }catch(Exception excep){
                    throw new CitysearchException(this.getClass().getName(),
                            "parseXML", excep.getMessage());
                }
            }
        }
        return review;
    }

    /**
     * Parses the review element and set the required values in the Review bean
     * 
     * @param review
     * @param reviewsElem
     * @throws CitysearchException 
     */
    private Review processReviews(Review reviewObj, Element reviewElem, Date date) throws CitysearchException {
        if (reviewElem != null) {
            try{
                reviewObj.setBusiness_name(reviewElem.getChildText(businessName));
                reviewObj.setListing_id(reviewElem.getChildText(listingId));
    
                reviewObj.setReview_title(reviewElem.getChildText(reviewTitle));
                reviewObj.setReview_author(reviewElem.getChildText(reviewAuthor));
                reviewObj.setReview_text(reviewElem.getChildText(reviewText));
                reviewObj.setPros(reviewElem.getChildText(pros));
                reviewObj.setCons(reviewElem.getChildText(cons));
                String ratingVal = reviewElem.getChildText(reviewRating);
                double rating = NumberUtils.toDouble(ratingVal) / 2;
                reviewObj.setRating(getRatingsList(ratingVal));
                reviewObj.setReview_rating(String.valueOf(rating));
                reviewObj.setReview_id(reviewElem.getChildText(reviewId));
                // reviewObj.setTime_since_review(getTimeSinceReview(date));
            }catch(Exception excep){
                throw new CitysearchException(this.getClass().getName(),
                        "processReviews", excep.getMessage());
            }
        }
        return reviewObj;
    }


}
