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
    private static final String AD_DESTINATION_URL = "ad_destination_url";
    private static final String DEFAULT_RADIUS = "25";

    private String rootPath;
    private Integer displaySize;

    // Field to cache the PFP response document.
    private Document pfpWithGeoResponseDocument = null;
    private Document pfpWithOutGeoResponseDocument = null;

    public NearbyPlacesHelper(String rootPath) throws CitysearchException {
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
        /*
         * PFP is not accepting publisher with lat lon
         * apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
         * apiQueryString.append(HelperUtil.
         * constructQueryParam(APIFieldNameConstants.PUBLISHER_CODE, request.getPublisher()));
         */
        if (!StringUtils.isBlank(request.getTags())) {
            apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
            apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                    request.getTags()));
        }

        String radius = (StringUtils.isBlank(request.getRadius())) ? DEFAULT_RADIUS
                : request.getRadius();
        apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
        apiQueryString.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS, radius));

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
        SearchRequest sRequest = new SearchRequest(request);
        sRequest.setWhat(request.getWhat());
        sRequest.setWhere(request.getWhere());
        sRequest.setTags(request.getTags());

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
        if (this.displaySize == null) {
            this.displaySize = CommonConstants.DEFAULT_NEARBY_DISPLAY_SIZE;
        }
        log.info("NearbyPlacesHelper.getNearbyPlaces: After validate");
        boolean latitudeLongitudePresentInRequest = true;
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No lat lon. Find Lat and Lon");
            latitudeLongitudePresentInRequest = false;
            loadLatitudeAndLongitudeFromSearchAPI(request);
            if (StringUtils.isBlank(request.getRadius())) {
                request.setRadius(String.valueOf(CommonConstants.EXTENDED_RADIUS));
            }
        }
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No lat lon. return house ads.");
            throw new CitysearchException(this.getClass().getName(), "getNearbyPlaces",
                    "Invalid Latitude and Longitude");
        }

        List<NearbyPlace> nearbyPlaces = getPlacesByGeoCodes(request,
                latitudeLongitudePresentInRequest);
        if (nearbyPlaces == null || nearbyPlaces.isEmpty()) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No results with geography.");
            nearbyPlaces = getPlacesWithoutGeoCodes(request);
        }

        return createResponse(nearbyPlaces, request);
    }

    private NearbyPlacesResponse createResponse(List<NearbyPlace> nearbyPlaces,
            NearbyPlacesRequest request) throws CitysearchException {
        NearbyPlacesResponse response = new NearbyPlacesResponse();

        int noOfBackFillNeeded = (nearbyPlaces == null || nearbyPlaces.isEmpty()) ? this.displaySize
                : this.displaySize - nearbyPlaces.size();
        List<NearbyPlace> backfill = null;
        List<HouseAd> houseAds = null;
        List<NearbyPlace> searchResults = null;
        // If no results from PFP or PFP results size is less than required for Conquest
        if (noOfBackFillNeeded == this.displaySize
                || (noOfBackFillNeeded > 0 && request.getAdUnitSize().equals(
                        CommonConstants.CONQUEST_AD_SIZE))) {
            backfill = getNearbyPlacesBackfill(request);
            int noOfSearchResultsNeeded = (backfill == null || backfill.isEmpty()) ? noOfBackFillNeeded
                    : noOfBackFillNeeded - backfill.size();
            if (noOfSearchResultsNeeded > 0) {
                searchResults = getSearchResults(request, noOfSearchResultsNeeded);
                int noOfHouseAdsNeeded = (searchResults == null || searchResults.isEmpty()) ? noOfSearchResultsNeeded
                        : noOfSearchResultsNeeded - searchResults.size();
                if (noOfHouseAdsNeeded > 0) {
                    houseAds = HouseAdsHelper.getHouseAds(this.rootPath,
                            request.getDartClickTrackUrl());
                    houseAds = houseAds.subList(0, noOfHouseAdsNeeded);
                } else if (noOfHouseAdsNeeded < 0) {
                    searchResults = searchResults.subList(0, noOfSearchResultsNeeded);
                }
            } else if (noOfSearchResultsNeeded < 0) {
                backfill = backfill.subList(0, noOfBackFillNeeded);
            }
        } else if (noOfBackFillNeeded > 0
                && !request.getAdUnitSize().equals(CommonConstants.CONQUEST_AD_SIZE)) {
            // Less than required PFP results found for Mantel read the reviews from Profile API
            ProfileRequest profileRequest = new ProfileRequest(request);
            profileRequest.setClientIP(request.getClientIP());
            ProfileHelper phelper = new ProfileHelper(this.rootPath);
            for (NearbyPlace nbp : nearbyPlaces) {
                profileRequest.setListingId(nbp.getListingId());
                Profile profile = phelper.getProfileAndHighestReview(profileRequest);
                nbp.setProfile(profile);
            }
        }
        response.setNearbyPlaces(nearbyPlaces);
        response.setBackfill(backfill);
        response.setSearchResults(searchResults);
        response.setHouseAds(houseAds);
        return response;
    }

    @Deprecated
    private NearbyPlacesResponse createResponse(List<NearbyPlace> nearbyPlaces,
            NearbyPlacesRequest request, boolean searchResponse) throws CitysearchException {
        NearbyPlacesResponse response = new NearbyPlacesResponse();

        // When no results from PFP or Search
        if (nearbyPlaces == null || nearbyPlaces.isEmpty()) {
            List<NearbyPlace> backfill = getNearbyPlacesBackfill(request);
            List<HouseAd> houseAds = null;
            if (backfill == null || backfill.isEmpty()) {
                // If no backfills from PFP, return 3 house ads
                houseAds = HouseAdsHelper.getHouseAds(this.rootPath, request.getDartClickTrackUrl());
                houseAds = houseAds.subList(0, this.displaySize);
            } else if (backfill.size() < this.displaySize) {
                // If less than 3 backfills found, fill the rest with house ads.
                houseAds = HouseAdsHelper.getHouseAds(this.rootPath, request.getDartClickTrackUrl());
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
                    List<NearbyPlace> backfill = getNearbyPlacesBackfill(request);
                    List<HouseAd> houseAds = null;
                    if (backfill == null || backfill.isEmpty()) {
                        houseAds = HouseAdsHelper.getHouseAds(this.rootPath,
                                request.getDartClickTrackUrl());
                        houseAds = houseAds.subList(0, moreRequired);
                    } else if (backfill.size() < moreRequired) {
                        houseAds = HouseAdsHelper.getHouseAds(this.rootPath,
                                request.getDartClickTrackUrl());
                        houseAds = houseAds.subList(0, moreRequired - backfill.size());
                    } else if (backfill.size() > moreRequired) {
                        backfill = backfill.subList(0, moreRequired);
                    }
                    response.setBackfill(backfill);
                    response.setHouseAds(houseAds);
                } else {
                    // If Conquest
                    if (this.displaySize == 2) {
                        // Add 1 backfill or 1 house ad
                        List<NearbyPlace> backfill = getNearbyPlacesBackfill(request);
                        List<HouseAd> houseAds = null;
                        if (backfill == null || backfill.isEmpty()) {
                            houseAds = HouseAdsHelper.getHouseAds(this.rootPath,
                                    request.getDartClickTrackUrl());
                            houseAds = houseAds.subList(0, 1);
                        } else if (backfill.size() > 1) {
                            backfill = backfill.subList(0, 1);
                        }
                        response.setBackfill(backfill);
                        response.setHouseAds(houseAds);
                    } else {
                        // If Mantel
                        ProfileRequest profileRequest = new ProfileRequest(request);
                        profileRequest.setClientIP(request.getClientIP());

                        ProfileHelper phelper = new ProfileHelper(this.rootPath);
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

    private List<NearbyPlace> getSearchResults(NearbyPlacesRequest request,
            int maxNoOfResultsRequired) throws CitysearchException {
        SearchRequest sRequest = new SearchRequest(request);
        sRequest.setWhat(request.getWhat());
        sRequest.setWhere(request.getWhere());
        sRequest.setLatitude(request.getLatitude());
        sRequest.setLongitude(request.getLongitude());
        sRequest.setRadius(request.getRadius());
        sRequest.setTags(request.getTags());
        SearchHelper sHelper = new SearchHelper(this.rootPath, maxNoOfResultsRequired);
        return sHelper.getNearbyPlaces(sRequest);
    }

    private List<NearbyPlace> getNearbyPlacesBackfill(NearbyPlacesRequest request)
            throws CitysearchException {
        List<NearbyPlace> backFillFromPFPWithGeo = getNearbyPlacesBackfill(request,
                pfpWithGeoResponseDocument);
        List<NearbyPlace> backFillFromPFPWithOutGeo = getNearbyPlacesBackfill(request,
                pfpWithOutGeoResponseDocument);
        List<NearbyPlace> backfill = new ArrayList<NearbyPlace>();
        if (backFillFromPFPWithGeo != null && !backFillFromPFPWithGeo.isEmpty()) {
            backfill.addAll(backFillFromPFPWithGeo);
        }
        if (backFillFromPFPWithOutGeo != null && !backFillFromPFPWithOutGeo.isEmpty()) {
            backfill.addAll(backFillFromPFPWithOutGeo);
        }
        return backfill;
    }

    private List<NearbyPlace> getPlacesByGeoCodes(NearbyPlacesRequest request,
            boolean latitudeLongitudePresentInRequest) throws CitysearchException {
        log.info("NearbyPlacesHelper.getPlacesByGeoCodes: Begin");
        Properties properties = PropertiesLoader.getAPIProperties();
        StringBuilder urlStringBuilder = null;
        urlStringBuilder = new StringBuilder(properties.getProperty(PFP_LOCATION_URL));
        urlStringBuilder.append(getQueryStringWithLatitudeAndLongitude(request));
        log.info("NearbyPlacesHelper.getPlacesByGeoCodes: Query: " + urlStringBuilder.toString());
        // Document responseDocument = null;
        try {
            pfpWithGeoResponseDocument = HelperUtil.getAPIResponse(urlStringBuilder.toString(),
                    null);
            log.info("NearbyPlacesHelper.getPlacesByGeoCodes: successful response");
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getPlacesByGeoCodes", ihe);
        }
        return getNearbyPlaces(request, pfpWithGeoResponseDocument);
    }

    private List<NearbyPlace> getPlacesWithoutGeoCodes(NearbyPlacesRequest request)
            throws CitysearchException {
        log.info("NearbyPlacesHelper.getPlacesWithoutGeoCodes: Begin");
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PFP_URL)
                + getQueryStringWithoutGeography(request);
        log.info("NearbyPlacesHelper.getPlacesWithoutGeoCodes: Query " + urlString);
        try {
            pfpWithOutGeoResponseDocument = HelperUtil.getAPIResponse(urlString, null);
            log.info("NearbyPlacesHelper.getPlacesWithoutGeoCodes: Successful response");
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getPlacesWithoutGeoCodes",
                    ihe);
        }
        return getNearbyPlaces(request, pfpWithOutGeoResponseDocument);
    }

    private List<NearbyPlace> getNearbyPlaces(NearbyPlacesRequest request, Document doc)
            throws CitysearchException {
        log.info("NearbyPlacesHelper.getNearbyPlaces: Begin");
        List<NearbyPlace> nearbyPlaces = null;
        if (doc != null && doc.hasRootElement()) {
            SortedMap<Double, List<Element>> elmsSortedByDistance = new TreeMap<Double, List<Element>>();
            Element rootElement = doc.getRootElement();
            List<Element> children = rootElement.getChildren(AD_TAG);
            if (children != null && !children.isEmpty()) {
                BigDecimal sourceLatitude = new BigDecimal(request.getLatitude());
                BigDecimal sourceLongitude = new BigDecimal(request.getLongitude());
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
                        nearbyPlaces.add(toNearbyPlace(request, elm));
                    }
                    addDefaultImages(nearbyPlaces, this.rootPath);
                }
            }
        }
        log.info("NearbyPlacesHelper.getNearbyPlaces: End");
        return nearbyPlaces;
    }

    private NearbyPlace toNearbyPlace(NearbyPlacesRequest request, Element ad)
            throws CitysearchException {
        String dLat = ad.getChildText(CommonConstants.LATITUDE);
        String dLon = ad.getChildText(CommonConstants.LONGITUDE);
        BigDecimal sourceLatitude = new BigDecimal(request.getLatitude());
        BigDecimal sourceLongitude = new BigDecimal(request.getLongitude());
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

        nearbyPlace.setCallBackFunction(request.getCallBackFunction());
        nearbyPlace.setCallBackUrl(request.getCallBackUrl());

        String adDisplayTrackingUrl = HelperUtil.getTrackingUrl(nearbyPlace.getAdDisplayURL(),
                request.getCallBackUrl(), request.getDartClickTrackUrl(),
                nearbyPlace.getListingId(), nearbyPlace.getPhone(), request.getPublisher(),
                request.getAdUnitName(), request.getAdUnitSize());
        nearbyPlace.setAdDisplayTrackingURL(adDisplayTrackingUrl);

        String callBackFn = HelperUtil.getCallBackFunctionString(request.getCallBackFunction(),
                nearbyPlace.getListingId(), nearbyPlace.getPhone());
        nearbyPlace.setCallBackFunction(callBackFn);

        return nearbyPlace;
    }

    private List<NearbyPlace> getNearbyPlacesBackfill(NearbyPlacesRequest request, Document doc)
            throws CitysearchException {
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
                        nearbyPlaces.add(toBackfill(request, elm));
                    }
                }
            }
        }
        log.info("NearbyPlacesHelper.getNearbyPlacesBackfill: End");
        return nearbyPlaces;
    }

    private NearbyPlace toBackfill(NearbyPlacesRequest request, Element ad)
            throws CitysearchException {
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
        nbp.setListingId(ad.getChildText(LISTING_ID_TAG));
        nbp.setPhone(ad.getChildText(PHONE_TAG));

        String adDisplayTrackingUrl = HelperUtil.getTrackingUrl(nbp.getAdDisplayURL(), null,
                request.getDartClickTrackUrl(), nbp.getListingId(), nbp.getPhone(),
                request.getPublisher(), request.getAdUnitName(), request.getAdUnitSize());

        nbp.setAdDisplayTrackingURL(adDisplayTrackingUrl);
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
