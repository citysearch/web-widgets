package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.AdListBean;
import com.citysearch.webwidget.bean.SearchRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * This class performs all the functionalities related to Search API like validating query
 * parameters, querying API and processing response Constructs request with different parameters and
 * processes response accordingly for various APIs
 * 
 * @author Aspert Benjamin
 * 
 */
public class SearchHelper {

    public final static String PROPERTY_SEARCH_URL = "search.url";

    private Logger log = Logger.getLogger(getClass());

    private static final String REGION_TAG = "region";
    private static final String ADDRESS_TAG = "address";
    private static final String LISTING_ID_TAG = "id";
    private static final String REVIEWS_TAG = "userreviewcount";
    private static final String TAGLINE_TAG = "samplecategories";
    private static final String PHONE_TAG = "phonenumber";
    private static final String AD_DISPLAY_URL_TAG = "profile";
    private static final String AD_IMAGE_URL_TAG = "image";
    private static final String REVIEW_RATING_TAG = "rating";
    private static final String LOCATION_TAG = "location";

    private static Properties imageProperties;

    /**
     * Returns the Search API Query String
     * 
     * @param request
     * @return
     * @throws CitysearchException
     */
    private String getQueryString(SearchRequest request) throws CitysearchException {
        // Reflection Probably???
        StringBuilder strBuilder = new StringBuilder();

        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);

        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));

        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LATITUDE,
                request.getLatitude()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LONGITUDE,
                request.getLongitude()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS,
                request.getRadius()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        return strBuilder.toString();
    }

    /**
     * Validates the Search API request parameters for fetching nearest Postal Code
     * 
     * @param request
     * @throws CitysearchException
     */
    private void validateClosestLocationPostalCodeRequest(SearchRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        List<String> errors = new ArrayList<String>();
        Properties errorProperties = PropertiesLoader.getErrorProperties();

        if (StringUtils.isBlank(request.getPublisher())) {
            errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getLatitude())) {
            errors.add(errorProperties.getProperty(CommonConstants.LATITUDE_ERROR));
        }
        if (StringUtils.isBlank(request.getLongitude())) {
            errors.add(errorProperties.getProperty(CommonConstants.LONGITUDE_ERROR));
        }
        if (StringUtils.isBlank(request.getRadius())) {
            errors.add(errorProperties.getProperty(CommonConstants.RADIUS_ERROR));
        }
        if (!errors.isEmpty()) {
            throw new InvalidRequestParametersException(this.getClass().getName(),
                    "validateClosestLocationPostalCodeRequest", "Invalid parameters.", errors);
        }
    }

    /**
     * Validates the request parameters, calls Search API and returns the closest Postal Code from
     * the response
     * 
     * @param request
     * @return String
     * @throws CitysearchException
     */
    public String getClosestLocationPostalCode(SearchRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        validateClosestLocationPostalCodeRequest(request);
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_SEARCH_URL) + getQueryString(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(),
                    "getClosestLocationPostalCode", ihe.getMessage());
        }
        String nearestListingPostalCode = findClosestLocationPostalCode(responseDocument);
        if (nearestListingPostalCode == null) {
            throw new CitysearchException(this.getClass().getName(),
                    "getClosestLocationPostalCode", "No locations found.");
        }
        return nearestListingPostalCode;
    }

    /**
     * Returns the nearest Postal Code from the Search API Response
     * 
     * @param doc
     * @return String
     * @throws CitysearchException
     */
    private String findClosestLocationPostalCode(Document doc) throws CitysearchException {
        String closestPostalCode = null;
        if (doc != null && doc.hasRootElement()) {
            Element rootElement = doc.getRootElement();
            List<Element> locationList = rootElement.getChildren("location");
            Double closest = 100000.000D;
            for (int i = 0; i < locationList.size(); i++) {
                Element locationElm = locationList.get(i);
                Element addressElm = locationElm.getChild("address");
                String distanceStr = locationElm.getChildText("distance");
                Double distance = NumberUtils.toDouble(distanceStr);
                String postalCode = addressElm.getChildText("postalcode");
                if ((closestPostalCode == null || distance < closest) && postalCode != null) {
                    closestPostalCode = postalCode;
                    closest = distance;
                }
            }
        }
        return closestPostalCode;
    }

    /**
     * Validates the Search API request parameters to get latitude and longitude
     * 
     * @param request
     * @throws CitysearchException
     */
    private void validateRequest(SearchRequest request) throws InvalidRequestParametersException,
            CitysearchException {
        List<String> errors = new ArrayList<String>();
        Properties errorProperties = PropertiesLoader.getErrorProperties();
        if (StringUtils.isBlank(request.getWhat()) && StringUtils.isBlank(request.getTags())) {
            errors.add(errorProperties.getProperty(CommonConstants.WHAT_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getWhere())) {
            errors.add(errorProperties.getProperty(CommonConstants.WHERE_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getPublisher())) {
            errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
        }

        if (!errors.isEmpty()) {
            throw new InvalidRequestParametersException(this.getClass().getName(),
                    "validateLatitudeLongitudeRequest", "Invalid parameters.", errors);
        }
    }

    /**
     * Constructs Search API request to get latitude and longitude values
     * 
     * @param request
     * @return
     * @throws CitysearchException
     */
    private String getSearchRequestQueryString(SearchRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        StringBuilder apiQueryString = new StringBuilder();

        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));

        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHERE,
                request.getWhere()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                request.getTags()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam("rpp", request.getRpp()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS,
                request.getRadius()));
        return apiQueryString.toString();

    }

    /**
     * Queries the Search API and returns latitude, longitude values in a String Array
     * 
     * @param request
     * @return String[]
     * @throws CitysearchException
     */
    public String[] getLatitudeLongitude(SearchRequest request) throws CitysearchException {
        validateRequest(request);
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_SEARCH_URL)
                + getSearchRequestQueryString(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getLatitudeLongitude",
                    ihe.getMessage());
        }
        String[] latLonValues = getLatitudeAndLongitude(responseDocument);
        return latLonValues;
    }

    public List<AdListBean> getNearbyPlaces(SearchRequest request) throws CitysearchException {
        validateRequest(request);
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_SEARCH_URL)
                + getSearchRequestQueryString(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getNearbyPlaces",
                    ihe.getMessage());
        }
        String[] latLonValues = getLatitudeAndLongitude(responseDocument);
        return parseXML(responseDocument, latLonValues[0], latLonValues[1],
                CommonConstants.SEARCH_API_TYPE, "/");
    }

    private String[] getLatitudeAndLongitude(Document document) {
        String[] latLonValues = new String[2];
        if (document != null && document.hasRootElement()) {
            Element rootElement = document.getRootElement();
            Element region = rootElement.getChild("region");
            if (region != null) {
                String sLat = region.getChildText(CommonConstants.LATITUDE);
                String sLon = region.getChildText(CommonConstants.LONGITUDE);
                if (sLat != null && sLon != null) {
                    latLonValues[0] = sLat;
                    latLonValues[1] = sLon;
                }
            }
        }
        return latLonValues;
    }

    public ArrayList<AdListBean> parseXML(Document doc, String sLat, String sLon, String apiType,
            String contextPath) throws CitysearchException {
        ArrayList<AdListBean> adList = new ArrayList<AdListBean>();
        try {
            if (doc != null && doc.hasRootElement()) {
                Element rootElement = doc.getRootElement();
                List<Element> resultSet = rootElement.getChildren(LOCATION_TAG);
                if (resultSet != null) {
                    int size = resultSet.size();
                    HashMap<String, String> resultMap;
                    // Retrieving values from result xml
                    for (int i = 0; i < size; i++) {
                        AdListBean adListBean = new AdListBean();
                        Element ad = (Element) resultSet.get(i);
                        resultMap = processElement(ad);
                        adListBean = processMap(resultMap, sLat, sLon);
                        if (adListBean != null)
                            adList.add(adListBean);
                    }
                }
            }
        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " parseXML()";
            log.error(errMsg, excep);
            throw new CitysearchException(this.getClass().getName(), "parseXML", excep.getMessage());
        }
        Collections.sort(adList);
        adList = getDisplayList(adList, contextPath);
        return adList;
    }

    /**
     * Restricts the list size to three and add default images, if images are not returned in the
     * API response
     * 
     * @param adList
     * @param contextPath
     * @return ArrayList
     * @throws CitysearchException
     */
    protected ArrayList<AdListBean> getDisplayList(ArrayList<AdListBean> adList, String contextPath)
            throws CitysearchException {
        ArrayList<AdListBean> displayList = new ArrayList<AdListBean>(3);
        if (adList.size() > CommonConstants.NEARBY_PLACES_DISPLAY_SIZE) {
            for (int i = 0; i < CommonConstants.NEARBY_PLACES_DISPLAY_SIZE; i++) {
                displayList.add(adList.get(i));
            }
        } else {
            displayList = adList;
        }
        displayList = addDefaultImages(displayList, contextPath);
        return displayList;
    }

    /**
     * Adds default images to each of the ad elements which do not have an image returned in the
     * response
     * 
     * @param adList
     * @param contextPath
     * @return ArrayList
     * @throws CitysearchException
     */
    private ArrayList<AdListBean> addDefaultImages(ArrayList<AdListBean> adList, String contextPath)
            throws CitysearchException {
        AdListBean adListBean;
        ArrayList<String> imageList;
        Random random;
        ArrayList<Integer> indexList = new ArrayList<Integer>(3);
        int imageListSize = 0;
        String imageUrl = "";

        imageList = getImageList(contextPath);
        random = new Random();
        int size = adList.size();

        for (int i = 0; i < size; i++) {
            adListBean = adList.get(i);
            imageUrl = adListBean.getAdImageURL();
            if (StringUtils.isBlank(imageUrl)) {
                int index = 0;
                imageListSize = imageList.size();
                if (imageListSize > 0) {
                    do {
                        index = random.nextInt(imageListSize);
                    } while (indexList.contains(index));
                    indexList.add(index);
                    imageUrl = imageList.get(index);
                    adListBean.setAdImageURL(imageUrl);
                }
            }
            adList.set(i, adListBean);
        }

        return adList;
    }

    /**
     * Reads the images from a properties file, add them to a list and returns it
     * 
     * @param contextPath
     * @return ArrayList
     * @throws CitysearchException
     */
    private ArrayList<String> getImageList(String contextPath) throws CitysearchException {
        ArrayList<String> imageList = new ArrayList<String>();
        try {
            if (imageProperties == null) {
                imageProperties = PropertiesLoader.getProperties(CommonConstants.IMAGES_PROPERTIES_FILE);
            }
            Enumeration<Object> enumerator = imageProperties.keys();
            while (enumerator.hasMoreElements()) {
                String key = (String) enumerator.nextElement();
                String value = imageProperties.getProperty(key);
                imageList.add(contextPath + "/" + value);
            }

        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.IMAGE_ERROR);
            log.error(errMsg);
        }

        return imageList;
    }

    /**
     * Reads from the Ad element and constructs bean element
     * 
     * @param sLat
     * @return AdListBean
     */
    protected HashMap<String, String> processElement(Element location) {
        HashMap<String, String> elementMap = new HashMap<String, String>();
        if (location != null) {
            String name = location.getChildText(CommonConstants.NAME);
            if (StringUtils.isNotBlank(name)) {
                elementMap.put(CommonConstants.NAME, name);
                Element address = location.getChild(ADDRESS_TAG);
                if (address != null) {
                    elementMap.put(CommonConstants.CITY,
                            location.getChildText(CommonConstants.CITY));
                    elementMap.put(CommonConstants.STATE,
                            location.getChildText(CommonConstants.STATE));
                }
                elementMap.put(CommonConstants.RATING, location.getChildText(REVIEW_RATING_TAG));
                elementMap.put(CommonConstants.REVIEWCOUNT, location.getChildText(REVIEWS_TAG));
                elementMap.put(CommonConstants.CATEGORY, location.getChildText(TAGLINE_TAG));
                elementMap.put(CommonConstants.DLAT,
                        location.getChildText(CommonConstants.LATITUDE));
                elementMap.put(CommonConstants.DLON,
                        location.getChildText(CommonConstants.LONGITUDE));
                elementMap.put(CommonConstants.PHONE, location.getChildText(PHONE_TAG));
                elementMap.put(CommonConstants.LISTING_ID,
                        location.getAttributeValue(LISTING_ID_TAG));
                elementMap.put(CommonConstants.DISPLAY_URL,
                        location.getChildText(AD_DISPLAY_URL_TAG));
                elementMap.put(CommonConstants.IMAGE_URL, location.getChildText(AD_IMAGE_URL_TAG));

            }
        }
        return elementMap;
    }

    /**
     * Read the values from Map, do the required processing , add to AdListBean and return it
     * 
     * @param resultMap
     * @param sLat
     * @param sLon
     * @return AdListBean
     * @throws CitysearchException
     */
    private AdListBean processMap(HashMap<String, String> resultMap, String sLat, String sLon)
            throws CitysearchException {
        AdListBean adListBean = null;
        if (resultMap != null) {
            // Calculating Distance
            double distance = 0.0;
            String dLat = resultMap.get(CommonConstants.DLAT);
            String dLon = resultMap.get(CommonConstants.DLON);
            String rating = resultMap.get(CommonConstants.RATING);
            String reviewCount = resultMap.get(CommonConstants.REVIEWCOUNT);
            String listingId = resultMap.get(CommonConstants.LISTING_ID);
            String category = resultMap.get(CommonConstants.CATEGORY);
            String name = resultMap.get(CommonConstants.NAME);
            String phone = resultMap.get(CommonConstants.PHONE);
            if (StringUtils.isNotBlank(sLat) && StringUtils.isNotBlank(sLon)
                    && StringUtils.isNotBlank(dLat) && StringUtils.isNotBlank(dLon)) {
                BigDecimal sourceLat = new BigDecimal(sLat);
                BigDecimal sourceLon = new BigDecimal(sLon);
                BigDecimal destLat = new BigDecimal(dLat);
                BigDecimal destLon = new BigDecimal(dLon);
                distance = HelperUtil.getDistance(sourceLat, sourceLon, destLat, destLon);
            }

            List<Integer> ratingList = HelperUtil.getRatingsList(rating);
            double ratings = HelperUtil.getRatingValue(rating);
            int userReviewCount = HelperUtil.toInteger(reviewCount);
            name = HelperUtil.getAbbreviatedString(name,
                    CommonConstants.BUSINESS_NAME_MAX_LENGTH_PROP,
                    CommonConstants.BUSINESS_NAME_MAX_LENGTH);
            category = HelperUtil.getAbbreviatedString(category,
                    CommonConstants.TAGLINE_MAX_LENGTH_PROP,
                    CommonConstants.BUSINESS_NAME_MAX_LENGTH);
            String location = HelperUtil.getLocationString(resultMap.get(CommonConstants.CITY),
                    resultMap.get(CommonConstants.STATE));

            // Adding to AdListBean
            if (distance < CommonConstants.EXTENDED_RADIUS) {
                adListBean = new AdListBean();
                adListBean.setName(name);
                adListBean.setLocation(location);
                adListBean.setRating(ratingList);
                adListBean.setReviewCount(userReviewCount);
                adListBean.setDistance(distance);
                adListBean.setListingId(StringUtils.trim(listingId));
                adListBean.setCategory(category);
                adListBean.setRatings(ratings);
                adListBean.setAdDisplayURL(resultMap.get(CommonConstants.DISPLAY_URL));
                adListBean.setAdImageURL(resultMap.get(CommonConstants.IMAGE_URL));
                adListBean.setPhone(StringUtils.trim(phone));
            }
        }
        return adListBean;
    }
}
