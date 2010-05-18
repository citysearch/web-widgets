package com.citysearch.processors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.exception.CitysearchException;
import com.citysearch.helper.CommonConstants;
import com.citysearch.helper.PropertiesLoader;

public class RequestHelper {

    private Logger log = Logger.getLogger(getClass());
    private final static String searchQuery = "search";
    private final static String encodeMethod = "UTF-8";
    private final static String lonURL = "lon";
    private final static String latURL = "lat";
    private final static String pfpLocationURLProp = "pfplocation.url";
    private final static String pfpURLProp = "pfp.url";
    private final static String apiKeyParam = "apikey";
    private final static String searchRppKey = "search.rpp";
    private final static String pfpRppKey = "pfp.rpp";
    private final static String searchURL = "search.url";
    private final static String rppParam = "rpp";
    private final static String whatErrMsg = "what.errmsg";
    private final static String publisherErrMsg = "publisher.errmsg";
    private final static String whereErrMsg = "where.errmsg";
    private final static String apiKeyErrMsg = "apikey.errmsg";
    private final static String publisherKey = "publisher";
    private final static String equals = "=";
    private final static String ampersand = "&";
    private final static String error = "urlencode.error";
    private String what;
    private String where;
    private String publisher;
    private String sourceLat;
    private String sourceLon;
    private String tags;
    private String apiKey;
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
    public RequestHelper(Map<String, String[]> map) throws CitysearchException {
        properties = PropertiesLoader.getAPIProperties();
        errorProperties = PropertiesLoader.getErrorProperties();
        this.map = map;
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
        // Added on 05/12/2010 to make it a required parameter
        values = (String[]) map.get(apiKeyParam);
        apiKey = values != null ? values[0] : null;
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

        // Added on 05/12/2010 to make it a required parameter
        if (StringUtils.isBlank(apiKey)) {
            errMsg = errorProperties.getProperty(apiKeyErrMsg);
            error = true;
        }
        if (error) {
            log.error(errMsg);
        }
        return error;
    }

    /**
     * Chacks if lat and lon parameters are passed in the request
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
        } else {
            queryString = getPFPQueryString();
        }
        return queryString;
    }

    /**
     * Constructs PFP api url If parameters lat and lon are present a different url is constructed
     * 
     * @return
     */
    private String getPFPQueryString() throws CitysearchException {
        StringBuffer apiQueryString = new StringBuffer();
        Set<String> keySet = map.keySet();
        Iterator<String> keyIterator = keySet.iterator();
        String value = null;
        String key;
        String values[];
        String queryParam;
        String urlString = null;
        try {
            while (keyIterator.hasNext()) {
                key = keyIterator.next();
                values = map.get(key);
                if (values != null) {
                    value = values[0];
                }
                queryParam = constructQueryParam(key, value);
                apiQueryString.append(queryParam);
            }
            String url;
            if (keySet.contains(latURL) && keySet.contains(lonURL)
                    && StringUtils.isNotBlank(sourceLat) && StringUtils.isNotBlank(sourceLon)) {
                url = properties.getProperty(pfpLocationURLProp);
            } else {
                url = properties.getProperty(pfpURLProp);
            }
            queryParam = constructQueryParam(rppParam, properties.getProperty(pfpRppKey));
            urlString = url + apiQueryString.toString();
        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " getPFPQueryString()";
            log.error(errMsg, excep);
            throw new CitysearchException(errMsg);
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
        queryParam = constructQueryParam(CommonConstants.TAGS, tags);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(apiKeyParam, apiKey);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(rppParam, rppVal);
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
