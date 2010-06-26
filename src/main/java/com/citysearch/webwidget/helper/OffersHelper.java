package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.OffersRequest;
import com.citysearch.webwidget.bean.OffersResponse;
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

public class OffersHelper {

    private final static String PROPERTY_OFFERS_URL = "offers.url";
    private Logger log = Logger.getLogger(getClass());
    private String rootPath;

    private static final String OFFER = "offer";
    private static final String RPP_OFFERS = "2";
    private static final String CITY = "city";
    private static final String ATTRIBUTION_SOURCE = "attribution_source";
    private static final String CS_RATING = "cs_rating";
    private static final String REVIEW_COUNT = "review_count";
    private static final String IMAGE_URL = "image_url";
    private static final String LATITUDE = "latitude";
    private static final String LISTING_ID = "listing_id";
    private static final String LISTING_NAME = "listing_name";
    private static final String LONGITUDE = "longitude";
    private static final String OFFER_DESCRIPTION = "offer_description";
    private static final String OFFER_ID = "offer_id";
    private static final String OFFER_TITLE = "offer_title";
    private static final String REFERENCE_ID = "reference_id";
    private static final String STATE = "state";
    private static final String STREET = "street";
    private static final String ZIP = "zip";
    private static final String PUBLISHER_HEADER = "X-Publisher";
    private static final String OFFER_ANCHOR = "#target-couponLink";
    private Integer displaySize = 1;
    private static final String CONQUEST_AD = "conquestAd";

