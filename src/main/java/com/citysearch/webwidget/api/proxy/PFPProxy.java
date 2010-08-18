package com.citysearch.webwidget.api.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.api.bean.PFPAd;
import com.citysearch.webwidget.api.bean.PFPResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class PFPProxy extends AbstractProxy {
	private Logger log = Logger.getLogger(getClass());

	private final static String PFP_LOCATION_URL = "pfplocation.url";
	private final static String PFP_URL = "pfp.url";
	private static final String AD_TAG = "ad";
	private static final String TYPE_TAG = "type";
	private static final String REVIEW_RATING_TAG = "overall_review_rating";
	private static final String REVIEWS_TAG = "reviews";
	private static final String LISTING_ID_TAG = "listingId";
	private static final String TAGLINE_TAG = "tagline";
	private static final String AD_DISPLAY_URL_TAG = "ad_display_url";
	private static final String AD_IMAGE_URL_TAG = "ad_image_url";
	private static final String PHONE_TAG = "phone";
	private static final String DESC_TAG = "description";
	private static final String ZIP_TAG = "zip";
	private static final String AD_DESTINATION_URL = "ad_destination_url";
	private static final String AD_TYPE_PFP = "local PFP";
	private static final String AD_TYPE_BACKFILL = "backfill";

	private Document pfpLocationResponse;
	private Document pfpResponse;

	private PFPAd toPFPAd(Element adElm) {
		PFPAd ad = new PFPAd();
		ad.setName(adElm.getChildText(CommonConstants.NAME));
		ad.setRating(adElm.getChildText(REVIEW_RATING_TAG));
		ad.setReviewCount(adElm.getChildText(REVIEWS_TAG));
		ad.setDistance(adElm.getChildText(CommonConstants.DISTANCE));
		ad.setCategory(adElm.getChildText(TAGLINE_TAG));
		ad.setListingId(adElm.getChildText(LISTING_ID_TAG));
		ad.setAdDisplayUrl(adElm.getChildText(AD_DISPLAY_URL_TAG));
		ad.setImageUrl(adElm.getChildText(AD_IMAGE_URL_TAG));
		ad.setPhone(adElm.getChildText(PHONE_TAG));
		ad.setOffers(adElm.getChildText(CommonConstants.OFFERS));
		ad.setDescription(adElm.getChildText(DESC_TAG));
		ad.setStreet(adElm.getChildText(CommonConstants.STREET));
		ad.setCity(adElm.getChildText(CommonConstants.CITY));
		ad.setState(adElm.getChildText(CommonConstants.STATE));
		ad.setPostalCode(adElm.getChildText(ZIP_TAG));
		ad.setAdDestinationUrl(adElm.getChildText(AD_DESTINATION_URL));
		return ad;
	}

	private PFPAd toPFPAdBackfill(Element adElm) {
		PFPAd ad = new PFPAd();
		String category = adElm.getChildText(TAGLINE_TAG);
		if (StringUtils.isNotBlank(category)) {
			category = category.replaceAll("<b>", "");
			category = category.replaceAll("</b>", "");
			ad.setCategory(category);
		}
		ad.setImageUrl(adElm.getChildText(AD_IMAGE_URL_TAG));

		String description = adElm.getChildText(DESC_TAG);
		if (StringUtils.isNotBlank(description)) {
			description = description.replaceAll("<b>", "");
			description = description.replaceAll("</b>", "");
			ad.setDescription(description);
		}

		ad.setOffers(adElm.getChildText(CommonConstants.OFFERS));
		ad.setAdDisplayUrl(adElm.getChildText(AD_DISPLAY_URL_TAG));
		ad.setAdDestinationUrl(adElm.getChildText(AD_DESTINATION_URL));
		ad.setListingId(adElm.getChildText(LISTING_ID_TAG));
		ad.setPhone(adElm.getChildText(PHONE_TAG));
		return ad;
	}

	private List<PFPAd> getNearbyPlacesBackfill(Document doc,
			int requiredNoOfBackfills) throws CitysearchException {
		log.info("PFPProxy.getNearbyPlacesBackfill: Begin");
		List<PFPAd> backfills = null;
		if (doc != null && doc.hasRootElement()) {
			List<Element> backfillElms = new ArrayList<Element>();
			Element rootElement = doc.getRootElement();
			List<Element> children = rootElement.getChildren(AD_TAG);
			if (children != null && !children.isEmpty()) {
				for (Element elm : children) {
					String adType = StringUtils
							.trim(elm.getChildText(TYPE_TAG));
					if (adType != null
							&& adType.equalsIgnoreCase(AD_TYPE_BACKFILL)) {
						backfillElms.add(elm);
					}
				}
				if (!backfillElms.isEmpty()) {
					List<Element> elmsToConvert = new ArrayList<Element>();
					if (backfillElms.size() >= requiredNoOfBackfills) {
						for (int idx = 0; idx < requiredNoOfBackfills; idx++) {
							elmsToConvert.add(backfillElms.get(idx));
						}
					} else {
						elmsToConvert = backfillElms;
					}
					backfills = new ArrayList<PFPAd>();
					for (Element elm : elmsToConvert) {
						backfills.add(toPFPAdBackfill(elm));
					}
				}
			}
		}
		log.info("PFPProxy.getNearbyPlacesBackfill: End");
		return backfills;
	}

	private List<PFPAd> getClosestPlaces(RequestBean request, Document doc,
			int requiredSize) throws CitysearchException {
		log.info("PFPProxy.getClosestPlaces: Begin");
		List<PFPAd> pfpAds = null;
		if (doc != null && doc.hasRootElement()) {
			SortedMap<Double, List<Element>> elmsSortedByDistance = new TreeMap<Double, List<Element>>();
			Element rootElement = doc.getRootElement();
			List<Element> children = rootElement.getChildren(AD_TAG);
			if (children != null && !children.isEmpty()) {
				for (Element elm : children) {
					String adType = StringUtils
							.trim(elm.getChildText(TYPE_TAG));
					if (adType != null && adType.equalsIgnoreCase(AD_TYPE_PFP)) {
						String distanceStr = elm
								.getChildText(CommonConstants.DISTANCE);
						double distance = 0.0;
						if (!StringUtils.isBlank(distanceStr)
								&& StringUtils.isNumeric(distanceStr)) {
							distance = Double.valueOf(distanceStr);
						}
						if (elmsSortedByDistance.containsKey(distance)) {
							elmsSortedByDistance.get(distance).add(elm);
						} else {
							List<Element> elms = new ArrayList<Element>();
							elms.add(elm);
							elmsSortedByDistance.put(distance, elms);
						}
					}
				}
				pfpAds = getTopResults(request, elmsSortedByDistance,
						requiredSize);
			}
		}
		log.info("PFPProxy.getClosestPlaces: End");
		return pfpAds;
	}

	private List<PFPAd> getTopReviewedPlaces(RequestBean request, Document doc,
			Set<String> listingsToIgnore, int requiredSize)
			throws CitysearchException {
		log.info("PFPProxy.getTopReviewedPlaces: Begin");
		List<PFPAd> pfpAds = null;
		if (doc != null && doc.hasRootElement()) {
			SortedMap<Double, List<Element>> elmsSortedByRating = new TreeMap<Double, List<Element>>();
			Element rootElement = doc.getRootElement();
			List<Element> children = rootElement.getChildren(AD_TAG);
			if (children != null && !children.isEmpty()) {
				for (Element elm : children) {
					String adType = StringUtils
							.trim(elm.getChildText(TYPE_TAG));
					String listingId = StringUtils.trim(elm
							.getChildText(LISTING_ID_TAG));
					if (adType != null && adType.equalsIgnoreCase(AD_TYPE_PFP)
							&& !listingsToIgnore.contains(listingId)) {
						String rating = elm.getChildText(REVIEW_RATING_TAG);
						double ratings = HelperUtil.getRatingValue(rating);
						if (elmsSortedByRating.containsKey(ratings)) {
							elmsSortedByRating.get(ratings).add(elm);
						} else {
							List<Element> elms = new ArrayList<Element>();
							elms.add(elm);
							elmsSortedByRating.put(ratings, elms);
						}
					}
				}
				pfpAds = getTopResults(request, elmsSortedByRating,
						requiredSize);
			}
		}
		log.info("PFPProxy.getTopReviewedPlaces: End");
		return pfpAds;
	}

	private List<PFPAd> getTopResults(RequestBean request,
			SortedMap<Double, List<Element>> sortedElms, int requiredSize)
			throws CitysearchException {
		List<PFPAd> pfpAds = null;
		if (!sortedElms.isEmpty()) {
			List<Element> elmsToConvert = new ArrayList<Element>();
			for (int j = 0; j < sortedElms.size(); j++) {
				if (elmsToConvert.size() >= requiredSize) {
					break;
				}
				Double key = sortedElms.firstKey();
				List<Element> elms = sortedElms.remove(key);
				for (int idx = 0; idx < elms.size(); idx++) {
					if (elmsToConvert.size() == requiredSize) {
						break;
					}
					elmsToConvert.add(elms.get(idx));
				}
			}

			pfpAds = new ArrayList<PFPAd>();
			for (Element elm : elmsToConvert) {
				pfpAds.add(toPFPAd(elm));
			}
		}
		return pfpAds;
	}

	public PFPResponse getAdsFromPFPLocation(RequestBean request,
			int requiredNoOfAds) throws InvalidRequestParametersException,
			CitysearchException {
		request.validate();
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PFP_LOCATION_URL));
		urlStringBuilder.append(getLatLonQueryString(request));
		log.info("PFPProxy.getAdsFromPFPLocation: Query: "
				+ urlStringBuilder.toString());
		try {
			pfpLocationResponse = getAPIResponse(urlStringBuilder.toString(),
					null);
			log.info("PFPProxy.getAdsFromPFPLocation: successful response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getAdsFromPFPLocation", ihe);
		}
		PFPResponse response = new PFPResponse();
		List<PFPAd> ads = getClosestPlaces(request, pfpLocationResponse,
				requiredNoOfAds);
		response.setLocalPfp(ads);
		return response;
	}

	public PFPResponse getAdsFromPFP(RequestBean request, int requiredNoOfAds,
			Set<String> listingIdsToIgnore)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("PFPProxy.getAdsFromPFP: Begin");
		request.validate();
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PFP_URL));
		urlStringBuilder.append(getWhereQueryString(request));
		log.info("PFPProxy.getAdsFromPFP: Query: "
				+ urlStringBuilder.toString());
		try {
			pfpResponse = getAPIResponse(urlStringBuilder.toString(), null);
			log.info("PFPProxy.getAdsFromPFP: successful response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getAdsFromPFP", ihe);
		}
		PFPResponse response = new PFPResponse();
		List<PFPAd> ads = getTopReviewedPlaces(request, pfpResponse,
				listingIdsToIgnore, requiredNoOfAds);
		response.setLocalPfp(ads);
		return response;
	}

	public PFPResponse getBackFill(int requiredNoOfBackfills)
			throws CitysearchException {
		List<PFPAd> backfills = getNearbyPlacesBackfill(pfpResponse,
				requiredNoOfBackfills);
		PFPResponse response = new PFPResponse();
		response.setBackfill(backfills);
		return response;
	}
}
