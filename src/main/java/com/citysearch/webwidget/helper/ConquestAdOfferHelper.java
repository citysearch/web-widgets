package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.ConquestAdOffer;
import com.citysearch.webwidget.bean.ConquestAdOfferRequest;
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
@Deprecated
public class ConquestAdOfferHelper {

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
    private Integer displaySize = 1;

    public ConquestAdOfferHelper(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Constructs the Offers API query string with all the supplied parameters
     * 
     * @return String
     * @throws CitysearchException
     */
    private String getQueryString(ConquestAdOfferRequest request) throws CitysearchException {
        log.info("=========Start offersHelper getQueryString()============================ >");
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher().trim()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);

        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RPP, RPP_OFFERS));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);

        if (!StringUtils.isBlank(request.getWhat())) {
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                    request.getWhat().trim()));
        }
        if (!StringUtils.isBlank(request.getTag())) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                    request.getTag().trim()));
        }
        if (!StringUtils.isBlank(request.getWhere())
                && (StringUtils.isBlank(request.getLatitude()) || StringUtils.isBlank(request.getLongitude()))) {
            strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
            strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHERE,
                    request.getWhere().trim()));
        }
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
        log.info("=========Start offersHelper getQueryString() querystring is ============================ >"
                + strBuilder);
        return strBuilder.toString();
    }

    /**
     * Validates the request. If any of the parameters are missing, throws Citysearch Exception
     * 
     * @throws CitysearchException
     */
    public void validateRequest(ConquestAdOfferRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        log.info("=========Start offersHelper validateRequest()============================ >");
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
        if (StringUtils.isBlank(request.getLatitude())
                && StringUtils.isBlank(request.getLongitude())
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
        log.info("=========End offersHelper validateRequest()============================ >"
                + errors);
    }

    /**
     * Get the offers from Offers API
     * 
     * @param request
     * @return List of Offers
     * @throws InvalidRequestParametersException
     * @throws CitysearchException
     */
    public List<ConquestAdOffer> getOffers(ConquestAdOfferRequest request)
            throws InvalidRequestParametersException, CitysearchException {
        log.info("=========Start offersHelper getOffers()============================ >");
        validateRequest(request);

        // TODO: why are you doing this???
        if (StringUtils.isBlank(request.getLatitude())
                || StringUtils.isBlank(request.getLongitude())) {
            log.info("NearbyPlacesHelper.getNearbyPlaces: No lat lon. Find Lat and Lon");
            // latitudeLongitudePresentInRequest = false;
            loadLatitudeAndLongitudeFromSearchAPI(request);
            if (StringUtils.isBlank(request.getRadius())) {
                request.setRadius(String.valueOf(CommonConstants.EXTENDED_RADIUS));
            }
        }

        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(PROPERTY_OFFERS_URL) + getQueryString(request);
        Document responseDocument = null;
        try {
            String publisherHdr = request.getPublisher().trim();
            HashMap<String, String> hdrMap = new HashMap<String, String>();
            hdrMap.put(PUBLISHER_HEADER, publisherHdr);
            responseDocument = HelperUtil.getAPIResponse(urlString, hdrMap);
        } catch (InvalidHttpResponseException ihe) {
            throw new CitysearchException(this.getClass().getName(), "getOffers", ihe);
        }
        List<ConquestAdOffer> offersList = parseXML(responseDocument);
        // TODO: BUG!!!!
        if (offersList == null) {
            log.info("OffersHelper.getOffers:: Null offers instance ");
            throw new CitysearchException(this.getClass().getName(), "getOffers", "No offer found.");
        }
        // call for Profile API to get review count, profile url and phone#
        Iterator<ConquestAdOffer> it = offersList.iterator();
        while (it.hasNext()) {
            ConquestAdOffer offer = (ConquestAdOffer) it.next();
            ProfileRequest profileRequest = new ProfileRequest();
            profileRequest.setPublisher(request.getPublisher());
            profileRequest.setClientIP(request.getClientIP());
            profileRequest.setListingId(offer.getListingId());
            ProfileHelper profHelper = new ProfileHelper(this.rootPath);
            Profile profile = profHelper.getProfile(profileRequest);
            if (profile != null) {
                offer.setReviewCount(HelperUtil.toInteger(profile.getReviewCount()));
                offer.setProfileUrl(profile.getProfileUrl());
                offer.setProfilePhone(profile.getPhone());
                offer.setDistance(new Double(HelperUtil.getDistance(new BigDecimal(
                        request.getLatitude()), new BigDecimal(request.getLongitude()),
                        new BigDecimal(offer.getLatitude()), new BigDecimal(offer.getLongitude()))).toString());
            }
        }
        log.info("=========End offersHelper getOffers()============================ >");
        return offersList;
    }

    private void loadLatitudeAndLongitudeFromSearchAPI(ConquestAdOfferRequest request)
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

    /**
     * Parses the offers xml. Returns List of offer objects
     * 
     * @param doc
     * @return List of Offer objects
     * @throws CitysearchException
     */
    private List<ConquestAdOffer> parseXML(Document doc) throws CitysearchException {
        log.info("========================== Start OffersHelper parseXML=======================");
        List<Element> offersElementList = null;
        List<ConquestAdOffer> offersList = null;
        if (doc != null && doc.hasRootElement()) {
            Element rootElement = doc.getRootElement();
            offersElementList = (List<Element>) rootElement.getChildren(OFFER);
            offersList = (List<ConquestAdOffer>) getOffersList(offersElementList);
        }
        log.info("========================== End OffersHelper parseXML=======================");
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
    private List<ConquestAdOffer> getOffersList(List<Element> offerElemList)
            throws CitysearchException {
        log.info("========================== Start OffersHelper getOffersList=======================");
        ConquestAdOffer offer = null;
        List<ConquestAdOffer> offersLst = new ArrayList<ConquestAdOffer>();
        if (!offerElemList.isEmpty()) {
            int cnt = 0;
            Iterator<Element> it = offerElemList.iterator();
            while (it.hasNext() && cnt < 1) {
                Element offerElement = (Element) it.next();

                offer = new ConquestAdOffer();
                offer.setCity(offerElement.getChildText(CITY));
                offer.setAttributionSrc(offerElement.getChildText(ATTRIBUTION_SOURCE));

                String ratingVal = offerElement.getChildText(CS_RATING);
                double rating = NumberUtils.toDouble(ratingVal) / 2;
                List<Integer> ratingList = HelperUtil.getRatingsList(ratingVal);
                offer.setListingRating(ratingList);
                offer.setReviewCount(HelperUtil.toInteger(offerElement.getChildText(REVIEW_COUNT)));
                offer.setImgUrl(offerElement.getChildText(IMAGE_URL));
                offer.setLatitude(offerElement.getChildText(LATITUDE));
                offer.setListingId(offerElement.getChildText(LISTING_ID));
                offer.setListingName(offerElement.getChildText(LISTING_NAME));
                offer.setLongitude(offerElement.getChildText(LONGITUDE));
                offer.setOfferDescription(offerElement.getChildText(OFFER_DESCRIPTION));
                offer.setOfferId(offerElement.getChildText(OFFER_ID));
                offer.setOfferTitle(offerElement.getChildText(OFFER_TITLE));
                offer.setRefId(offerElement.getChildText(REFERENCE_ID));
                offer.setState(offerElement.getChildText(STATE));
                offer.setStreet(offerElement.getChildText(STREET));
                offer.setZip(offerElement.getChildText(ZIP));
                offersLst.add(offer);
                cnt = cnt + 1;
            }
        }
        log.info("========================== End OffersHelper getOffersList=======================");
        return offersLst;
    }
}
