package com.citysearch.webwidget.helper;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * This class contains functionality for processing the Search API response
 * 
 * @author Aspert
 * 
 */
public class SearchResponseHelper extends ResponseHelper {

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

    /**
     * Parse the response xml received from search api and returns latitude and longitude If proper
     * response is not returned by api, the user will be directed to a default page
     * 
     * @param input
     * @return
     * @throws IOException
     * @throws CitysearchException
     */
    public String[] parseXML(Document doc) throws CitysearchException {
        String[] latLonValues = new String[2];
        try {
            if (doc != null && doc.hasRootElement()) {
                Element rootElement = doc.getRootElement();
                // Getting Source Latitude and Longitude
                Element region = rootElement.getChild(REGION_TAG);
                if (region != null) {
                    String sLat = region.getChildText(CommonConstants.LATITUDE);
                    String sLon = region.getChildText(CommonConstants.LONGITUDE);
                    if (sLat != null && sLon != null) {
                        latLonValues[0] = sLat;
                        latLonValues[1] = sLon;
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
        return latLonValues;
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
}
