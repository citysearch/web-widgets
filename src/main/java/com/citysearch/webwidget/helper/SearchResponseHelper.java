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

public class SearchResponseHelper extends ResponseHelper {

    private Logger log = Logger.getLogger(getClass());
    private static final String regionTag = "region";
    private static final String addressTag = "address";
    private static final String listingIdTag = "id";
    private static final String reviewsTag = "userreviewcount";
    private static final String taglineTag = "samplecategories";
    private static final String phoneTag = "phonenumber";
    private static final String adDisplayURLTag = "profile";
    private static final String adImageURLTag = "image";
    private static final String reviewRatingTag = "rating";

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
                Element region = rootElement.getChild(regionTag);
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
                Element address = location.getChild(addressTag);
                if (address != null) {
                    elementMap.put(CommonConstants.CITY,
                            location.getChildText(CommonConstants.CITY));
                    elementMap.put(CommonConstants.STATE,
                            location.getChildText(CommonConstants.STATE));
                }
                elementMap.put(CommonConstants.RATING, location.getChildText(reviewRatingTag));
                elementMap.put(CommonConstants.REVIEWCOUNT, location.getChildText(reviewsTag));
                elementMap.put(CommonConstants.CATEGORY, location.getChildText(taglineTag));
                elementMap.put(CommonConstants.DLAT,
                        location.getChildText(CommonConstants.LATITUDE));
                elementMap.put(CommonConstants.DLON,
                        location.getChildText(CommonConstants.LONGITUDE));
                elementMap.put(CommonConstants.PHONE, location.getChildText(phoneTag));
                elementMap.put(CommonConstants.LISTING_ID, location.getAttributeValue(listingIdTag));
                elementMap.put(CommonConstants.DISPLAY_URL, location.getChildText(adDisplayURLTag));
                elementMap.put(CommonConstants.IMAGE_URL, location.getChildText(adImageURLTag));

            }
        }
        return elementMap;
    }
}
