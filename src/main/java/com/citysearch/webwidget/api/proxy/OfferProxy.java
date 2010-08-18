package com.citysearch.webwidget.api.proxy;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.api.bean.OfferAPIBean;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class OfferProxy extends AbstractProxy {
	private final static String PROPERTY_OFFERS_URL = "offers.url";
	private Logger log = Logger.getLogger(getClass());

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

	protected String getQueryString(RequestBean request)
			throws CitysearchException {
		log.info("Start OfferProxy getQueryString()");
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.RPP, RPP_OFFERS));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);

		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.CLIENT_IP, request.getClientIP()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);

		if (!StringUtils.isBlank(request.getWhat())) {
			// Offers API throws internal error when reading sushi+restaurant
			String what = request.getWhat().trim();
			try {
				what = URLEncoder.encode(what, "UTF-8");
			} catch (UnsupportedEncodingException excep) {
				throw new CitysearchException("OffersHelper", "getQueryString",
						excep);
			}
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.WHAT, what));
		}

		if (!StringUtils.isBlank(request.getLatitude())
				&& !StringUtils.isBlank(request.getLongitude())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.LATITUDE, request.getLatitude()
							.trim()));

			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.LONGITUDE, request.getLongitude()
							.trim()));
		} else {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.WHERE, request.getWhere().trim()));
		}
		if (!StringUtils.isBlank(request.getTag())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.TAG, request.getTag().trim()));
		}
		if (!StringUtils.isBlank(request.getExpiresBefore())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.EXPIRES_BEFORE, request
							.getExpiresBefore().trim()));
		}
		if (!StringUtils.isBlank(request.getCustomerHasbudget())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.CUSTOMER_HASBUDGET, String
							.valueOf(request.getCustomerHasbudget().trim())));
		}
		if (!StringUtils.isBlank(request.getRadius())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			String radius = HelperUtil.parseRadius(request.getRadius());
			request.setRadius(radius);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.RADIUS, request.getRadius()));
		}
		if (!StringUtils.isBlank(request.getCallBackFunction())) {
			strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
			strBuilder.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.CALLBACK, request
							.getCallBackFunction().trim()));
		}
		log.info("Start OfferProxy getQueryString() querystring is "
				+ strBuilder);
		return strBuilder.toString();
	}

	private OfferAPIBean toOffer(Element offerElement)
			throws CitysearchException {
		OfferAPIBean offer = new OfferAPIBean();
		offer.setCity(offerElement.getChildText(CITY));
		offer.setState(offerElement.getChildText(STATE));
		offer.setAttributionSrc(offerElement.getChildText(ATTRIBUTION_SOURCE));
		offer.setRating(offerElement.getChildText(CS_RATING));
		offer.setReviewCount(offerElement.getChildText(REVIEW_COUNT));
		offer.setImageUrl(offerElement.getChildText(IMAGE_URL));
		offer.setLatitude(offerElement.getChildText(LATITUDE));
		offer.setListingId(offerElement.getChildText(LISTING_ID));
		offer.setLongitude(offerElement.getChildText(LONGITUDE));
		offer.setOfferId(offerElement.getChildText(OFFER_ID));
		offer.setOfferTitle(offerElement.getChildText(OFFER_TITLE));
		offer.setOfferDescription(offerElement.getChildText(OFFER_DESCRIPTION));
		offer.setListingName(offerElement.getChildText(LISTING_NAME));
		offer.setReferenceId(offerElement.getChildText(REFERENCE_ID));
		offer.setStreet(offerElement.getChildText(STREET));
		offer.setZip(offerElement.getChildText(ZIP));
		return offer;
	}

	private List<OfferAPIBean> parse(RequestBean request, Document doc,
			int requiredNoOfOffers) throws CitysearchException {
		log.info("Start OffersHelper parseXML");
		List<OfferAPIBean> offersList = null;
		if (doc != null && doc.hasRootElement()) {
			Element rootElement = doc.getRootElement();
			List<Element> offersElementList = rootElement.getChildren(OFFER);
			if (StringUtils.isBlank(request.getLatitude())
					|| StringUtils.isBlank(request.getLongitude())) {
				offersList = (List<OfferAPIBean>) getOffersByRating(request,
						offersElementList, requiredNoOfOffers);
			} else {
				offersList = (List<OfferAPIBean>) getOffersByDistance(request,
						offersElementList, requiredNoOfOffers);
			}
		}
		log.info("End OffersHelper parseXML");
		return offersList;
	}

	private List<OfferAPIBean> getOffersByDistance(RequestBean request,
			List<Element> offerElemList, int requiredNoOfOffers)
			throws CitysearchException {
		log.info("Start OffersHelper getOffersByDistance");
		List<OfferAPIBean> offersLst = new ArrayList<OfferAPIBean>();
		if (!offerElemList.isEmpty()) {
			BigDecimal sourceLatitude = new BigDecimal(request.getLatitude());
			BigDecimal sourceLongitude = new BigDecimal(request.getLongitude());
			SortedMap<Double, List<Element>> elmsSortedByDistance = new TreeMap<Double, List<Element>>();
			for (Element elm : offerElemList) {
				BigDecimal businessLatitude = new BigDecimal(elm
						.getChildText(CommonConstants.LATITUDE));
				BigDecimal businessLongitude = new BigDecimal(elm
						.getChildText(CommonConstants.LONGITUDE));
				double distance = HelperUtil.getDistance(sourceLatitude,
						sourceLongitude, businessLatitude, businessLongitude);
				if (elmsSortedByDistance.containsKey(distance)) {
					elmsSortedByDistance.get(distance).add(elm);
				} else {
					List<Element> elms = new ArrayList<Element>();
					elms.add(elm);
					elmsSortedByDistance.put(distance, elms);
				}
			}
			offersLst = getOffersList(request, elmsSortedByDistance,
					requiredNoOfOffers);
		}
		log.info("End OffersHelper getOffersByDistance");
		return offersLst;
	}

	private List<OfferAPIBean> getOffersByRating(RequestBean request,
			List<Element> offerElemList, int requiredNoOfOffers)
			throws CitysearchException {
		log.info("Start OffersHelper getOffersByRating");
		List<OfferAPIBean> offersLst = new ArrayList<OfferAPIBean>();
		if (!offerElemList.isEmpty()) {
			SortedMap<Double, List<Element>> elmsSortedByRating = new TreeMap<Double, List<Element>>();
			for (Element elm : offerElemList) {
				String ratingVal = elm.getChildText(CS_RATING);
				if (!StringUtils.isBlank(ratingVal)
						&& NumberUtils.isNumber(ratingVal)) {
					double rating = NumberUtils.toDouble(ratingVal);
					if (elmsSortedByRating.containsKey(rating)) {
						elmsSortedByRating.get(rating).add(elm);
					} else {
						List<Element> elms = new ArrayList<Element>();
						elms.add(elm);
						elmsSortedByRating.put(rating, elms);
					}
				}
			}
			offersLst = getOffersList(request, elmsSortedByRating,
					requiredNoOfOffers);
		}
		log.info("End OffersHelper getOffersByRating");
		return offersLst;
	}

	private List<OfferAPIBean> getOffersList(RequestBean request,
			SortedMap<Double, List<Element>> sortedElms, int requiredNoOfOffers)
			throws CitysearchException {
		List<OfferAPIBean> offersLst = new ArrayList<OfferAPIBean>();
		if (!sortedElms.isEmpty()) {
			for (int j = 0; j < sortedElms.size(); j++) {
				if (offersLst.size() >= requiredNoOfOffers) {
					break;
				}
				Double key = sortedElms.firstKey();
				List<Element> elms = sortedElms.remove(key);
				for (int idx = 0; idx < elms.size(); idx++) {
					if (offersLst.size() == requiredNoOfOffers) {
						break;
					}
					OfferAPIBean offer = toOffer(elms.get(idx));
					offersLst.add(offer);
				}
			}
		}
		return offersLst;
	}

	public List<OfferAPIBean> getOffers(RequestBean request,
			int requiredNoOfOffers) throws InvalidRequestParametersException,
			CitysearchException {
		log.info("Start OfferProxy getOffers()");
		request.validate();
		request.setCustomerHasbudget("true");

		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlString = new StringBuilder(properties
				.getProperty(PROPERTY_OFFERS_URL));
		urlString.append(getQueryString(request));
		Document responseDocument = null;
		try {
			String publisherHdr = request.getPublisher().trim();
			HashMap<String, String> hdrMap = new HashMap<String, String>();
			hdrMap.put(PUBLISHER_HEADER, publisherHdr);
			responseDocument = getAPIResponse(urlString.toString(), hdrMap);
		} catch (InvalidHttpResponseException ihe) {
			log.error(ihe.getMessage());
			return null;
		}
		log.info("End OfferProxy getOffers()");
		return parse(request, responseDocument, requiredNoOfOffers);
	}
}
