package com.citysearch.webwidget.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.ProfileRequest;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.bean.SearchRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * This Helper class performs all the functionality related to Reviews. Validates the Review
 * request, calls the API, aprses the response, then calls the Profile API and returns the final
 * response back
 * 
 * @author Aspert Benjamin
 * 
 */
public class ReviewHelper {

    public final static String PROPERTY_REVIEW_URL = "reviews.url";

    public final static Integer BUSINESS_NAME_SIZE = 30;
    public final static Integer REVIEW_TITLE_SIZE = 45;
    public final static Integer REVIEW_TEXT_SIZE = 315;
    public final static Integer PROS_SIZE = 40;
    public final static Integer CONS_SIZE = 40;
    private static final int MINIMUM_RATING = 6;

    private static final String ELEMENT_REVIEW_URL = "review_url";
    private static final String BUSINESS_NAME = "business_name";
    private static final String LISTING_ID = "listing_id";
    private static final String REVIEW_ID = "review_id";
    private static final String REVIEW_TITLE = "review_title";
    private static final String REVIEW_TEXT = "review_text";
    private static final String PROS = "pros";
    private static final String CONS = "cons";
    private static final String REVIEW_RATING = "review_rating";
    private static final String REVIEW_DATE = "review_date";
    private static final String REVIEW_AUTHOR = "review_author";
    private static final String DATE_FORMAT = "reviewdate.format";
    private static final String REVIEW_ELEMENT = "review";

    private Logger log = Logger.getLogger(getClass());

