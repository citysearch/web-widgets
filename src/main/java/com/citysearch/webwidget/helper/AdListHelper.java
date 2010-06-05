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
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.AdListBean;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.bean.SearchRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * Helper class for PFP API. Contains the functionality to validate request parameters, queries the
 * API for different kind of requests and processes response accordingly
 * 
 * @author Aspert Benjamin
 * 
 */
public class AdListHelper {

    private final static String PFP_LOCATION_URL = "pfplocation.url";

    private Logger log = Logger.getLogger(getClass());
    private static final String IOEXCEP_MSG = "streamread.error";
    private static final String JDOMEXCEP_MSG = "jdom.excep.msg";

    private static final String AD_TAG = "ad";
    private static final String LOCATION_TAG = "location";
    private static final int DISPLAY_SIZE = 3;

    private static final int TOTAL_RATING = 5;
    private static final int EMPTY_STAR = 0;
    private static final int HALF_STAR = 1;
    private static final int FULL_STAR = 2;

    private static final String APITYPE_ERROR = "invalid.apitype";

    private static final String REVIEW_RATING_TAG = "overall_review_rating";
    private static final String REVIEWS_TAG = "reviews";
    private static final String LISTING_ID_TAG = "listingId";
    private static final String TAGLINE_TAG = "tagline";
    private static final String AD_DISPLAY_URL_TAG = "ad_display_url";
    private static final String AD_IMAGE_URL_TAG = "ad_image_url";
    private static final String PHONE_TAG = "phone";

    private static Properties imageProperties;

