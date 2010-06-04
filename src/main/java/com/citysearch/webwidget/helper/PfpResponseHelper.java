package com.citysearch.webwidget.helper;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.citysearch.webwidget.util.CommonConstants;

/**
 * Processes the response from PFP API and returns the individual ad element values in a map
 * 
 * @author Aspert Benjamin
 * 
 */
public class PfpResponseHelper extends ResponseHelper {

    private Logger log = Logger.getLogger(getClass());

    private static final String reviewRatingTag = "overall_review_rating";
    private static final String reviewsTag = "reviews";
    private static final String listingIdTag = "listingId";
    private static final String taglineTag = "tagline";
    private static final String adDisplayURLTag = "ad_display_url";
    private static final String adImageURLTag = "ad_image_url";
    private static final String phoneTag = "phone";

    /**
     * Reads from the Ad element and constructs bean element
     * 
     * @param sLat
     * @return AdListBean
     */
    protected HashMap<String, String> processElement(Element ad) {
        HashMap<String, String> elementMap = new HashMap<String, String>();
        if (ad != null) {
            String name = ad.getChildText(CommonConstants.NAME);
            if (StringUtils.isNotBlank(name)) {
                elementMap.put(CommonConstants.NAME, name);
                elementMap.put(CommonConstants.CITY, ad.getChildText(CommonConstants.CITY));
                elementMap.put(CommonConstants.STATE, ad.getChildText(CommonConstants.STATE));
                elementMap.put(CommonConstants.RATING, ad.getChildText(reviewRatingTag));
                elementMap.put(CommonConstants.REVIEWCOUNT, ad.getChildText(reviewsTag));
                elementMap.put(CommonConstants.LISTING_ID, ad.getChildText(listingIdTag));
                elementMap.put(CommonConstants.CATEGORY, ad.getChildText(taglineTag));
                elementMap.put(CommonConstants.DLAT, ad.getChildText(CommonConstants.LATITUDE));
                elementMap.put(CommonConstants.DLON, ad.getChildText(CommonConstants.LONGITUDE));
                elementMap.put(CommonConstants.PHONE, ad.getChildText(phoneTag));
                elementMap.put(CommonConstants.DISPLAY_URL, ad.getChildText(adDisplayURLTag));
                elementMap.put(CommonConstants.IMAGE_URL, ad.getChildText(adImageURLTag));
            }
        }
        return elementMap;
    }

}