    /**
     * Constructs the Reviews API query string with all the supplied parameters
     * 
     * @return String
     * @throws CitysearchException
     */
    private String getQueryString(ReviewRequest request) throws CitysearchException {
        // Reflection Probably???
        StringBuilder strBuilder = new StringBuilder();

        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHERE,
                request.getWhere()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG_ID,
                request.getTagId()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG_NAME,
                request.getTagName()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        if (request.isCustomerOnly()) {
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CUSTOMER_ONLY,
                    String.valueOf(request.isCustomerOnly())));
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        }
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RATING,
                request.getRating()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.DAYS,
                request.getDays()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.MAX,
                request.getMax()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PLACEMENT,
                request.getPlacement()));
        return strBuilder.toString();
    }

    /**
     * Validates the request. If any of the parameters are missing, throws Citysearch Exception
     * 
     * @throws CitysearchException
     */
    public void validateRequest(ReviewRequest request) throws InvalidRequestParametersException,
            CitysearchException {
        List<String> errors = new ArrayList<String>();
        Properties errorProperties = PropertiesLoader.getErrorProperties();

        if (StringUtils.isBlank(request.getPublisher())) {
            errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getLatitude())
                && StringUtils.isBlank(request.getLongitude())
                && StringUtils.isBlank(request.getWhere())) {
            errors.add(errorProperties.getProperty(CommonConstants.WHERE_ERROR_CODE));
        }

        if (!StringUtils.isBlank(request.getLatitude())
                && StringUtils.isBlank(request.getLongitude())) {
            errors.add(errorProperties.getProperty(CommonConstants.LONGITUDE_ERROR));
        } else if (StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())) {
            errors.add(errorProperties.getProperty(CommonConstants.LATITUDE_ERROR));
        }

        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())
                && StringUtils.isBlank(request.getRadius())) {
            errors.add(errorProperties.getProperty(CommonConstants.RADIUS_ERROR));
        }
        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())
                && StringUtils.isBlank(request.getWhat())) {
            errors.add(errorProperties.getProperty(CommonConstants.WHAT_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getClientIP())) {
            errors.add(errorProperties.getProperty(CommonConstants.CLIENT_IP_ERROR_CODE));
        }
        if (!errors.isEmpty()) {
            throw new InvalidRequestParametersException(this.getClass().getName(),
                    "validateRequest", "Invalid parameters.", errors);
        }
    }

    /**
     * Gets the review with the latest timestamp from Review API Then calls the profile API and gets
     * the details not available from review API like Address,Phone,SendToFriendURL and ImageURL
     * 
     * @param request
     * @return Review
     * @throws InvalidRequestParametersException
     * @throws CitysearchException
     */
    public Review getLatestReview(ReviewRequest request) throws InvalidRequestParametersException,
            CitysearchException {
        validateRequest(request);

        // If Lat and Lon is set find the nearest postal code using search API
        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())) {
            SearchRequest searchReq = new SearchRequest();
            searchReq.setPublisher(request.getPublisher());
            searchReq.setWhat(request.getWhat());
            searchReq.setLatitude(request.getLatitude());
            searchReq.setLongitude(request.getLongitude());

            SearchHelper shelper = new SearchHelper();
            String where = shelper.getClosestLocationPostalCode(searchReq);
            request.setWhere(where);
        }

        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_REVIEW_URL) + getQueryString(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getLatestReview", ihe);
        }
        Review reviewObj = parseXML(responseDocument);
        if (reviewObj == null) {
            throw new CitysearchException(this.getClass().getName(), "getLatestReview",
                    "No latest review found.");
        }

        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setPublisher(request.getPublisher());
        profileRequest.setClientIP(request.getClientIP());
        profileRequest.setListingId(reviewObj.getListingId());

        ProfileHelper profHelper = new ProfileHelper();
        Profile profile = profHelper.getProfile(profileRequest);
        if (profile != null) {
            reviewObj.setAddress(profile.getAddress());
            reviewObj.setPhone(profile.getPhone());
            reviewObj.setProfileUrl(profile.getProfileUrl());
            reviewObj.setSendToFriendUrl(profile.getSendToFriendUrl());
            reviewObj.setImageUrl(profile.getImageUrl());
        }

        return reviewObj;
    }

    /**
     * Parses the Reviews xml. Returns Review object with values from api
     * 
     * @param doc
     * @return Review
     * @throws CitysearchException
     */
    private Review parseXML(Document doc) throws CitysearchException {
        Review review = null;
        if (doc != null && doc.hasRootElement()) {
            Element rootElement = doc.getRootElement();
            List<Element> reviewsList = rootElement.getChildren(REVIEW_ELEMENT);
            SimpleDateFormat formatter = new SimpleDateFormat(PropertiesLoader.getAPIProperties()
                    .getProperty(DATE_FORMAT));
            SortedMap<Date, Element> reviewMap = new TreeMap<Date, Element>();
            for (int i = 0; i < reviewsList.size(); i++) {
                Element reviewElem = reviewsList.get(i);
                String rating = reviewElem.getChildText(REVIEW_RATING);
                if (NumberUtils.toInt(rating) >= MINIMUM_RATING) {
                    String dateStr = reviewElem.getChildText(REVIEW_DATE);
                    Date date = HelperUtil.parseDate(dateStr, formatter);
                    if (date != null) {
                        reviewMap.put(date, reviewElem);
                    }
                }
            }
            Element reviewElm = reviewMap.get(reviewMap.lastKey());
            review = getReviewInstance(reviewElm);
        }
        return review;
    }

    /**
     * Parses the review element and set the required values in the Review bean
     * 
     * @param review
     * @param reviewsElem
     * @return Review
     * @throws CitysearchException
     */
    private Review getReviewInstance(Element reviewElem) throws CitysearchException {
        Review review = new Review();

        String businessName = reviewElem.getChildText(BUSINESS_NAME);
        review.setBusinessName(businessName);
        if (businessName != null && businessName.trim().length() > BUSINESS_NAME_SIZE) {
            review.setShortBusinessName(StringUtils.substring(businessName, 0,
                    BUSINESS_NAME_SIZE - 1));
        } else {
            review.setShortBusinessName(businessName);
        }

        String reviewTitle = reviewElem.getChildText(REVIEW_TITLE);
        review.setReviewTitle(reviewTitle);
        if (reviewTitle != null && reviewTitle.trim().length() > REVIEW_TITLE_SIZE) {
            review.setShortTitle(StringUtils.substring(reviewTitle, 0, REVIEW_TITLE_SIZE - 1));
        } else {
            review.setShortTitle(reviewTitle);
        }

        String reviewText = reviewElem.getChildText(REVIEW_TEXT);
        review.setReviewText(reviewText + "Some more text");
        if (reviewText != null && reviewText.trim().length() > REVIEW_TEXT_SIZE) {
            review.setShortReviewText(StringUtils.substring(reviewText, 0, REVIEW_TEXT_SIZE - 1));
        } else {
            review.setShortReviewText(reviewText);
        }

        String pros = reviewElem.getChildText(PROS);
        review.setPros(pros);
        if (pros != null && pros.trim().length() > PROS_SIZE) {
            review.setShortPros(StringUtils.substring(pros, 0, PROS_SIZE - 1));
        } else {
            review.setShortPros(pros);
        }

        String cons = reviewElem.getChildText(CONS);
        review.setCons(cons);
        if (cons != null && cons.trim().length() > CONS_SIZE) {
            review.setShortCons(StringUtils.substring(cons, 0, CONS_SIZE - 1));
        } else {
            review.setShortCons(cons);
        }

        review.setListingId(reviewElem.getChildText(LISTING_ID));
        review.setReviewAuthor(reviewElem.getChildText(REVIEW_AUTHOR));
        String ratingVal = reviewElem.getChildText(REVIEW_RATING);
        double rating = NumberUtils.toDouble(ratingVal) / 2;
        review.setRating(HelperUtil.getRatingsList(ratingVal));
        review.setReviewRating(String.valueOf(rating));
        review.setReviewId(reviewElem.getChildText(REVIEW_ID));
        review.setReviewUrl(reviewElem.getChildText(ELEMENT_REVIEW_URL));

        String rDateStr = reviewElem.getChildText(REVIEW_DATE);
        SimpleDateFormat formatter = new SimpleDateFormat(PropertiesLoader.getAPIProperties()
                .getProperty(DATE_FORMAT));
        Date date = HelperUtil.parseDate(rDateStr, formatter);
        long now = Calendar.getInstance().getTimeInMillis();
        review.setTimeSinceReviewString(DurationFormatUtils.formatDurationWords(now
                - date.getTime(), true, true));

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        review.setReviewDate(df.format(date));

        return review;
    }

}
