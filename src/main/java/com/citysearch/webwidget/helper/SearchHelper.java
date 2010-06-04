package com.citysearch.webwidget.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdom.Document;
import org.jdom.Element;

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
    private void validateLatitudeLongitudeRequest(SearchRequest request)
            throws InvalidRequestParametersException, CitysearchException {
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
    private String getLatitudeLongitudeRequestQueryString(SearchRequest request)
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
        validateLatitudeLongitudeRequest(request);
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_SEARCH_URL)
                + getLatitudeLongitudeRequestQueryString(request);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getLatitudeLongitude",
                    ihe.getMessage());
        }
        String[] latLonValues = new String[2];
        if (responseDocument != null && responseDocument.hasRootElement()) {
            Element rootElement = responseDocument.getRootElement();
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
}
