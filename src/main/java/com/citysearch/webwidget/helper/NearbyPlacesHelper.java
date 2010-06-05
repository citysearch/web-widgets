package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.NearbyPlace;
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
public class NearbyPlacesHelper {

    private final static String PFP_LOCATION_URL = "pfplocation.url";
    private final static String PFP_URL = "pfp.url";

    private Logger log = Logger.getLogger(getClass());
    private static final String AD_TAG = "ad";

    private static final String REVIEW_RATING_TAG = "overall_review_rating";
    private static final String REVIEWS_TAG = "reviews";
    private static final String LISTING_ID_TAG = "listingId";
    private static final String TAGLINE_TAG = "tagline";
    private static final String AD_DISPLAY_URL_TAG = "ad_display_url";
    private static final String AD_IMAGE_URL_TAG = "ad_image_url";
    private static final String PHONE_TAG = "phone";

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
    public List<NearbyPlace> getNearbyPlaces(NearbyPlacesRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        validateRequest(request);
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            loadLatitudeAndLongitudeFromSearchAPI(request);
        }
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            throw new CitysearchException(this.getClass().getName(), "getNearbyPlaces",
                    "Invalid Latitude and Longitude");
        }

        List<NearbyPlace> adList = getPlacesByGeoCodes(request);
        if (adList == null || adList.isEmpty()) {
            adList = getPlacesWithoutGeoCodes(request);
            if (adList == null || adList.isEmpty()) {
                // Query Search API
                SearchRequest sRequest = new SearchRequest();
                sRequest.setWhat(request.getWhat());
                sRequest.setWhere(request.getWhere());
                sRequest.setTags(request.getTags());
                sRequest.setPublisher(request.getPublisher());

                SearchHelper sHelper = new SearchHelper();
                adList = sHelper.getNearbyPlaces(sRequest);
            }
        }
        return adList;
    }

    private List<NearbyPlace> getPlacesByGeoCodes(NearbyPlacesRequest request)
            throws CitysearchException {
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PFP_LOCATION_URL)
                + getQueryStringWithGeography(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getPlacesByGeoCodes", ihe);
        }
        return parseXML(responseDocument, request.getLatitude(), request.getLongitude());
    }

    private List<NearbyPlace> getPlacesWithoutGeoCodes(NearbyPlacesRequest request)
            throws CitysearchException {
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PFP_URL)
                + getQueryStringWithoutGeography(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getPlacesWithoutGeoCodes",
                    ihe);
        }
        return parseXML(responseDocument, request.getLatitude(), request.getLongitude());
    }

    private List<NearbyPlace> parseXML(Document doc, String latitude, String longitude)
            throws CitysearchException {
        List<NearbyPlace> adList = new ArrayList<NearbyPlace>();
        if (doc != null && doc.hasRootElement()) {
            Element rootElement = doc.getRootElement();
            List<Element> resultSet = rootElement.getChildren(AD_TAG);
            if (resultSet != null) {
                int size = resultSet.size();
                HashMap<String, String> resultMap;
                for (int i = 0; i < size; i++) {
                    NearbyPlace adListBean = new NearbyPlace();
                    Element ad = (Element) resultSet.get(i);
                    resultMap = processElement(ad);
                    adListBean = createNearbyPlace(resultMap, latitude, longitude);
                    if (adListBean != null)
                        adList.add(adListBean);
                }
            }
        }
        Collections.sort(adList);
        adList = getDisplayList(adList);
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
    public static List<NearbyPlace> getDisplayList(List<NearbyPlace> adList)
            throws CitysearchException {
        List<NearbyPlace> displayList = new ArrayList<NearbyPlace>(3);
        if (adList.size() > CommonConstants.NEARBY_PLACES_DISPLAY_SIZE) {
            for (int i = 0; i < CommonConstants.NEARBY_PLACES_DISPLAY_SIZE; i++) {
                displayList.add(adList.get(i));
            }
        } else {
            displayList = adList;
        }
        displayList = addDefaultImages(displayList);
        return displayList;
    }

    public static List<NearbyPlace> addDefaultImages(List<NearbyPlace> adList)
            throws CitysearchException {
        NearbyPlace adListBean;
        List<String> imageList;
        Random random;
        ArrayList<Integer> indexList = new ArrayList<Integer>(3);
        int imageListSize = 0;
        String imageUrl = "";

        imageList = HelperUtil.getImages();
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
    public static NearbyPlace createNearbyPlace(HashMap<String, String> resultMap, String sLat,
            String sLon) throws CitysearchException {
        NearbyPlace adListBean = null;
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
                adListBean = new NearbyPlace();
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
