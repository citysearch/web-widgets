package com.citysearch.webwidget.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * Helper class for constructing Profile and Search API requests
 * 
 * @author Aspert
 * 
 */
public class RequestHelper {

    private Logger log = Logger.getLogger(getClass());
    private final static String searchQuery = "search";
    private final static String encodeMethod = "UTF-8";
    private final static String lonURL = "lon";
    private final static String latURL = "lat";
    private final static String pfpLocationURLProp = "pfplocation.url";
    private final static String pfpURLProp = "pfp.url";
    private final static String searchRppKey = "search.rpp";
    private final static String pfpRppKey = "pfp.rpp";
    private final static String searchURL = "search.url";
    private final static String rppParam = "rpp";
    private final static String whatErrMsg = "what.errmsg";
    private final static String publisherErrMsg = "publisher.errmsg";
    private final static String whereErrMsg = "where.errmsg";
    private final static String publisherKey = "publisher";
    private final static String equals = "=";
    private final static String ampersand = "&";
    private final static String error = "urlencode.error";
    private final static String defaultRadius = "default.radius";
    private final static String radiusParam = "radius";
    private String what;
    private String where;
    private String publisher;
    private String sourceLat;
    private String sourceLon;
    private String tags;
    private String radius;
    private Map<String, String[]> map;
    private Properties properties;
    private Properties errorProperties;

    /**
     * Gets the api properties file and if the file is not found, an exception will be thrown and
     * user will be redirected to a default page Reads the required parameters from the map
     * 
     * @param map
     * @throws CitysearchException
     */
    public RequestHelper(Map<String, String[]> reqMap) throws CitysearchException {
        properties = PropertiesLoader.getAPIProperties();
        errorProperties = PropertiesLoader.getErrorProperties();
        map = reqMap;
        String values[] = (String[]) map.get(CommonConstants.WHAT);
        what = values != null ? values[0] : null;
        values = (String[]) map.get(CommonConstants.WHERE);
        where = values != null ? values[0] : null;
        values = (String[]) map.get(CommonConstants.PUBLISHER_CODE);
        publisher = values != null ? values[0] : null;
        values = (String[]) map.get(CommonConstants.LAT_URL);
        sourceLat = values != null ? values[0] : null;
        values = (String[]) map.get(CommonConstants.LON_URL);
        sourceLon = values != null ? values[0] : null;
        values = (String[]) map.get(CommonConstants.TAGS);
        tags = values != null ? values[0] : null;
        // Added on 05/17/2010 to read radius
        values = (String[]) map.get(CommonConstants.RADIUS);
        radius = values != null ? values[0] : null;
        if (StringUtils.isBlank(radius)) {
            radius = properties.getProperty(defaultRadius);
        }
    }

    /**
     * Validates the required parameters and if they are missing, the user will be redirected to a
     * default page by throwing Exception
     * 
     * @return
     * @throws CitysearchException
     */
    public boolean validateRequest() throws CitysearchException {
        String errMsg = null;
        boolean error = false;
        if (StringUtils.isBlank(what)) {
            if (StringUtils.isBlank(tags)) {
                errMsg = errorProperties.getProperty(whatErrMsg);
                error = true;
            }
        }
        if (StringUtils.isBlank(where)) {
            if (StringUtils.isBlank(sourceLat) || StringUtils.isBlank(sourceLon)) {
                errMsg = errorProperties.getProperty(whereErrMsg);
                error = true;
            }
        }
        if (StringUtils.isBlank(publisher)) {
            errMsg = errorProperties.getProperty(publisherErrMsg);
            error = true;
        }

        if (error) {
            log.error(errMsg);
        }
        return error;
    }

    /**
     * Checks if lat and lon parameters are passed in the request
     * 
     * @return
     */
    public boolean validateLatLon() {
        boolean latLonFlag = true;
        if (StringUtils.isBlank(sourceLat) || StringUtils.isBlank(sourceLon)) {
            latLonFlag = false;
        }
        return latLonFlag;
    }

    /**
     * Returns latitude and longitude sent in the url
     * 
     * @return
     */
    public String[] getSourceLatLon() {
        String sourceLatLon[] = new String[2];
        sourceLatLon[0] = sourceLat;
        sourceLatLon[1] = sourceLon;
        return sourceLatLon;
    }