    public OffersHelper(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Constructs the Offers API query string with all the supplied parameters
     * 
     * @return String
     * @throws CitysearchException
     */
    private String getQueryString(OffersRequest request) throws CitysearchException {
        log.info("Start offersHelper getQueryString()");
        StringBuilder strBuilder = new StringBuilder();
        /*
         * strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
         * request.getPublisher().trim())); strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
         */
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RPP, RPP_OFFERS));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);

        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CLIENT_IP,
                request.getClientIP()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);

        if (!StringUtils.isBlank(request.getWhat())) {
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                    request.getWhat().trim()));
        }
        if (!StringUtils.isBlank(request.getWhere())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHERE,
                    request.getWhere().trim()));
        } else {
            if (!StringUtils.isBlank(request.getLatitude())) {
                strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
                strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LATITUDE,
                        request.getLatitude().trim()));
            }
            if (!StringUtils.isBlank(request.getLongitude())) {
                strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
                strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LONGITUDE,
                        request.getLongitude().trim()));
            }
        }
        if (!StringUtils.isBlank(request.getTag())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                    request.getTag().trim()));
        }
        if (!StringUtils.isBlank(request.getExpiresBefore())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.EXPIRES_BEFORE,
                    request.getExpiresBefore().trim()));
        }
        if (!StringUtils.isBlank(request.getCustomerHasbudget())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(
                    APIFieldNameConstants.CUSTOMER_HASBUDGET,
                    String.valueOf(request.getCustomerHasbudget().trim())));
        }
        if (!StringUtils.isBlank(request.getRadius())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS,
                    request.getRadius().trim()));
        }
        if (!StringUtils.isBlank(request.getCallbackFunction())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CALLBACK,
                    request.getCallbackFunction().trim()));
        }
        log.info("Start offersHelper getQueryString() querystring is " + strBuilder);
        return strBuilder.toString();
    }

    /**
     * Validates the request. If any of the parameters are missing, throws Citysearch Exception
     * 
     * @throws CitysearchException
     */
    public void validateRequest(OffersRequest request) throws InvalidRequestParametersException,
            CitysearchException {
        log.info("Start offersHelper validateRequest()");
        List<String> errors = new ArrayList<String>();
        Properties errorProperties = PropertiesLoader.getErrorProperties();

        if (StringUtils.isBlank(request.getPublisher())) {
            errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
        }
        if (!StringUtils.isBlank(request.getRadius())
                && !StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())
                && !StringUtils.isBlank(request.getWhere())) {
            errors.add(errorProperties.getProperty(CommonConstants.LOCATION_ERROR));
        }
        if ((StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude()) || StringUtils.isBlank(request.getRadius()))
                && (StringUtils.isBlank(request.getWhere()))) {
            errors.add(errorProperties.getProperty(CommonConstants.WHERE_ERROR_CODE));
        }
        if (!StringUtils.isBlank(request.getLatitude())
                && StringUtils.isBlank(request.getLongitude())) {
            errors.add(errorProperties.getProperty(CommonConstants.LONGITUDE_ERROR));
        } else if (StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())) {
            errors.add(errorProperties.getProperty(CommonConstants.LATITUDE_ERROR));
        }
        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())
                && (StringUtils.isBlank(request.getRadius())
                        || (new Integer(request.getRadius()).intValue() > 25) || (new Integer(
                        request.getRadius()).intValue() < 1))) {
            errors.add(errorProperties.getProperty(CommonConstants.RADIUS_ERROR));
        }
        if (StringUtils.isBlank(request.getWhere())) {
            errors.add(errorProperties.getProperty(CommonConstants.ZIPCODE_ERROR));
        }
        if (StringUtils.isBlank(request.getClientIP())) {
            errors.add(errorProperties.getProperty(CommonConstants.CLIENT_IP_ERROR_CODE));
        }
        if (!errors.isEmpty()) {
            throw new InvalidRequestParametersException(this.getClass().getName(),
                    "validateRequest", "Invalid parameters.", errors);
        }
        log.info("End offersHelper validateRequest()");
    }

    /**
     * Get the offers from Offers API
     * 
     * @param request
     * @return List of Offers
     * @throws InvalidRequestParametersException
     * @throws CitysearchException
     */
    public OffersResponse getOffers(OffersRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        log.info("Start offersHelper getOffers()");
        validateRequest(request);

        // set lat long for ConquestAD for distance calculation
        if (request.getAdUnitName().equals(CONQUEST_AD)
                && (StringUtils.isBlank(request.getLatitude()) || StringUtils.isBlank(request.getLongitude()))) {
            log.info("OffersHelper.getOffers: No lat lon. Find Lat and Lon");
            loadLatitudeAndLongitudeFromSearchAPI(request);
        }

        Properties properties = PropertiesLoader.getAPIProperties();
        StringBuilder urlString = new StringBuilder(properties.getProperty(PROPERTY_OFFERS_URL));
        urlString.append(getQueryString(request));
        Document responseDocument = null;
        try {
            String publisherHdr = request.getPublisher().trim();
            HashMap<String, String> hdrMap = new HashMap<String, String>();
            hdrMap.put(PUBLISHER_HEADER, publisherHdr);
            responseDocument = HelperUtil.getAPIResponse(urlString.toString(), hdrMap);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getOffers", ihe);
        }

        List<Offer> offersList = parseXML(request, responseDocument);
        OffersResponse response = new OffersResponse();
        response.setOffers(offersList);

        if (offersList != null && !offersList.isEmpty()) {
            for (Offer offer : offersList) {
                ProfileRequest profileRequest = new ProfileRequest(request);
                profileRequest.setClientIP(request.getClientIP());
                profileRequest.setListingId(offer.getListingId());

                ProfileHelper profHelper = new ProfileHelper(this.rootPath);
                Profile profile = profHelper.getProfile(profileRequest);
                if (profile != null) {
                    offer.setReviewCount(HelperUtil.toInteger(profile.getReviewCount()));
                    offer.setProfileUrl(profile.getProfileUrl());
                    offer.setPhone(profile.getPhone());

                    offer.setCallBackFunction(request.getCallBackFunction());
                    offer.setCallBackUrl(request.getCallBackUrl());

                    String profileTrackingUrl = HelperUtil.getTrackingUrl(profile.getProfileUrl(),
                            request.getCallBackUrl(), request.getDartClickTrackUrl(),
                            offer.getListingId(), profile.getPhone(), request.getPublisher(),
                            request.getAdUnitName(), request.getAdUnitSize());
                    offer.setProfileTrackingUrl(profileTrackingUrl);

                    String callBackFn = HelperUtil.getCallBackFunctionString(
                            request.getCallBackFunction(), offer.getListingId(), profile.getPhone());
                    offer.setCallBackFunction(callBackFn);

                    StringBuilder couponUrl = new StringBuilder(profile.getProfileUrl());
                    couponUrl.append(OFFER_ANCHOR);
                    String couponTrackingUrl = HelperUtil.getTrackingUrl(couponUrl.toString(),
                            null, request.getDartClickTrackUrl(), offer.getListingId(),
                            profile.getPhone(), request.getPublisher(), request.getAdUnitName(),
                            request.getAdUnitSize());
                    offer.setCouponUrl(couponTrackingUrl);

                    // set distance for conquest AD
                    if (request.getAdUnitName().equals(CONQUEST_AD)) {
                        offer.setDistance(new Double(HelperUtil.getDistance(new BigDecimal(
                                request.getLatitude()), new BigDecimal(request.getLongitude()),
                                new BigDecimal(offer.getLatitude()), new BigDecimal(
                                        offer.getLongitude()))).toString());
                    }
                }
            }
        }

        if (offersList != null && offersList.size() < request.getDisplaySize()) {
            NearbyPlacesRequest nbpRequest = new NearbyPlacesRequest(request);
            nbpRequest.setWhat(request.getWhat());
            nbpRequest.setWhere(request.getWhere());
            nbpRequest.setLatitude(request.getLatitude());
            nbpRequest.setLongitude(request.getLongitude());
            nbpRequest.setRadius(request.getRadius());
            nbpRequest.setTags(request.getTag());

            NearbyPlacesHelper nbpHelper = new NearbyPlacesHelper(this.rootPath);
            NearbyPlacesResponse nbpResponse = nbpHelper.getNearbyPlaces(nbpRequest);
            List<NearbyPlace> backfill = new ArrayList<NearbyPlace>();
            response.setBackfill(backfill);

            int moreRequired = request.getDisplaySize() - offersList.size();
            if (nbpResponse.getNearbyPlaces() != null && !nbpResponse.getNearbyPlaces().isEmpty()) {
                List<NearbyPlace> nbpPlaces = null;
                if (nbpResponse.getNearbyPlaces().size() >= moreRequired) {
                    nbpPlaces = nbpResponse.getNearbyPlaces().subList(0, moreRequired);
                }
                response.getBackfill().addAll(nbpPlaces);
            }

            if (offersList.size() < request.getDisplaySize() && nbpResponse.getBackfill() != null
                    && !nbpResponse.getBackfill().isEmpty()) {
                moreRequired = request.getDisplaySize() - offersList.size();
                List<NearbyPlace> nbpPlaces = null;
                if (nbpResponse.getBackfill().size() >= moreRequired) {
                    nbpPlaces = nbpResponse.getBackfill().subList(0, moreRequired);
                }
                response.getBackfill().addAll(nbpPlaces);
            }

            if (offersList.size() < request.getDisplaySize() && nbpResponse.getHouseAds() != null
                    && !nbpResponse.getHouseAds().isEmpty()) {
                moreRequired = request.getDisplaySize() - offersList.size();
                List<HouseAd> houseAds = null;
                if (nbpResponse.getHouseAds().size() >= moreRequired) {
                    houseAds = nbpResponse.getHouseAds().subList(0, moreRequired);
                }
                response.setHouseAds(houseAds);
            }
        }

        log.info("End offersHelper getOffers()");
        return response;
    }

    /**
     * Parses the offers xml. Returns List of offer objects
     * 
     * @param doc
     * @return List of Offer objects
     * @throws CitysearchException
     */
    private List<Offer> parseXML(OffersRequest request, Document doc) throws CitysearchException {
        log.info("Start OffersHelper parseXML");
        List<Offer> offersList = null;
        if (doc != null && doc.hasRootElement()) {
            Element rootElement = doc.getRootElement();
            List<Element> offersElementList = rootElement.getChildren(OFFER);
            offersList = (List<Offer>) getOffersList(request, offersElementList);
        }
        log.info("End OffersHelper parseXML");
        return offersList;
    }

    /**
     * Parses the offers element list and create list of Offer objects
     * 
     * @param List
     *            of Offer Elements
     * @return List of Offer beans
     * @throws CitysearchException
     */
    private List<Offer> getOffersList(OffersRequest request, List<Element> offerElemList)
            throws CitysearchException {
        log.info("Start OffersHelper getOffersList");
        List<Offer> offersLst = new ArrayList<Offer>();
        if (!offerElemList.isEmpty()) {
            Iterator<Element> it = offerElemList.iterator();
            while (offersLst.size() <= request.getDisplaySize() && it.hasNext()) {
                Element offerElement = (Element) it.next();
                Offer offer = new Offer();
                offer.setCity(offerElement.getChildText(CITY));
                offer.setState(offerElement.getChildText(STATE));
                String location = HelperUtil.getLocationString(offer.getCity(), offer.getState());
                offer.setLocation(location);
                offer.setAttributionSrc(offerElement.getChildText(ATTRIBUTION_SOURCE));
                String ratingVal = offerElement.getChildText(CS_RATING);
                List<Integer> ratingList = HelperUtil.getRatingsList(ratingVal);
                offer.setListingRating(ratingList);
                offer.setReviewCount(HelperUtil.toInteger(offerElement.getChildText(REVIEW_COUNT)));
                offer.setImageUrl(offerElement.getChildText(IMAGE_URL));
                offer.setLatitude(offerElement.getChildText(LATITUDE));
                offer.setListingId(offerElement.getChildText(LISTING_ID));
                offer.setListingName(offerElement.getChildText(LISTING_NAME));
                offer.setLongitude(offerElement.getChildText(LONGITUDE));
                offer.setOfferDescription(offerElement.getChildText(OFFER_DESCRIPTION));
                offer.setOfferId(offerElement.getChildText(OFFER_ID));
                offer.setOfferTitle(offerElement.getChildText(OFFER_TITLE));
                offer.setReferenceId(offerElement.getChildText(REFERENCE_ID));
                offer.setStreet(offerElement.getChildText(STREET));
                offer.setZip(offerElement.getChildText(ZIP));
                offersLst.add(offer);
            }
        }
        log.info("End OffersHelper getOffersList");
        return offersLst;
    }

    private void loadLatitudeAndLongitudeFromSearchAPI(OffersRequest request)
            throws CitysearchException {
        SearchRequest sRequest = new SearchRequest();
        sRequest.setWhat(request.getWhat());
        sRequest.setWhere(request.getWhere());
        // sRequest.setTags(request.getTags());
        sRequest.setPublisher(request.getPublisher());

        SearchHelper sHelper = new SearchHelper(this.rootPath, this.displaySize);
        String[] latLon = sHelper.getLatitudeLongitude(sRequest);
        if (latLon.length >= 2) {
            request.setLatitude(latLon[0]);
            request.setLongitude(latLon[1]);
        }
    }
}
