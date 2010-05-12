package com.citysearch.helper.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.exception.CitySearchException;
import com.citysearch.helper.PropertiesLoader;
import com.citysearch.shared.CommonConstants;

public class RequestHelper {

    private Logger log = Logger.getLogger(getClass());
    private final static String searchQuery = "search";
    private final static String encodeMethod = "UTF-8";
    private final static String lonURL = "lon";
    private final static String latURL = "lat";
    private final static String pfpLocationURLProp = "pfpLocationURL";
    private final static String pfpURLProp = "pfpURL";
    private final static String apiKey = "api_key";
    private final static String searchRppKey = "search_rpp";
    private final static String pfpRppKey = "pfp_rpp";
    private final static String searchURL = "searchURL";
    private final static String rppParam = "rpp";
    private String what;
    private String where;
    private String publisher;
    private String sourceLat;
    private String sourceLon;
    private String tags;
    private Map<String, String[]> map;
    private Properties properties;
    private Properties errorProperties;

    /**
     * Gets the api properties file and if the file is not found, an exception will be thrown and
     * user will be redirected to a default page Reads the required parameters from the map
     * 
     * @param map
     * @throws CitySearchException
     */
    public RequestHelper(Map<String, String[]> map) throws CitySearchException {

        properties = PropertiesLoader.apiProperties;
        errorProperties = PropertiesLoader.getErrorProperties();
        if (properties == null) {
            String error = "apiproperties";
            String errMsg = errorProperties.getProperty(error);
            log.error(errMsg);
            throw new CitySearchException(errMsg);
        }

        String values[];
        this.map = map;
        values = (String[]) map.get(CommonConstants.WHAT);
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
    }

    /**
     * Validates the required parameters and if they are missing, the user will be redirected to a
     * default page by throwing Exception
     * 
     * @return
     * @throws CitySearchException
     */
    public boolean validateRequest() throws CitySearchException {
        final String whatErrMsg = "whatErrMsg";
        String errMsg = null;
        boolean error = false;
        if (StringUtils.isBlank(what)) {
            if (StringUtils.isBlank(tags)) {
                errMsg = errorProperties.getProperty(whatErrMsg);
                error = true;
            }
        }
        final String whereErrMsg = "whereErrMsg";
        if (StringUtils.isBlank(where)) {
            if (StringUtils.isBlank(sourceLat) || StringUtils.isBlank(sourceLon)) {
                errMsg = errorProperties.getProperty(whereErrMsg);
                error = true;
            }
        }
        final String publisherErrMsg = "publisherErrMsg";
        if (StringUtils.isBlank(publisher)) {
            errMsg = errorProperties.getProperty(publisherErrMsg);
            error = true;
        }
        log.error(errMsg);
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
     */
    public String getQueryString(String queryType) {
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
    private String getPFPQueryString() {
        StringBuffer apiQueryString = new StringBuffer();
        String key;
        String value;
        String values[];
        String queryParam;

        Set<String> keySet = map.keySet();
        Iterator<String> keyIterator = keySet.iterator();

        while (keyIterator.hasNext()) {
            key = keyIterator.next();
            values = map.get(key);
            value = values[0];
            queryParam = constructQueryParam(key, value);
            apiQueryString.append(queryParam);
        }
        String url;
        if (keySet.contains(latURL) && keySet.contains(lonURL)) {
            url = properties.getProperty(pfpLocationURLProp);
        } else {
            url = properties.getProperty(pfpURLProp);
        }
        queryParam = constructQueryParam(rppParam, properties.getProperty(pfpRppKey));
        String urlString = getURLString(url, apiQueryString.toString());
        return urlString;
    }

    /**
     * Constructs search query url from request parameters
     * 
     * @return
     */
    private String getSearchQueryString() {
        final String publisherKey = "publisher";
        StringBuffer apiQueryString = new StringBuffer();
        String apiVal = properties.getProperty(apiKey);
        String rppVal = properties.getProperty(searchRppKey);
        String queryParam = constructQueryParam(CommonConstants.WHAT, what);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(CommonConstants.WHERE, where);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(publisherKey, publisher);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(CommonConstants.TAGS, tags);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(apiKey, apiVal);
        apiQueryString.append(queryParam);
        queryParam = constructQueryParam(rppParam, rppVal);
        apiQueryString.append(queryParam);
        String urlString = getURLString(properties.getProperty(searchURL),
                apiQueryString.toString());
        return urlString;

    }

    /**
     * Encodes url string as per UTF-8 encoding
     * 
     * @param urlString
     * @param queryString
     * @return
     */
    private String getURLString(String urlString, String queryString) {
        String url;
        try {
            queryString = URLEncoder.encode(queryString, encodeMethod);
        } catch (UnsupportedEncodingException excep) {
            String error = "urlEncodeError";
            String errMsg = errorProperties.getProperty(error);
            log.error(errMsg, excep);
        }
        url = urlString + queryString;
        return url;
    }

    /**
     * Takes the name and value parameters,constructs a sting in the format "&name=value" and
     * returns the string
     */
    private String constructQueryParam(String name, String value) {
        StringBuffer apiQueryString = new StringBuffer();
        if (value != null) {
            final String equals = "=";
            final String ampersand = "&";
            apiQueryString.append(ampersand);
            apiQueryString.append(name);
            apiQueryString.append(equals);
            apiQueryString.append(value);
        }
        return apiQueryString.toString();
    }
}