    /**
     * 
     * If querytype is "search". search url is constructed Otherwise, if querytype is "pfp" or null,
     * pfp url is constructed.
     * 
     * @param queryType
     * @return
     * @throws CitysearchException
     */
    public String getQueryString(String queryType) throws CitysearchException {
        String queryString = null;
        if (queryType.equalsIgnoreCase(searchQuery)) {
            queryString = getSearchQueryString();
        } else if (queryType.equalsIgnoreCase(CommonConstants.PFP_WITHOUT_GEOGRAPHY)) {
            queryString = getPFPQueryString(false);
        } else {
            queryString = getPFPQueryString(true);
        }
        return queryString;
    }

    /**
     * Constructs PFP api url If parameters lat and lon are present a different url is constructed
     * Removes parameters not required in the query from the map and then constructs query. E.g. if
     * we want to construct pfp api query without any geographical parameters, first we remove
     * where,lat and lon parameters from the map and then construct query string with the remaining
     * parameters
     * 
     * @return
     */
    private String getPFPQueryString(boolean geography) throws CitysearchException {
        StringBuffer apiQueryString = new StringBuffer();
        Map<String, String[]> queryMap = new HashMap<String, String[]>();
        queryMap.putAll(map);
        String value = null;
        String key;
        String values[];
        String queryParam;
        String urlString = null;
        try {
            String url;
            Set<String> keySet = queryMap.keySet();
            if (!geography) {
                url = properties.getProperty(pfpURLProp);
                queryMap.remove(CommonConstants.WHERE);
                queryMap.remove(latURL);
                queryMap.remove(lonURL);
            } else if (keySet.contains(latURL) && keySet.contains(lonURL)
                    && StringUtils.isNotBlank(sourceLat) && StringUtils.isNotBlank(sourceLon)) {
                url = properties.getProperty(pfpLocationURLProp);
                queryMap.remove(where);
            } else {
                url = properties.getProperty(pfpURLProp);
            }
            Iterator<String> keyIterator = keySet.iterator();
            while (keyIterator.hasNext()) {
                key = keyIterator.next();
                values = queryMap.get(key);
                if (values != null) {
                    value = values[0];
                }
                queryParam = constructQueryParam(key, value);
                apiQueryString.append(queryParam);
            }
            queryParam = constructQueryParam(rppParam, properties.getProperty(pfpRppKey));
            if (!keySet.contains(radiusParam)) {
                queryParam = constructQueryParam(radiusParam, radius);
            }
            urlString = url + apiQueryString.toString();
        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " getPFPQueryString()";
            log.error(errMsg, excep);
            throw new CitysearchException(this.getClass().getName(), "getPFPQueryString", errMsg);
        }
        return urlString;
    }

    /**
     * Constructs search query url from request parameters
     * 
     * @return
     */
    private String getSearchQueryString() {
        StringBuffer apiQueryString = new StringBuffer();
        String rppVal = properties.getProperty(searchRppKey);
        String queryParam = constructQueryParam(CommonConstants.WHAT, what);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(CommonConstants.WHERE, where);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(publisherKey, publisher);
        apiQueryString.append(queryParam);
        // Modified from "tags" to "tag" as per Search API - 05/17/2010
        queryParam = constructQueryParam(CommonConstants.TAG_SEARCH, tags);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(CommonConstants.LAT_URL, sourceLat);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(CommonConstants.LON_URL, sourceLon);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(rppParam, rppVal);
        apiQueryString.append(queryParam);
        // Added on 05/17/2010 to send defalut radius, if radius not present
        queryParam = constructQueryParam(radiusParam, radius);
        apiQueryString.append(queryParam);
        String urlString = properties.getProperty(searchURL) + apiQueryString.toString();
        return urlString;

    }

    /**
     * Takes the name and value parameters,constructs a sting in the format "&name=value" and
     * returns the string
     */
    private String constructQueryParam(String name, String value) {
        StringBuffer apiQueryString = new StringBuffer();
        if (StringUtils.isNotBlank(value)) {
            apiQueryString.append(ampersand);
            apiQueryString.append(name);
            apiQueryString.append(equals);
            try {
                value = URLEncoder.encode(value, encodeMethod);
            } catch (UnsupportedEncodingException excep) {
                String errMsg = errorProperties.getProperty(error);
                log.error(errMsg, excep);
            }
            apiQueryString.append(value);
        }
        return apiQueryString.toString();
    }
}
