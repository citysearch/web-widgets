package com.citysearch.webwidget.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
 * Helper class for PFP API. Contains the functionality to validate request
 * parameters, queries the API for different kind of requests and processes
 * response accordingly
 * 
 * SAMPLE PFP Url's:
 * 
 * http://pfp.citysearch.com/pfp?api_key=gunyay6vkqnvc2geyfedbdt3&what=sushi&
 * where=Pasadena%2C+CA&publishercode=insiderpages
 * http://pfp.citysearch.com/pfp/
 * location?api_key=gunyay6vkqnvc2geyfedbdt3&what=sushi
 * &lat=34.15982&lon=-118.139102&radius=25&publishercode=acme
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

	private String rootPath;
	private Integer displaySize;

	// Field to cache the PFP response document.
	private Document pfpLocationResponse = null;

	private Document pfpResponse = null;

	public NearbyPlacesHelper(String rootPath) throws CitysearchException {
		this.rootPath = rootPath;
	}

	private String getPFPLocationQuery(NearbyPlacesRequest request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder(request
				.getQueryString());
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		return apiQueryString.toString();
	}

	public String getPFPQuery(NearbyPlacesRequest request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();
		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, request.getWhat()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHERE, request.getWhere()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		return apiQueryString.toString();
	}

	/**
	 * Queries Search API for latitude and longitude if not present in request,
	 * then queries PFP api with Geography parameters. If no results are
	 * returned then queries PFP API again but without geography parameters.
	 * 
	 * @param request
	 * @throws CitysearchException
	 */
	public NearbyPlacesResponse getNearbyPlaces(NearbyPlacesRequest request)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("NearbyPlacesHelper.getNearbyPlaces: Begin");
		request.validate();
		this.displaySize = request.getDisplaySize();
		if (this.displaySize == null) {
			this.displaySize = CommonConstants.DEFAULT_NEARBY_DISPLAY_SIZE;
		}
		log.info("NearbyPlacesHelper.getNearbyPlaces: After validate");
		if (request.getPublisher().equalsIgnoreCase(
				CommonConstants.PUBLISHER_INSIDERPAGES)) {
			return findInsiderPagesNearbyPlaces(request);
		} else if (request.getPublisher().equalsIgnoreCase(
				CommonConstants.PUBLISHER_PROJECT_YELLOW)
				|| request.getPublisher().equalsIgnoreCase(
						CommonConstants.PUBLISHER_CITYSEARCH)) {
			return findConquestNearbyPlaces(request);
		}
		return null;
	}

	private NearbyPlacesResponse findInsiderPagesNearbyPlaces(
			NearbyPlacesRequest request) throws CitysearchException {
		NearbyPlacesResponse response = new NearbyPlacesResponse();

		List<NearbyPlace> nearbyPlaces = null;

		if (!StringUtils.isBlank(request.getLatitude())
				&& !StringUtils.isBlank(request.getLongitude())) {
			nearbyPlaces = getAdsFromPFPLocation(request);
		}
		List<NearbyPlace> backfill = null;
		List<HouseAd> houseAds = null;
		List<NearbyPlace> searchResults = null;
		int noOfAdsNeeded = (nearbyPlaces == null || nearbyPlaces.isEmpty()) ? this.displaySize
				: this.displaySize - nearbyPlaces.size();
		if (noOfAdsNeeded > 0) {
			nearbyPlaces = (nearbyPlaces == null) ? new ArrayList<NearbyPlace>()
					: nearbyPlaces;
			if (!StringUtils.isBlank(request.getWhere())) {
				Set<String> listingIds = new TreeSet<String>();
				for (NearbyPlace nbp : nearbyPlaces)
					listingIds.add(nbp.getListingId());
				List<NearbyPlace> pfpResults = getAdsFromPFP(request,
						listingIds);
				if (pfpResults != null && !pfpResults.isEmpty()) {
					pfpResults = (pfpResults.size() > noOfAdsNeeded) ? pfpResults
							.subList(0, noOfAdsNeeded)
							: pfpResults;
					nearbyPlaces.addAll(pfpResults);
				}
			}
			int noOfBackFillNeeded = (nearbyPlaces == null || nearbyPlaces
					.isEmpty()) ? this.displaySize : this.displaySize
					- nearbyPlaces.size();
			if (noOfBackFillNeeded == this.displaySize) {
				backfill = getNearbyPlacesBackfill(request);
				int noOfSearchResultsNeeded = (backfill == null || backfill
						.isEmpty()) ? noOfBackFillNeeded : noOfBackFillNeeded
						- backfill.size();
				if (noOfSearchResultsNeeded > 0) {
					searchResults = getSearchResults(request,
							noOfSearchResultsNeeded);
					int noOfHouseAdsNeeded = (searchResults == null || searchResults
							.isEmpty()) ? noOfSearchResultsNeeded
							: noOfSearchResultsNeeded - searchResults.size();
					if (noOfHouseAdsNeeded > 0) {
						houseAds = HouseAdsHelper.getHouseAds(this.rootPath,
								request.getDartClickTrackUrl());
						houseAds = houseAds.subList(0, noOfHouseAdsNeeded);
					} else if (noOfHouseAdsNeeded < 0) {
						searchResults = searchResults.subList(0,
								noOfSearchResultsNeeded);
					}
				} else if (noOfSearchResultsNeeded < 0) {
					backfill = backfill.subList(0, noOfBackFillNeeded);
				}
			} else if (nearbyPlaces != null && !nearbyPlaces.isEmpty()
					&& nearbyPlaces.size() < this.displaySize) {
				ProfileRequest profileRequest = new ProfileRequest(request);
				profileRequest.setClientIP(request.getClientIP());
				ProfileHelper phelper = new ProfileHelper(this.rootPath);
				for (NearbyPlace nbp : nearbyPlaces) {
					profileRequest.setListingId(nbp.getListingId());
					Profile profile = phelper
							.getProfileAndHighestReview(profileRequest);
					nbp.setProfile(profile);
				}
			}
		}
		response.setNearbyPlaces(nearbyPlaces);
		response.setBackfill(backfill);
		response.setSearchResults(searchResults);
		response.setHouseAds(houseAds);
		return response;
	}

	private NearbyPlacesResponse findConquestNearbyPlaces(
			NearbyPlacesRequest request) throws CitysearchException {
		NearbyPlacesResponse response = new NearbyPlacesResponse();

		List<NearbyPlace> nearbyPlaces = null;

		if (!StringUtils.isBlank(request.getLatitude())
				&& !StringUtils.isBlank(request.getLongitude())) {
			nearbyPlaces = getAdsFromPFPLocation(request);
		}
		List<NearbyPlace> backfill = null;
		List<HouseAd> houseAds = null;
		List<NearbyPlace> searchResults = null;
		int noOfAdsNeeded = (nearbyPlaces == null || nearbyPlaces.isEmpty()) ? this.displaySize
				: this.displaySize - nearbyPlaces.size();
		if (noOfAdsNeeded > 0) {
			nearbyPlaces = (nearbyPlaces == null) ? new ArrayList<NearbyPlace>()
					: nearbyPlaces;
			if (!StringUtils.isBlank(request.getWhere())) {
				Set<String> listingIds = new TreeSet<String>();
				for (NearbyPlace nbp : nearbyPlaces)
					listingIds.add(nbp.getListingId());
				List<NearbyPlace> pfpResults = getAdsFromPFP(request,
						listingIds);
				if (pfpResults != null && !pfpResults.isEmpty()) {
					pfpResults = (pfpResults.size() > noOfAdsNeeded) ? pfpResults
							.subList(0, noOfAdsNeeded)
							: pfpResults;
					nearbyPlaces.addAll(pfpResults);
				}
			}
			int noOfBackFillNeeded = (nearbyPlaces == null || nearbyPlaces
					.isEmpty()) ? this.displaySize : this.displaySize
					- nearbyPlaces.size();
			if (noOfBackFillNeeded > 0) {
				backfill = getNearbyPlacesBackfill(request);
				int noOfHouseAdsNeeded = (backfill == null || backfill
						.isEmpty()) ? noOfBackFillNeeded : noOfBackFillNeeded
						- backfill.size();
				if (noOfHouseAdsNeeded > 0) {
					houseAds = HouseAdsHelper.getHouseAds(this.rootPath,
							request.getDartClickTrackUrl());
					houseAds = houseAds.subList(0, noOfHouseAdsNeeded);
				} else if (noOfHouseAdsNeeded < 0) {
					backfill = backfill.subList(0, noOfBackFillNeeded);
				}
			}
		}
		response.setNearbyPlaces(nearbyPlaces);
		response.setBackfill(backfill);
		response.setSearchResults(searchResults);
		response.setHouseAds(houseAds);
		return response;
	}

	private List<NearbyPlace> getSearchResults(NearbyPlacesRequest request,
			int maxNoOfResultsRequired) throws CitysearchException {
		SearchRequest sRequest = new SearchRequest(request);
		SearchHelper sHelper = new SearchHelper(this.rootPath,
				maxNoOfResultsRequired);
		return sHelper.getNearbyPlaces(sRequest);
	}

	private List<NearbyPlace> getNearbyPlacesBackfill(
			NearbyPlacesRequest request) throws CitysearchException {
		List<NearbyPlace> backFillFromPFP = getNearbyPlacesBackfill(request,
				pfpResponse);
		return backFillFromPFP;
	}

	private List<NearbyPlace> getAdsFromPFPLocation(NearbyPlacesRequest request)
			throws CitysearchException {
		log.info("NearbyPlacesHelper.getAdsFromPFPLocation: Begin");
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PFP_LOCATION_URL));
		urlStringBuilder.append(getPFPLocationQuery(request));
		log.info("NearbyPlacesHelper.getAdsFromPFPLocation: Query: "
				+ urlStringBuilder.toString());
		try {
			pfpLocationResponse = HelperUtil.getAPIResponse(urlStringBuilder
					.toString(), null);
			log
					.info("NearbyPlacesHelper.getAdsFromPFPLocation: successful response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getAdsFromPFPLocation", ihe);
		}
		return getClosestPlaces(request, pfpLocationResponse);
	}

	private List<NearbyPlace> getAdsFromPFP(NearbyPlacesRequest request,
			Set<String> listingsToIgnore) throws CitysearchException {
		log.info("getAdsFromPFP: Begin");
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PFP_URL));
		urlStringBuilder.append(getPFPQuery(request));
		log.info("getAdsFromPFP: Query: " + urlStringBuilder.toString());
		try {
			pfpResponse = HelperUtil.getAPIResponse(
					urlStringBuilder.toString(), null);
			log.info("getAdsFromPFP: successful response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getAdsFromPFP", ihe);
		}
		return getTopReviewedPlaces(request, pfpResponse, listingsToIgnore);
	}

	private List<NearbyPlace> getTopReviewedPlaces(NearbyPlacesRequest request,
			Document doc, Set<String> listingsToIgnore)
			throws CitysearchException {
		log.info("NearbyPlacesHelper.getTopReviewedPlaces: Begin");
		List<NearbyPlace> nearbyPlaces = null;
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
				nearbyPlaces = getTopResults(request, elmsSortedByRating);
			}
		}
		log.info("NearbyPlacesHelper.getTopReviewedPlaces: End");
		return nearbyPlaces;
	}

	private List<NearbyPlace> getClosestPlaces(NearbyPlacesRequest request,
			Document doc) throws CitysearchException {
		log.info("NearbyPlacesHelper.getClosestPlaces: Begin");
		List<NearbyPlace> nearbyPlaces = null;
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
				nearbyPlaces = getTopResults(request, elmsSortedByDistance);
			}
		}
		log.info("NearbyPlacesHelper.getClosestPlaces: End");
		return nearbyPlaces;
	}

	private List<NearbyPlace> getTopResults(NearbyPlacesRequest request,
			SortedMap<Double, List<Element>> sortedElms)
			throws CitysearchException {
		List<NearbyPlace> nearbyPlaces = null;
		if (!sortedElms.isEmpty()) {
			List<Element> elmsToConvert = new ArrayList<Element>();
			for (int j = 0; j < sortedElms.size(); j++) {

				if (elmsToConvert.size() >= this.displaySize) {
					break;
				}
				Double key = sortedElms.firstKey();
				List<Element> elms = sortedElms.remove(key);
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
		return nearbyPlaces;
	}

	private NearbyPlace toNearbyPlace(NearbyPlacesRequest request, Element ad)
			throws CitysearchException {
		NearbyPlace nearbyPlace = new NearbyPlace();

		String adUnitIdentifier = request.getAdUnitIdentifier();

		StringBuilder nameLengthProp = new StringBuilder(adUnitIdentifier);
		nameLengthProp.append(".");
		nameLengthProp.append(CommonConstants.NAME_LENGTH);

		String name = ad.getChildText(CommonConstants.NAME);
		name = HelperUtil.getAbbreviatedString(name, nameLengthProp.toString());
		nearbyPlace.setName(name);

		String location = HelperUtil.getLocationString(ad
				.getChildText(CommonConstants.CITY), ad
				.getChildText(CommonConstants.STATE));
		nearbyPlace.setLocation(location);

		String rating = ad.getChildText(REVIEW_RATING_TAG);
		List<Integer> ratingList = HelperUtil.getRatingsList(rating);
		double ratings = HelperUtil.getRatingValue(rating);
		nearbyPlace.setRating(ratingList);
		nearbyPlace.setRatings(ratings);

		String reviewCount = ad.getChildText(REVIEWS_TAG);
		int userReviewCount = HelperUtil.toInteger(reviewCount);
		nearbyPlace.setReviewCount(userReviewCount);

		String distanceStr = ad.getChildText(CommonConstants.DISTANCE);
		if (!StringUtils.isBlank(distanceStr)
				&& NumberUtils.isNumber(distanceStr)) {
			nearbyPlace.setDistance(Math.round(Double.valueOf(distanceStr)));
		} else {
			nearbyPlace.setDistance(-1);
		}

		StringBuilder tagLengthProp = new StringBuilder(adUnitIdentifier);
		tagLengthProp.append(".");
		tagLengthProp.append(CommonConstants.TAGLINE_LENGTH);
		String category = ad.getChildText(TAGLINE_TAG);
		category = HelperUtil.getAbbreviatedString(category, tagLengthProp
				.toString());
		nearbyPlace.setCategory(category);

		nearbyPlace.setListingId(ad.getChildText(LISTING_ID_TAG));
		nearbyPlace.setAdDisplayURL(ad.getChildText(AD_DISPLAY_URL_TAG));
		nearbyPlace.setAdImageURL(ad.getChildText(AD_IMAGE_URL_TAG));
		nearbyPlace.setPhone(ad.getChildText(PHONE_TAG));
		nearbyPlace.setOffers(ad.getChildText(CommonConstants.OFFERS));

		StringBuilder descLengthProp = new StringBuilder(adUnitIdentifier);
		descLengthProp.append(".");
		descLengthProp.append(CommonConstants.DESCRIPTION_LENGTH);
		String description = ad.getChildText(DESC_TAG);
		description = HelperUtil.getAbbreviatedString(description,
				descLengthProp.toString());
		nearbyPlace.setDescription(description);

		nearbyPlace.setStreet(ad.getChildText(CommonConstants.STREET));
		nearbyPlace.setCity(ad.getChildText(CommonConstants.CITY));
		nearbyPlace.setState(ad.getChildText(CommonConstants.STATE));
		nearbyPlace.setPostalCode(ad.getChildText(ZIP_TAG));
		nearbyPlace.setAdDestinationUrl(ad.getChildText(AD_DESTINATION_URL));

		nearbyPlace.setCallBackFunction(request.getCallBackFunction());
		nearbyPlace.setCallBackUrl(request.getCallBackUrl());

		String adDisplayTrackingUrl = HelperUtil.getTrackingUrl(nearbyPlace
				.getAdDisplayURL(), nearbyPlace.getAdDestinationUrl(), request
				.getCallBackUrl(), request.getDartClickTrackUrl(), nearbyPlace
				.getListingId(), nearbyPlace.getPhone(),
				request.getPublisher(), request.getAdUnitName(), request
						.getAdUnitSize());
		nearbyPlace.setAdDisplayTrackingURL(adDisplayTrackingUrl);

		String callBackFn = HelperUtil.getCallBackFunctionString(request
				.getCallBackFunction(), nearbyPlace.getListingId(), nearbyPlace
				.getPhone());
		nearbyPlace.setCallBackFunction(callBackFn);

		StringBuilder displayUrlLengthProp = new StringBuilder(adUnitIdentifier);
		displayUrlLengthProp.append(".");
		displayUrlLengthProp.append(CommonConstants.DISPLAY_URL_LENGTH);
		String adDisplayUrl = nearbyPlace.getAdDisplayURL();
		adDisplayUrl = HelperUtil.getTruncatedString(adDisplayUrl,
				displayUrlLengthProp.toString());
		nearbyPlace.setAdDisplayURL(adDisplayUrl);

		return nearbyPlace;
	}

	private List<NearbyPlace> getNearbyPlacesBackfill(
			NearbyPlacesRequest request, Document doc)
			throws CitysearchException {
		log.info("NearbyPlacesHelper.getNearbyPlacesBackfill: Begin");
		List<NearbyPlace> nearbyPlaces = null;
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
		String adUnitIdentifier = request.getAdUnitIdentifier();

		String category = ad.getChildText(TAGLINE_TAG);
		if (StringUtils.isNotBlank(category)) {
			category = category.replaceAll("<b>", "");
			category = category.replaceAll("</b>", "");

			StringBuilder tagLengthProp = new StringBuilder(adUnitIdentifier);
			tagLengthProp.append(".backfill.");
			tagLengthProp.append(CommonConstants.TAGLINE_LENGTH);
			category = HelperUtil.getAbbreviatedString(category, tagLengthProp
					.toString());

			nbp.setCategory(category);
		}
		nbp.setAdImageURL(ad.getChildText(AD_IMAGE_URL_TAG));

		String description = ad.getChildText(DESC_TAG);
		if (StringUtils.isNotBlank(description)) {
			description = description.replaceAll("<b>", "");
			description = description.replaceAll("</b>", "");

			StringBuilder descLengthProp = new StringBuilder(adUnitIdentifier);
			descLengthProp.append(".");
			descLengthProp.append(CommonConstants.DESCRIPTION_LENGTH);
			description = HelperUtil.getAbbreviatedString(description,
					descLengthProp.toString());
			nbp.setDescription(description);
		}

		nbp.setOffers(ad.getChildText(CommonConstants.OFFERS));
		nbp.setAdDisplayURL(ad.getChildText(AD_DISPLAY_URL_TAG));
		nbp.setAdDestinationUrl(ad.getChildText(AD_DESTINATION_URL));
		nbp.setListingId(ad.getChildText(LISTING_ID_TAG));
		nbp.setPhone(ad.getChildText(PHONE_TAG));

		String adDisplayTrackingUrl = HelperUtil.getTrackingUrl(nbp
				.getAdDisplayURL(), nbp.getAdDestinationUrl(), null, request
				.getDartClickTrackUrl(), nbp.getListingId(), nbp.getPhone(),
				request.getPublisher(), request.getAdUnitName(), request
						.getAdUnitSize());

		nbp.setAdDisplayTrackingURL(adDisplayTrackingUrl);

		StringBuilder displayUrlLengthProp = new StringBuilder(adUnitIdentifier);
		displayUrlLengthProp.append(".");
		displayUrlLengthProp.append(CommonConstants.DISPLAY_URL_LENGTH);
		String adDisplayUrl = nbp.getAdDisplayURL();
		adDisplayUrl = HelperUtil.getTruncatedString(adDisplayUrl,
				displayUrlLengthProp.toString());
		nbp.setAdDisplayURL(adDisplayUrl);

		return nbp;
	}

	// TODO: Refactor!!!
	public static List<NearbyPlace> addDefaultImages(
			List<NearbyPlace> nearbyPlaces, String path)
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
