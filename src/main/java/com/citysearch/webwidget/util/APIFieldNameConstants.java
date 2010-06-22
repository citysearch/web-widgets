package com.citysearch.webwidget.util;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Contains query parameter constants for all APIs
 *
 * @author Aspert
 *
 */
public class APIFieldNameConstants {

    // Common API Field Names
    public static final String API_KEY = "api_key";
    public static final String PUBLISHER = "publisher";
    public static final String CUSTOMER_ONLY = "customer_only";
    public static final String FORMAT = "format";
    public static final String PLACEMENT = "placement";
    public static final String WHERE = "where";
    public static final String WHAT = "what";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String RADIUS = "radius";
    public static final String TAG = "tag";
    public static final String CALLBACK = "callback";

    //PFP
    public static final String PUBLISHER_CODE = "publishercode";

    // Review API Field Names
    public static final String TAG_ID = "tag_id";
    public static final String TAG_NAME = "tag_name";
    public static final String RATING = "rating";
    public static final String DAYS = "days";
    public static final String MAX = "max";

    // Profile API Field Names
    public static final String LISTING_ID = "listing_id";
    public static final String INFOUSA_ID = "infousa_id";
    public static final String PHONE = "phone";
    public static final String ALL_RESULTS = "all_results";
    public static final String REVIEW_COUNT = "review_count";
    public static final String CLIENT_IP = "client_ip";
    public static final String NO_LOG = "nolog";

    // Offers API Field Names
    public static final String PAGE = "page";
    public static final String RPP = "rpp";
    public static final String EXPIRES_BEFORE = "expires_before";
    public static final String CUSTOMER_HASBUDGET = "customer_hasbudget";
}
