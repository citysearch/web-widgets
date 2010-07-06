package com.citysearch.webwidget.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Address;
import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.ProfileRequest;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * This helper class performs all the functionalty related to Profile API like validating request,
 * calling Profile API and parsing response returned by Profile API
 * 
 * @author Aspert Benjamin
 * 
 */
public class ProfileHelper {

    private final static String PROPERTY_PROFILE_URL = "profile.url";
    private static final String LSITING_ID_ERR_MSG = "listingid.errmsg";
    protected static final String LOCATION = "location";
    private static final String STREET = "street";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String POSTAL_CODE = "postal_code";
    private static final String ADDRESS = "address";
    private static final String CONTACT_INFO = "contact_info";
    private static final String PHONE = "display_phone";
    private static final String URLS = "urls";
    private static final String PROFILE_URL = "profile_url";
    private static final String SEND_TO_FRIEND_URL = "send_to_friend_url";
    private static final String IMAGES = "images";
    private static final String IMAGE = "image";
    private static final String IMAGE_URL = "image_url";
    private static final String IMAGE_PROPERTIES_FILE = "review.image.properties";
    private static final String COMMA_STRING = ",";
    private static HashMap<String, List<String>> imageMap;
    private static final String CATEGORIES = "categories";
    private static final String CATEGORY = "category";
    private static final String CATEGORY_NAME = "name";
    private static final String ID = "id";

    private static final String REVIEWS_URL = "reviews_url";
    private static final String WEBSITE_URL = "website_url";
    private static final String MENU_URL = "menu_url";
    private static final String RESERVATION_URL = "reservation_url";
    private static final String MAP_URL = "map_url";

    private static final String DATE_FORMAT = "reviewdate.format";

    private static final String TOTAL_USER_REVIEWS = "total_user_reviews";
    private static final String REVIEWS = "reviews";

    private Logger log = Logger.getLogger(getClass());
    private String rootPath;

