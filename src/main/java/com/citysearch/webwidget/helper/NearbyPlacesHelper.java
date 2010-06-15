package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.ProfileRequest;
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
    private static final String AD_TYPE_PFP = "local PFP";
    private static final String AD_TYPE_BACKFILL = "backfill";
    private static final String TYPE_TAG = "type";
    private static final String DESC_TAG = "description";
    private static final String ZIP_TAG = "zip";
    private static final String HTTP_PREFIX = "http://";
    private static final String AD_DESTINATION_URL = "ad_destination_url";

    private String rootPath;
    private Integer displaySize;

    // Field to cache the PFP response document.
    private Document pfpResponseDocument = null;

    public NearbyPlacesHelper(String rootPath) {
        this.rootPath = rootPath;
    }

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
    private String getQueryStringWithLatitudeAndLongitude(NearbyPlacesRequest request)
            throws CitysearchException {
        StringBuilder apiQueryString = new StringBuilder();

        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LATITUDE,
                request.getLatitude()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LONGITUDE,
                request.getLongitude()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER_CODE,
                request.getPublisher()));
        if (!StringUtils.isBlank(request.getTags())) {
            apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
            apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                    request.getTags()));
        }
        if (!StringUtils.isBlank(request.getRadius())) {
            apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
            apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS,
                    request.getRadius()));
        }
        return apiQueryString.toString();
    }

    private String getQueryStringWithWhere(NearbyPlacesRequest request) throws CitysearchException {
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
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER_CODE,
                request.getPublisher()));
        if (!StringUtils.isBlank(request.getTags())) {
            apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
            apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                    request.getTags()));
        }
        if (!StringUtils.isBlank(request.getRadius())) {
            apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
            apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS,
                    request.getRadius()));
        }
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
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER_CODE,
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

        SearchHelper sHelper = new SearchHelper(this.rootPath, this.displaySize);
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
    public NearbyPlacesResponse getNearbyPlaces(NearbyPlacesRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        log.info("NearbyPlacesHelper.getNearbyPlaces: Begin");
        validateRequest(request);
        this.displaySize = request.getDisplaySize();
        if (this.displaySize == null)
        {
            this.displaySize = CommonConstants.DEFAULT_NEARBY_DISPLAY_SIZE;
        }
        log.info("NearbyPlacesHelper.getNearbyPlaces: After validate");
        boolean latitudeLongitudePresentInRequest = true;
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No lat lon. Find Lat and Lon");
            latitudeLongitudePresentInRequest = false;
            loadLatitudeAndLongitudeFromSearchAPI(request);
        }
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No lat lon. excpetion.");
            throw new CitysearchException(this.getClass().getName(), "getNearbyPlaces",
                    "Invalid Latitude and Longitude");
        }

        boolean responseFromSearch = false;
        List<NearbyPlace> nearbyPlaces = getPlacesByGeoCodes(request,
                latitudeLongitudePresentInRequest);
        if (nearbyPlaces == null || nearbyPlaces.isEmpty()) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No results with geography.");
            nearbyPlaces = getPlacesWithoutGeoCodes(request);
            if (nearbyPlaces == null || nearbyPlaces.isEmpty()) {
                responseFromSearch = true;
                log.info("NearbyPlacesHelper.getNearbyPlaces: No results without geography.");
                // Query Search API
                SearchRequest sRequest = new SearchRequest();
                sRequest.setWhat(request.getWhat());
                sRequest.setWhere(request.getWhere());
                sRequest.setTags(request.getTags());
                sRequest.setPublisher(request.getPublisher());

                SearchHelper sHelper = new SearchHelper(this.rootPath, this.displaySize);
                nearbyPlaces = sHelper.getNearbyPlaces(sRequest);
            }
        }

        return createResponse(nearbyPlaces, request, responseFromSearch);
    }

    private NearbyPlacesResponse createResponse(List<NearbyPlace> nearbyPlaces,
            NearbyPlacesRequest request, boolean searchResponse) throws CitysearchException {
        NearbyPlacesResponse response = new NearbyPlacesResponse();

        // When no results from PFP or Search
        if (nearbyPlaces == null || nearbyPlaces.isEmpty()) {
            List<NearbyPlace> backfill = getNearbyPlacesBackfill();
            List<HouseAd> houseAds = null;
            if (backfill == null || backfill.isEmpty()) {
                // If no backfills from PFP, return 3 house ads
                houseAds = HouseAdsHelper.getHouseAds(this.rootPath);
                houseAds = houseAds.subList(0, this.displaySize);
            } else if (backfill.size() < this.displaySize) {
                // If less than 3 backfills found, fill the rest with house ads.
                houseAds = HouseAdsHelper.getHouseAds(this.rootPath);
                int noHouseAdsNeeded = this.displaySize - backfill.size();
                houseAds = houseAds.subList(0, noHouseAdsNeeded);
            }
            response.setBackfill(backfill);
            response.setHouseAds(houseAds);
        } else {
            response.setNearbyPlaces(nearbyPlaces);
            // If the # of results returned by PFP or Search is less than required size
            if (nearbyPlaces.size() < this.displaySize) {
                // If Response was from search
                if (searchResponse) {
                    int moreRequired = this.displaySize - nearbyPlaces.size();
                    List<NearbyPlace> backfill = getNearbyPlacesBackfill();
                    List<HouseAd> houseAds = null;
                    if (backfill == null || backfill.isEmpty()) {
                        houseAds = HouseAdsHelper.getHouseAds(this.rootPath);
                        houseAds = houseAds.subList(0, moreRequired);
                    } else if (backfill.size() < moreRequired) {
                        houseAds = HouseAdsHelper.getHouseAds(this.rootPath);
                        houseAds = houseAds.subList(0, moreRequired - backfill.size());
                    } else if (backfill.size() > moreRequired) {
                        backfill = backfill.subList(0, moreRequired);
                    }
                    response.setBackfill(backfill);
                    response.setHouseAds(houseAds);
                } else {
                    if (nearbyPlaces.size() < this.displaySize) {
                        ProfileHelper phelper = new ProfileHelper(this.rootPath);
                        ProfileRequest profileRequest = new ProfileRequest();
                        profileRequest.setPublisher(request.getPublisher());
                        profileRequest.setClientIP(request.getClientIP());
                        for (NearbyPlace nbp : nearbyPlaces) {
                            profileRequest.setListingId(nbp.getListingId());
                            Profile profile = phelper.getProfileAndHighestReview(profileRequest);
                            nbp.setProfile(profile);
                        }
                    }
                }
            }
        }
        return response;
    }

    public List<NearbyPlace> getNearbyPlacesBackfill() throws CitysearchException {
        return getNearbyPlacesBackfill(pfpResponseDocument);
    }

    private List<NearbyPlace> getPlacesByGeoCodes(NearbyPlacesRequest request,
            boolean latitudeLongitudePresentInRequest) throws CitysearchException {
        log.info("NearbyPlacesHelper.getPlacesByGeoCodes: Begin");
        Properties properties = PropertiesLoader.getAPIProperties();
        StringBuilder urlStringBuilder = null;
        if (latitudeLongitudePresentInRequest) {
            urlStringBuilder = new StringBuilder(properties.getProperty(PFP_LOCATION_URL));
            urlStringBuilder.append(getQueryStringWithLatitudeAndLongitude(request));
        } else {
            urlStringBuilder = new StringBuilder(properties.getProperty(PFP_URL));
            urlStringBuilder.append(getQueryStringWithWhere(request));
        }
        log.info("NearbyPlacesHelper.getPlacesByGeoCodes: Query: " + urlStringBuilder.toString());
        // Document responseDocument = null;
        try {
            pfpResponseDocument = HelperUtil.getAPIResponse(urlStringBuilder.toString());
            log.info("NearbyPlacesHelper.getPlacesByGeoCodes: successful response");
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getPlacesByGeoCodes", ihe);
        }
        return getNearbyPlaces(pfpResponseDocument, request.getLatitude(), request.getLongitude());
    }

    private List<NearbyPlace> getPlacesWithoutGeoCodes(NearbyPlacesRequest request)
            throws CitysearchException {
        log.info("NearbyPlacesHelper.getPlacesWithoutGeoCodes: Begin");
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PFP_URL)
                + getQueryStringWithoutGeography(request);
        log.info("NearbyPlacesHelper.getPlacesWithoutGeoCodes: Query " + urlString);
        Document responseDocument = null;
        try {
            responseDocument = HelperUtil.getAPIResponse(urlString);
            log.info("NearbyPlacesHelper.getPlacesWithoutGeoCodes: Successful response");
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getPlacesWithoutGeoCodes",
                    ihe);
        }
        return getNearbyPlaces(responseDocument, request.getLatitude(), request.getLongitude());
    }

    private List<NearbyPlace> getNearbyPlaces(Document doc, String latitude, String longitude)
            throws CitysearchException {
        log.info("NearbyPlacesHelper.getNearbyPlaces: Begin");
        List<NearbyPlace> nearbyPlaces = null;
        if (doc != null && doc.hasRootElement()) {
            SortedMap<Double, List<Element>> elmsSortedByDistance = new TreeMap<Double, List<Element>>();
            Element rootElement = doc.getRootElement();
            List<Element> children = rootElement.getChildren(AD_TAG);
            if (children != null && !children.isEmpty()) {
                BigDecimal sourceLatitude = new BigDecimal(latitude);
                BigDecimal sourceLongitude = new BigDecimal(longitude);
                for (Element elm : children) {
                    String adType = StringUtils.trim(elm.getChildText(TYPE_TAG));
                    if (adType != null && adType.equalsIgnoreCase(AD_TYPE_PFP)) {
                        BigDecimal businessLatitude = new BigDecimal(
                                elm.getChildText(CommonConstants.LATITUDE));
                        BigDecimal businessLongitude = new BigDecimal(
                                elm.getChildText(CommonConstants.LONGITUDE));
                        double distance = HelperUtil.getDistance(sourceLatitude, sourceLongitude,
                                businessLatitude, businessLongitude);
                        if (distance < CommonConstants.EXTENDED_RADIUS) {
                            if (elmsSortedByDistance.containsKey(distance)) {
                                elmsSortedByDistance.get(distance).add(elm);
                            } else {
                                List<Element> elms = new ArrayList<Element>();
                                elms.add(elm);
                                elmsSortedByDistance.put(distance, elms);
                            }
                        }
                    }
                }
                if (!elmsSortedByDistance.isEmpty()) {
                    List<Element> elmsToConvert = new ArrayList<Element>();
                    for (int j = 0; j < elmsSortedByDistance.size(); j++) {

                        if (elmsToConvert.size() >= this.displaySize) {
                            break;
                        }
                        Double key = elmsSortedByDistance.firstKey();
                        List<Element> elms = elmsSortedByDistance.remove(key);
                        for (int idx = 0; idx < elms.size(); idx++) {
                            if (elmsToConvert.size() == this.displaySize) {
                                break;
                            }
                            elmsToConvert.add(elms.get(idx));
                        }
                    }

                    nearbyPlaces = new ArrayList<NearbyPlace>();
                    for (Element elm : elmsToConvert) {
                        nearbyPlaces.add(toNearbyPlace(elm, latitude, longitude));
                    }
                    addDefaultImages(nearbyPlaces, this.rootPath);
                }
            }
        }
        log.info("NearbyPlacesHelper.getNearbyPlaces: End");
        return nearbyPlaces;
    }

    private NearbyPlace toNearbyPlace(Element ad, String latitude, String longitude)
            throws CitysearchException {
        String dLat = ad.getChildText(CommonConstants.LATITUDE);
        String dLon = ad.getChildText(CommonConstants.LONGITUDE);
        BigDecimal sourceLatitude = new BigDecimal(latitude);
        BigDecimal sourceLongitude = new BigDecimal(longitude);
        BigDecimal businessLatitude = new BigDecimal(dLat);
        BigDecimal businessLongitude = new BigDecimal(dLon);
        double distance = HelperUtil.getDistance(sourceLatitude, sourceLongitude, businessLatitude,
                businessLongitude);

        String rating = ad.getChildText(REVIEW_RATING_TAG);
        String reviewCount = ad.getChildText(REVIEWS_TAG);
        String category = ad.getChildText(TAGLINE_TAG);
        String name = ad.getChildText(CommonConstants.NAME);

        List<Integer> ratingList = HelperUtil.getRatingsList(rating);
        double ratings = HelperUtil.getRatingValue(rating);
        int userReviewCount = HelperUtil.toInteger(reviewCount);
        name = HelperUtil.getAbbreviatedString(name, CommonConstants.BUSINESS_NAME_MAX_LENGTH_PROP,
                CommonConstants.BUSINESS_NAME_MAX_LENGTH);
        category = HelperUtil.getAbbreviatedString(category,
                CommonConstants.TAGLINE_MAX_LENGTH_PROP, CommonConstants.BUSINESS_NAME_MAX_LENGTH);
        String location = HelperUtil.getLocationString(ad.getChildText(CommonConstants.CITY),
                ad.getChildText(CommonConstants.STATE));

        NearbyPlace nearbyPlace = new NearbyPlace();
        nearbyPlace.setName(name);
        nearbyPlace.setLocation(location);
        nearbyPlace.setRating(ratingList);
        nearbyPlace.setReviewCount(userReviewCount);
        nearbyPlace.setDistance(distance);
        nearbyPlace.setListingId(ad.getChildText(LISTING_ID_TAG));
        nearbyPlace.setCategory(category);
        nearbyPlace.setRatings(ratings);
        nearbyPlace.setAdDisplayURL(ad.getChildText(AD_DISPLAY_URL_TAG));
        nearbyPlace.setAdImageURL(ad.getChildText(AD_IMAGE_URL_TAG));
        nearbyPlace.setPhone(ad.getChildText(PHONE_TAG));
        nearbyPlace.setOffers(ad.getChildText(CommonConstants.OFFERS));
        nearbyPlace.setDescription(ad.getChildText(DESC_TAG));
        nearbyPlace.setStreet(ad.getChildText(CommonConstants.STREET));
        nearbyPlace.setCity(ad.getChildText(CommonConstants.CITY));
        nearbyPlace.setState(ad.getChildText(CommonConstants.STATE));
        nearbyPlace.setPostalCode(ad.getChildText(ZIP_TAG));
        nearbyPlace.setAdDestinationUrl(ad.getChildText(AD_DESTINATION_URL));
        return nearbyPlace;
    }

    private List<NearbyPlace> getNearbyPlacesBackfill(Document doc) throws CitysearchException {
        log.info("NearbyPlacesHelper.getNearbyPlacesBackfill: Begin");
        List<NearbyPlace> nearbyPlaces = null;
        if (doc != null && doc.hasRootElement()) {
            List<Element> backfillElms = new ArrayList<Element>();
            Element rootElement = doc.getRootElement();
            List<Element> children = rootElement.getChildren(AD_TAG);
            if (children != null && !children.isEmpty()) {
                for (Element elm : children) {
                    String adType = StringUtils.trim(elm.getChildText(TYPE_TAG));
                    if (adType != null && adType.equalsIgnoreCase(AD_TYPE_BACKFILL)) {
                        backfillElms.add(elm);
                    }
                }
                if (!backfillElms.isEmpty()) {
                    List<Element> elmsToConvert = new ArrayList<Element>();
                    if (backfillElms.size() >= this.displaySize) {
                        for (int idx = 0; idx < this.displaySize; idx++) {
                            elmsToConvert.add(backfillElms.get(idx));
                        }
                    } else {
                        elmsToConvert = backfillElms;
                    }
                    nearbyPlaces = new ArrayList<NearbyPlace>();
                    for (Element elm : elmsToConvert) {
                        nearbyPlaces.add(toBackfill(elm));
                    }
                }
            }
        }
        log.info("NearbyPlacesHelper.getNearbyPlacesBackfill: End");
        return nearbyPlaces;
    }

    private NearbyPlace toBackfill(Element ad) {
        NearbyPlace nbp = new NearbyPlace();
        String category = ad.getChildText(TAGLINE_TAG);
        if (StringUtils.isNotBlank(category)) {
            category = category.replaceAll("<b>", "");
            category = category.replaceAll("</b>", "");
            nbp.setCategory(category);
        }
        nbp.setAdImageURL(ad.getChildText(AD_IMAGE_URL_TAG));
        String description = ad.getChildText(DESC_TAG);
        if (StringUtils.isNotBlank(description)) {
            description = description.replaceAll("<b>", "");
            description = description.replaceAll("</b>", "");
            nbp.setDescription(description);
        }
        nbp.setOffers(ad.getChildText(CommonConstants.OFFERS));
        nbp.setAdDisplayURL(ad.getChildText(AD_DISPLAY_URL_TAG));
        nbp.setAdDestinationUrl(ad.getChildText(AD_DESTINATION_URL));
        return nbp;
    }

    // TODO: Refactor!!!
    public static List<NearbyPlace> addDefaultImages(List<NearbyPlace> nearbyPlaces, String path)
            throws CitysearchException {
        NearbyPlace nearbyPlace;
        List<String> imageList;
        ArrayList<Integer> indexList = new ArrayList<Integer>(3);
        int imageListSize = 0;
        String imageUrl = "";

        imageList = HelperUtil.getImages(path);
        Random randomizer = new Random();
        int size = nearbyPlaces.size();

        for (int i = 0; i < size; i++) {
            nearbyPlace = nearbyPlaces.get(i);
            imageUrl = nearbyPlace.getAdImageURL();
            if (StringUtils.isBlank(imageUrl)) {
                int index = 0;
                imageListSize = imageList.size();
                if (imageListSize > 0) {
                    do {
                        index = randomizer.nextInt(imageListSize);
                    } while (indexList.contains(index));
                    indexList.add(index);
                    imageUrl = imageList.get(index);
                    nearbyPlace.setAdImageURL(imageUrl);
                }
            }
            nearbyPlaces.set(i, nearbyPlace);
        }
        return nearbyPlaces;
    }
}