    /**
     * Validates PFP API request parameters
     * 
     * @param request
     * @throws CitysearchException
     */
    private void validateRequest(NearbyPlacesRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        List<String> errors = new ArrayList<String>();
        Properties errorProperties = PropertiesLoader.getErrorProperties();
        if (StringUtils.isBlank(request.getWhat()) && StringUtils.isBlank(request.getTags())) {
            errors.add(errorProperties.getProperty(CommonConstants.WHAT_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getWhere())
                && (StringUtils.isBlank(request.getLatitude()) || StringUtils.isBlank(request.getLongitude()))) {
            errors.add(errorProperties.getProperty(CommonConstants.WHERE_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getPublisher())) {
            errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
        }

        if (!errors.isEmpty()) {
            throw new InvalidRequestParametersException(this.getClass().getName(),
                    "validateRequest", "Invalid parameters.", errors);
        }
    }

    /**
     * Constructs and returns PFP query string with geography
     * 
     * @param request
     * @return String
     * @throws CitysearchException
     */
    private String getQueryStringWithGeography(NearbyPlacesRequest request)
            throws CitysearchException {
        StringBuilder apiQueryString = new StringBuilder();

        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));

        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LATITUDE,
                request.getLatitude()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LONGITUDE,
                request.getLongitude()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                request.getTags()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS,
                request.getRadius()));
        return apiQueryString.toString();
    }

    /**
     * Constructs and returns PFP Query String without geography parameters
     * 
     * @param request
     * @return String
     * @throws CitysearchException
     */
    private String getQueryStringWithoutGeography(NearbyPlacesRequest request)
            throws CitysearchException {
        StringBuilder apiQueryString = new StringBuilder();
        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));

        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                request.getTags()));
        return apiQueryString.toString();
    }

    private void loadLatitudeAndLongitudeFromSearchAPI(NearbyPlacesRequest request)
            throws CitysearchException {
        SearchRequest sRequest = new SearchRequest();
        sRequest.setWhat(request.getWhat());
        sRequest.setWhere(request.getWhere());
        sRequest.setTags(request.getTags());
        sRequest.setPublisher(request.getPublisher());

        SearchHelper sHelper = new SearchHelper();
        String[] latLon = sHelper.getLatitudeLongitude(sRequest);
        if (latLon.length >= 2) {
            request.setLatitude(latLon[0]);
            request.setLongitude(latLon[1]);
        }
    }

    /**
     * Queries Search API for latitude and longitude if not present in request, then queries PFP api
     * with Geography parameters. If no results are returned then queries PFP API again but without
     * geography parameters.
     * 
     * @param request
     * @throws CitysearchException
     */
    public List<AdListBean> getAdList(NearbyPlacesRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        validateRequest(request);
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            loadLatitudeAndLongitudeFromSearchAPI(request);
        }
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            throw new CitysearchException(this.getClass().getName(), "getAdList",
                    "Invalid Latitude and Longitude");
        }

        // TODO: clean this!!!!
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PFP_LOCATION_URL)
                + getQueryStringWithGeography(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getAdList", ihe.getMessage());
        }
        List<AdListBean> adList = parseXML(responseDocument, request.getLatitude(),
                request.getLongitude(), CommonConstants.PFP_API_TYPE, "/");

        if (adList == null || adList.size() == 0) {
            urlString = properties.getProperty(CommonConstants.PFP_WITHOUT_GEOGRAPHY)
                    + getQueryStringWithoutGeography(request);
            responseDocument = null;
            try {
                responseDocument = HelperUtil.getAPIResponse(urlString);
            } catch (InvalidHttpResponseException ihe) {
                throw new CitysearchException(this.getClass().getName(), "getAdList",
                        ihe.getMessage());
            }
            adList = parseXML(responseDocument, request.getLatitude(), request.getLongitude(),
                    CommonConstants.PFP_API_TYPE, "/");

            if (adList == null || adList.size() == 0) {
                // Query Search API
                SearchRequest sRequest = new SearchRequest();
                sRequest.setWhat(request.getWhat());
                sRequest.setWhere(request.getWhere());
                sRequest.setTags(request.getTags());
                sRequest.setPublisher(request.getPublisher());

                SearchHelper sHelper = new SearchHelper();
            }
        }
        return adList;
    }

    /**
     * Parses the API response xml and returns the List with three ads from the response.
     * 
     * @param doc
     * @param sLat
     * @param sLon
     * @param apiType
     * @param contextPath
     * @return ArrayList
     * @throws CitysearchException
     */
    public ArrayList<AdListBean> parseXML(Document doc, String sLat, String sLon, String apiType,
            String contextPath) throws CitysearchException {
        ArrayList<AdListBean> adList = new ArrayList<AdListBean>();
        try {
            if (doc != null && doc.hasRootElement()) {
                Element rootElement = doc.getRootElement();
                List<Element> resultSet = rootElement.getChildren(AD_TAG);
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
     * Parses the ad element returned in response and add the values to a HashMap and returns it
     * 
     * @param ad
     * @return HashMap
     */
    protected HashMap<String, String> processElement(Element ad) {
        HashMap<String, String> elementMap = new HashMap<String, String>();
        if (ad != null) {
            String name = ad.getChildText(CommonConstants.NAME);
            if (StringUtils.isNotBlank(name)) {
                elementMap.put(CommonConstants.NAME, name);
                elementMap.put(CommonConstants.CITY, ad.getChildText(CommonConstants.CITY));
                elementMap.put(CommonConstants.STATE, ad.getChildText(CommonConstants.STATE));
                elementMap.put(CommonConstants.RATING, ad.getChildText(REVIEW_RATING_TAG));
                elementMap.put(CommonConstants.REVIEWCOUNT, ad.getChildText(REVIEWS_TAG));
                elementMap.put(CommonConstants.LISTING_ID, ad.getChildText(LISTING_ID_TAG));
                elementMap.put(CommonConstants.CATEGORY, ad.getChildText(TAGLINE_TAG));
                elementMap.put(CommonConstants.DLAT, ad.getChildText(CommonConstants.LATITUDE));
                elementMap.put(CommonConstants.DLON, ad.getChildText(CommonConstants.LONGITUDE));
                elementMap.put(CommonConstants.PHONE, ad.getChildText(PHONE_TAG));
                elementMap.put(CommonConstants.DISPLAY_URL, ad.getChildText(AD_DISPLAY_URL_TAG));
                elementMap.put(CommonConstants.IMAGE_URL, ad.getChildText(AD_IMAGE_URL_TAG));
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

    /**
     * Reads the apiType and returns the name of the child element to be processed
     * 
     * @param apiType
     * @return String
     * @throws CitysearchException
     */
    /*
     * private String getApiChildElementName(String apiType) throws CitysearchException { String
     * childName; if (apiType.equalsIgnoreCase(CommonConstants.PFP_API_TYPE)) { childName = AD_TAG;
     * } else if (apiType.equalsIgnoreCase(CommonConstants.SEARCH_API_TYPE)) { childName =
     * LOCATION_TAG; } else { String errMsg =
     * PropertiesLoader.getErrorProperties().getProperty(APITYPE_ERROR); log.error(errMsg); throw
     * new CitysearchException(this.getClass().getName(), "getApiChildElementName", errMsg); }
     * return childName; }
     */
}