    public ProfileHelper(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Validates the request. If any of the parameters are missing, throws Citysearch Exception
     * 
     * @param request
     * @throws CitysearchException
     */
    private void validateRequest(ProfileRequest request) throws CitysearchException {
        List<String> errors = new ArrayList<String>();
        Properties errorProperties = PropertiesLoader.getErrorProperties();

        if (StringUtils.isBlank(request.getPublisher())) {
            errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getListingId())) {
            errors.add(errorProperties.getProperty(LSITING_ID_ERR_MSG));
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
     * Constructs the Profile API query string with all the supplied parameters and returns query
     * string
     * 
     * @param request
     * @return String
     * @throws CitysearchException
     */
    private String getQueryString(ProfileRequest request) throws CitysearchException {
        // Reflection Probably???
        StringBuilder strBuilder = new StringBuilder();

        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LISTING_ID,
                request.getListingId()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.INFOUSA_ID,
                request.getInfoUSAId()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PHONE,
                request.getPhone()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.ALL_RESULTS,
                String.valueOf(request.isAllResults())));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CUSTOMER_ONLY,
                String.valueOf(request.isCustomerOnly())));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.REVIEW_COUNT,
                request.getReviewCount()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CLIENT_IP,
                request.getClientIP()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CALLBACK,
                request.getCallback()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PLACEMENT,
                request.getPlacement()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.FORMAT,
                request.getFormat()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.NO_LOG,
                String.valueOf(request.getNolog())));
        return strBuilder.toString();
    }

    private Document executeQuery(ProfileRequest request) throws CitysearchException {
        validateRequest(request);
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_PROFILE_URL) + getQueryString(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString, null);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "executeQuery", ihe);
        }
        return responseDocument;
    }

    /**
     * Connects to the Profile API and processes the response sent by API for Reviews
     * Response.Returns the Profile object with values set from response
     * 
     * @param request
     * @return Profile
     * @throws CitysearchException
     */
    public Profile getProfile(ProfileRequest request) throws CitysearchException {
        Document responseDocument = executeQuery(request);
        Profile profile = parseProfileForReviews(responseDocument);
        return profile;
    }

    /**
     * Parses xml response and returns the Profile object
     * 
     * @param doc
     * @return Profile
     * @throws CitysearchException
     */
    private Profile parseProfileForReviews(Document doc) throws CitysearchException {
        Profile profile = null;
        if (doc != null && doc.hasRootElement()) {
            Element locationElem = doc.getRootElement().getChild(LOCATION);
            if (locationElem != null) {
                profile = new Profile();
                profile.setAddress(getAddress(locationElem.getChild(ADDRESS)));
                profile.setPhone(getPhone(locationElem.getChild(CONTACT_INFO)));
                Element url = locationElem.getChild(URLS);
                if (url != null) {
                    profile.setProfileUrl(url.getChildText(PROFILE_URL));
                    profile.setSendToFriendUrl(url.getChildText(SEND_TO_FRIEND_URL));
                }
                Element review = locationElem.getChild(REVIEWS);
                if (review != null) {
                    profile.setReviewCount(review.getChildText(TOTAL_USER_REVIEWS));
                }
                profile.setImageUrl(getImage(locationElem.getChild(IMAGES),
                        locationElem.getChild(CATEGORIES)));
            }
        }
        return profile;
    }

    /**
     * Parses the address element received in response and returns Address object
     * 
     * @param addressElem
     * @return Address
     * @throws CitysearchException
     */
    private Address getAddress(Element addressElem) throws CitysearchException {
        Address address = null;
        if (addressElem != null) {
            address = new Address();
            address.setStreet(addressElem.getChildText(STREET));
            address.setCity(addressElem.getChildText(CITY));
            address.setState(addressElem.getChildText(STATE));
            address.setPostalCode(addressElem.getChildText(POSTAL_CODE));
        }
        return address;
    }

    /**
     * Gets the phone number from contact info element
     * 
     * @param contactInfo
     * @return String
     */
    private String getPhone(Element contactInfo) {
        if (contactInfo != null) {
            return contactInfo.getChildText(PHONE);
        }
        return null;
    }

    /**
     * Gets the image url from xml. If no image url is found, returns the stock image related to the
     * business category
     * 
     * @param images
     * @return String
     * @throws CitysearchException
     */
    private String getImage(Element images, Element categories) throws CitysearchException {
        String imageurl = null;
        if (images != null) {
            List<Element> imageList = images.getChildren(IMAGE);
            int size = imageList.size();
            for (int index = 0; index < size; index++) {
                Element image = imageList.get(index);
                if (image != null) {
                    imageurl = image.getChildText(IMAGE_URL);
                    if (StringUtils.isNotBlank(imageurl)) {
                        break;
                    }
                }
            }
        }

        if (StringUtils.isBlank(imageurl)) {
            imageurl = getStockImage(categories);
        }

        return imageurl;
    }

    /**
     * Returns the stock image url, if no image url is returned by API response Parses the
     * categories element and checks if the any of the category child elements matches with the
     * categories present in the "imageMap" If present, picks up the imageurl randomly from the list
     * of images for that category from the "imageMap" object
     * 
     * @param categories
     * @return imageurl
     * @throws CitysearchException
     */
    private String getStockImage(Element categories) throws CitysearchException {
        if (imageMap == null) {
            getImageMap();
        }
        String imageURL = null;
        if (categories != null && imageMap != null) {
            List<Element> categoryList = categories.getChildren(CATEGORY);
            int size = categoryList.size();
            Set<String> imageKeySet = imageMap.keySet();
            Random randomizer = new Random();
            for (int index = 0; index < size; index++) {
                Element category = categoryList.get(index);
                if (category != null) {
                    String name = category.getAttributeValue(CATEGORY_NAME);
                    if (StringUtils.isNotBlank(name) && imageKeySet.contains(name)) {
                        List<String> imageList = imageMap.get(name);
                        int listSize = imageList.size();
                        int imgIndex = randomizer.nextInt(listSize);
                        imageURL = this.rootPath + imageList.get(imgIndex);
                        break;
                    }
                }
            }
        }
        return imageURL;
    }

    /**
     * Reads the images from a properties file, adds them to a map The properties file contains
     * properties of the format key=category,imageurl Each category is added as a key in the Map and
     * imageurls are added to the list and set as value in the Map The Map contains <key,value> =
     * <category,list of image urls>
     * 
     * @param contextPath
     * @return ArrayList
     * @throws CitysearchException
     */
    private void getImageMap() throws CitysearchException {
        List<String> imageList;
        Properties imageProperties = null;
        if (imageProperties == null) {
            imageProperties = PropertiesLoader.getProperties(IMAGE_PROPERTIES_FILE);
        }

        Set<String> imageKeySet;
        imageMap = new HashMap<String, List<String>>();
        Enumeration<Object> enumerator = imageProperties.keys();
        while (enumerator.hasMoreElements()) {
            String key = (String) enumerator.nextElement();
            String value = imageProperties.getProperty(key);
            String values[] = value.split(COMMA_STRING);
            if (StringUtils.isNotBlank(values[0]) && StringUtils.isNotBlank(values[1])) {
                imageKeySet = imageMap.keySet();
                if (imageKeySet.contains(values[0])) {
                    imageList = imageMap.get(values[0]);
                } else {
                    imageList = new ArrayList<String>();
                }
                imageList.add(values[1]);
                imageMap.put(values[0], imageList);
            }
        }

    }

    public Profile getProfileAndHighestReview(ProfileRequest request) throws CitysearchException {
        Document responseDocument = executeQuery(request);
        Profile profile = findProfileLatestReview(request, responseDocument);
        return profile;
    }

    private Profile findProfileLatestReview(ProfileRequest request, Document doc)
            throws CitysearchException {
        Profile profile = null;
        if (doc != null && doc.hasRootElement()) {
            Element locationElm = doc.getRootElement().getChild(LOCATION);
            if (locationElm != null) {
                profile = new Profile();
                profile.setListingId(locationElm.getChildText(ID));
                profile.setAddress(getAddress(locationElm.getChild(ADDRESS)));
                profile.setPhone(getPhone(locationElm.getChild(CONTACT_INFO)));
                Element urlElm = locationElm.getChild(URLS);
                if (urlElm != null) {
                    profile.setProfileUrl(urlElm.getChildText(PROFILE_URL));
                    profile.setSendToFriendUrl(urlElm.getChildText(SEND_TO_FRIEND_URL));
                    profile.setReviewsUrl(urlElm.getChildText(REVIEWS_URL));
                    profile.setWebsiteUrl(urlElm.getChildText(WEBSITE_URL));
                    profile.setMenuUrl(urlElm.getChildText(MENU_URL));
                    profile.setReservationUrl(urlElm.getChildText(RESERVATION_URL));
                    profile.setMapUrl(urlElm.getChildText(MAP_URL));
                }
                profile.setImageUrl(getImage(locationElm.getChild(IMAGES),
                        locationElm.getChild(CATEGORIES)));
                Element reviewsElm = locationElm.getChild("reviews");
                List<Element> reviews = reviewsElm.getChildren("review");
                SortedMap<Date, Element> reviewMap = new TreeMap<Date, Element>();
                if (reviews != null && !reviews.isEmpty()) {
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            PropertiesLoader.getAPIProperties().getProperty(DATE_FORMAT));
                    for (Element reviewElm : reviews) {
                        String dateStr = reviewElm.getChildText("review_date");
                        Date date = HelperUtil.parseDate(dateStr, formatter);
                        if (date != null) {
                            reviewMap.put(date, reviewElm);
                        }
                    }
                    Element reviewElm = reviewMap.get(reviewMap.lastKey());
                    Review review = ReviewHelper.getReviewInstance(null, reviewElm, this.rootPath);
                    review.setCallBackFunction(request.getCallBackFunction());
                    review.setCallBackUrl(request.getCallBackUrl());

                    String adDisplayTrackingUrl = HelperUtil.getTrackingUrl(review.getReviewUrl(),
                            null, request.getCallBackUrl(), request.getDartClickTrackUrl(),
                            profile.getListingId(), profile.getPhone(), request.getPublisher(),
                            request.getAdUnitName(), request.getAdUnitSize());
                    review.setReviewTrackingUrl(adDisplayTrackingUrl);

                    String callBackFn = HelperUtil.getCallBackFunctionString(
                            request.getCallBackFunction(), profile.getListingId(),
                            profile.getPhone());
                    review.setCallBackFunction(callBackFn);

                    profile.setReview(review);

                    if (profile.getSendToFriendUrl() != null) {
                        String sendToFriendTrackingUrl = HelperUtil.getTrackingUrl(
                                profile.getSendToFriendUrl(), null, request.getCallBackUrl(),
                                request.getDartClickTrackUrl(), profile.getListingId(),
                                profile.getPhone(), request.getPublisher(),
                                request.getAdUnitName(), request.getAdUnitSize());
                        profile.setSendToFriendTrackingUrl(sendToFriendTrackingUrl);
                    }
                }
            }
        }
        return profile;
    }
}
