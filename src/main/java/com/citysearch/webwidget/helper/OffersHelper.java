package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.OffersRequest;
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
    private static final Integer OFFER_TITLE_SIZE = 36;

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

    private Integer displaySize;

    public OffersHelper(String rootPath, Integer displaySize) {
        this.rootPath = rootPath;
        this.displaySize = displaySize;
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

        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LATITUDE,
                    request.getLatitude().trim()));

            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LONGITUDE,
                    request.getLongitude().trim()));
        } else {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHERE,
                    request.getWhere().trim()));
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

        if (StringUtils.isBlank(request.getWhat()) && StringUtils.isBlank(request.getTag())) {
            errors.add(errorProperties.getProperty(CommonConstants.WHAT_ERROR_CODE));
        }

        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())
                && !StringUtils.isBlank(request.getWhere())) {
            errors.add(errorProperties.getProperty(CommonConstants.LOCATION_ERROR));
        }
        if ((StringUtils.isBlank(request.getLatitude()) || StringUtils.isBlank(request.getLongitude()))
                && StringUtils.isBlank(request.getWhere())) {
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

        if (StringUtils.isBlank(request.getClientIP())) {
            errors.add(errorProperties.getProperty(CommonConstants.CLIENT_IP_ERROR_CODE));
        }
        if (!errors.isEmpty()) {
            throw new InvalidRequestParametersException(this.getClass().getName(),
                    "validateRequest", "Invalid parameters.", errors);
        }
        log.info("End offersHelper validateRequest()");
    }

    private void loadLatitudeAndLongitudeFromSearchAPI(OffersRequest request)
            throws CitysearchException {
        SearchRequest sRequest = new SearchRequest(request);
        sRequest.setWhat(request.getWhat());
        sRequest.setTags(request.getTag());
        sRequest.setWhere(request.getWhere());
        sRequest.setPublisher(request.getPublisher());

        SearchHelper sHelper = new SearchHelper(this.rootPath, this.displaySize);
        String[] latLon = sHelper.getLatitudeLongitude(sRequest);
        if (latLon.length >= 2) {
            request.setLatitude(latLon[0]);
            request.setLongitude(latLon[1]);
        }
    }

    /**
     * Get the offers from Offers API
     * 
     * @param request
     * @return List of Offers
     * @throws InvalidRequestParametersException
     * @throws CitysearchException
     */
    public List<Offer> getOffers(OffersRequest request) throws InvalidRequestParametersException,
            CitysearchException {
        log.info("Start offersHelper getOffers()");
        validateRequest(request);

        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            log.info("OffersHelper.getOffers: No lat lon. Find Lat and Lon");
            loadLatitudeAndLongitudeFromSearchAPI(request);
            if (StringUtils.isBlank(request.getRadius())) {
                request.setRadius(String.valueOf(CommonConstants.EXTENDED_RADIUS));
            }
        }
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No lat lon. return house ads.");
            throw new CitysearchException(this.getClass().getName(), "getOffers",
                    "Invalid Latitude and Longitude");
        }

        // Always return offers from customer who have budget
        // TODO: cleanup!!
        request.setCustomerHasbudget("true");// ????

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
            // throw new CitysearchException(this.getClass().getName(), "getOffers", ihe);
            // Return null and let it go to backfill
            log.error(ihe.getMessage());
            return null;
        }

        List<Offer> offersList = parseXML(request, responseDocument);
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
                            null, request.getCallBackUrl(), request.getDartClickTrackUrl(),
                            offer.getListingId(), profile.getPhone(), request.getPublisher(),
                            request.getAdUnitName(), request.getAdUnitSize());
                    offer.setProfileTrackingUrl(profileTrackingUrl);

                    String callBackFn = HelperUtil.getCallBackFunctionString(
                            request.getCallBackFunction(), offer.getListingId(), profile.getPhone());
                    offer.setCallBackFunction(callBackFn);

                    StringBuilder couponUrl = new StringBuilder(profile.getProfileUrl());
                    String couponTrackingUrl = HelperUtil.getTrackingUrl(couponUrl.toString(),
                            null, null, request.getDartClickTrackUrl(), offer.getListingId(),
                            profile.getPhone(), request.getPublisher(), request.getAdUnitName(),
                            request.getAdUnitSize());
                    offer.setCouponUrl(couponTrackingUrl);
                }
            }
        }

        if (offersList != null && !offersList.isEmpty()
                && offersList.size() < request.getDisplaySize()) {
            ProfileRequest profileRequest = new ProfileRequest(request);
            profileRequest.setClientIP(request.getClientIP());
            ProfileHelper phelper = new ProfileHelper(this.rootPath);
            for (Offer offer : offersList) {
                profileRequest.setListingId(offer.getListingId());
                Profile profile = phelper.getProfileAndHighestReview(profileRequest);
                offer.setProfile(profile);
            }
        }

        log.info("End offersHelper getOffers()");
        return offersList;
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
            BigDecimal sourceLatitude = new BigDecimal(request.getLatitude());
            BigDecimal sourceLongitude = new BigDecimal(request.getLongitude());
            SortedMap<Double, List<Element>> elmsSortedByDistance = new TreeMap<Double, List<Element>>();
            for (Element elm : offerElemList) {
                BigDecimal businessLatitude = new BigDecimal(
                        elm.getChildText(CommonConstants.LATITUDE));
                BigDecimal businessLongitude = new BigDecimal(
                        elm.getChildText(CommonConstants.LONGITUDE));
                double distance = HelperUtil.getDistance(sourceLatitude, sourceLongitude,
                        businessLatitude, businessLongitude);
                if (elmsSortedByDistance.containsKey(distance)) {
                    elmsSortedByDistance.get(distance).add(elm);
                } else {
                    List<Element> elms = new ArrayList<Element>();
                    elms.add(elm);
                    elmsSortedByDistance.put(distance, elms);
                }
            }
            if (!elmsSortedByDistance.isEmpty()) {
                for (int j = 0; j < elmsSortedByDistance.size(); j++) {
                    if (offersLst.size() >= this.displaySize) {
                        break;
                    }
                    Double key = elmsSortedByDistance.firstKey();
                    List<Element> elms = elmsSortedByDistance.remove(key);
                    for (int idx = 0; idx < elms.size(); idx++) {
                        if (offersLst.size() == this.displaySize) {
                            break;
                        }
                        Offer offer = toOffer(elms.get(idx));
                        offer.setDistance(String.valueOf(key));
                        offersLst.add(offer);
                    }
                }
            }
        }
        log.info("End OffersHelper getOffersList");
        return offersLst;
    }

    private Offer toOffer(Element offerElement) {
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
        String offerTitle = offer.getOfferTitle();
        if (offerTitle != null && offerTitle.trim().length() > OFFER_TITLE_SIZE) {
            offer.setOfferShortTitle(StringUtils.abbreviate(offerTitle, OFFER_TITLE_SIZE));
        } else {
            offer.setOfferShortTitle(offerTitle);
        }

        offer.setReferenceId(offerElement.getChildText(REFERENCE_ID));
        offer.setStreet(offerElement.getChildText(STREET));
        offer.setZip(offerElement.getChildText(ZIP));
        return offer;
    }
}
