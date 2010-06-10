package com.citysearch.webwidget.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.NearbyPlace;
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

    private static final String ADDRESS_TAG = "address";
    private static final String LISTING_ID_TAG = "id";
    private static final String REVIEWS_TAG = "userreviewcount";
    private static final String TAGLINE_TAG = "samplecategories";
    private static final String PHONE_TAG = "phonenumber";
    private static final String AD_DISPLAY_URL_TAG = "profile";
    private static final String AD_IMAGE_URL_TAG = "image";
    private static final String REVIEW_RATING_TAG = "rating";
    private static final String LOCATION_TAG = "location";

    private String rootPath;

    public SearchHelper(String rootPath) {
        this.rootPath = rootPath;
    }

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
        log.info("SearchHelper.getClosestLocationPostalCode: Begin");
        validateClosestLocationPostalCodeRequest(request);
        log.info("SearchHelper.getClosestLocationPostalCode: After validate");
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_SEARCH_URL) + getQueryString(request);
        log.info("SearchHelper.getClosestLocationPostalCode: Query " + urlString);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
            log.info("SearchHelper.getClosestLocationPostalCode: Successfull response");
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(),
                    "getClosestLocationPostalCode", ihe.getMessage());
        }
        String nearestListingPostalCode = findClosestLocationPostalCode(responseDocument);
        log.info("SearchHelper.getClosestLocationPostalCode: Postal Code " + nearestListingPostalCode);
        if (nearestListingPostalCode == null) {
            log.info("SearchHelper.getClosestLocationPostalCode: No postal code. Exception.");
            throw new CitysearchException(this.getClass().getName(),
                    "getClosestLocationPostalCode", "No locations found.");
        }
        log.info("SearchHelper.getClosestLocationPostalCode: End");
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
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
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
        log.info("SearchHelper.getLatitudeLongitude: Begin");
        validateRequest(request);
        log.info("SearchHelper.getLatitudeLongitude: After validate");
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_SEARCH_URL)
                + getSearchRequestQueryString(request);
        log.info("SearchHelper.getLatitudeLongitude: Query " + urlString);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
            log.info("SearchHelper.getLatitudeLongitude: Successfull response.");
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getLatitudeLongitude", ihe);
        }
        String[] latLonValues = getLatitudeAndLongitude(responseDocument);
        log.info("SearchHelper.getLatitudeLongitude: Lat & Lon " + latLonValues);
        log.info("SearchHelper.getLatitudeLongitude: End");
        return latLonValues;
    }

    public List<NearbyPlace> getNearbyPlaces(SearchRequest request) throws CitysearchException {
        log.info("SearchHelper.getNearbyPlaces: Begin");
        validateRequest(request);
        log.info("SearchHelper.getNearbyPlaces: After validate");
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_SEARCH_URL)
                + getSearchRequestQueryString(request);
        log.info("SearchHelper.getNearbyPlaces: Query " + urlString);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
            log.info("SearchHelper.getNearbyPlaces: Successfull response");
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getNearbyPlaces", ihe);
        }
        String[] latLonValues = getLatitudeAndLongitude(responseDocument);
        log.info("SearchHelper.getNearbyPlaces: End");
        return parseXML(responseDocument, latLonValues[0], latLonValues[1]);
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

    private List<NearbyPlace> parseXML(Document doc, String latitude, String longitude)
            throws CitysearchException {
        List<NearbyPlace> adList = new ArrayList<NearbyPlace>();
        if (doc != null && doc.hasRootElement()) {
            Element rootElement = doc.getRootElement();
            List<Element> resultSet = rootElement.getChildren(LOCATION_TAG);
            if (resultSet != null) {
                int size = resultSet.size();
                HashMap<String, String> resultMap;
                for (int i = 0; i < size; i++) {
                    NearbyPlace adListBean = new NearbyPlace();
                    Element ad = (Element) resultSet.get(i);
                    resultMap = processElement(ad);
                    adListBean = NearbyPlacesHelper.createNearbyPlace(resultMap, latitude,
                            longitude);
                    if (adListBean != null)
                        adList.add(adListBean);
                }
            }
        }
        if (!adList.isEmpty())
        {
            Collections.sort(adList);
            adList = NearbyPlacesHelper.getDisplayList(adList, this.rootPath);
        }
        return adList;
    }

    /**
     * Reads from the Ad element and constructs bean element
     * 
     * @param sLat
     * @return AdListBean
     */
    private HashMap<String, String> processElement(Element location) {
        HashMap<String, String> elementMap = null;
        if (location != null) {
            String name = location.getChildText(CommonConstants.NAME);
            if (StringUtils.isNotBlank(name)) {
                elementMap = new HashMap<String, String>();
                elementMap.put(CommonConstants.NAME, name);
                Element address = location.getChild(ADDRESS_TAG);
                if (address != null) {
                    elementMap.put(CommonConstants.CITY, address.getChildText(CommonConstants.CITY));
                    elementMap.put(CommonConstants.STATE,
                            address.getChildText(CommonConstants.STATE));
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
                elementMap.put(CommonConstants.OFFERS, location.getChildText(CommonConstants.OFFERS));

            }
        }
        return elementMap;
    }

}
