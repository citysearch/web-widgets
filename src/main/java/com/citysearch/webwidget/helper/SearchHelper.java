package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.SearchRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * This class performs all the functionalities related to Search API like
 * validating query parameters, querying API and processing response Constructs
 * request with different parameters and processes response accordingly for
 * various APIs
 * 
 * Sample Searh API URL:
 * http://api.citysearch.com/search/locations?api_key=gunyay6vkqnvc2geyfedbdt3
 * &what=sushi&where=Pasadena%2C+ca&publisher=acme&&rpp=20&radius=25
 * 
 * @author Aspert Benjamin
 * 
 */
public class SearchHelper {

	public final static String PROPERTY_SEARCH_URL = "search.url";

	private Logger log = Logger.getLogger(getClass());

	private static final String DEFAULT_RADIUS = "25";
	private static final String DEFAULT_RPP = "20";

	private static final String ADDRESS_TAG = "address";
	private static final String LISTING_ID_TAG = "id";
	private static final String REVIEWS_TAG = "userreviewcount";
	private static final String TAGLINE_TAG = "samplecategories";
	private static final String PHONE_TAG = "phonenumber";
	private static final String AD_DISPLAY_URL_TAG = "profile";
	private static final String AD_IMAGE_URL_TAG = "image";
	private static final String REVIEW_RATING_TAG = "rating";
	private static final String LOCATION_TAG = "location";

	private String rootPath;
	private Integer displaySize;

	public SearchHelper(String rootPath, Integer displaySize) {
		this.rootPath = rootPath;
		this.displaySize = displaySize;
	}

	/**
	 * Returns the Search API Query String
	 * 
	 * @param request
	 * @return
	 * @throws CitysearchException
	 */
	private String getQueryString(SearchRequest request)
			throws CitysearchException {
		StringBuilder strBuilder = new StringBuilder(request.getQueryString());
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		return strBuilder.toString();
	}

	/**
	 * Validates the Search API request parameters for fetching nearest Postal
	 * Code
	 * 
	 * @param request
	 * @throws CitysearchException
	 */
	private void validateClosestLocationPostalCodeRequest(SearchRequest request)
			throws InvalidRequestParametersException, CitysearchException {
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();

		if (StringUtils.isBlank(request.getPublisher())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getLatitude())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.LATITUDE_ERROR));
		}
		if (StringUtils.isBlank(request.getLongitude())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.LONGITUDE_ERROR));
		}
		if (StringUtils.isBlank(request.getRadius())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.RADIUS_ERROR));
		}
		if (!errors.isEmpty()) {
			throw new InvalidRequestParametersException(this.getClass()
					.getName(), "validateClosestLocationPostalCodeRequest",
					"Invalid parameters.", errors);
		}
	}

	/**
	 * Validates the request parameters, calls Search API and returns the
	 * closest Postal Code from the response
	 * 
	 * @param request
	 * @return String
	 * @throws CitysearchException
	 */
	public String getClosestLocationPostalCode(SearchRequest request)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("SearchHelper.getClosestLocationPostalCode: Begin");
		validateClosestLocationPostalCodeRequest(request);
		log.info("SearchHelper.getClosestLocationPostalCode: After validate");
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PROPERTY_SEARCH_URL));
		urlStringBuilder.append(getQueryString(request));
		log.info("SearchHelper.getClosestLocationPostalCode: Query "
				+ urlStringBuilder.toString());
		Document responseDocument = null;
		try {
			responseDocument = HelperUtil.getAPIResponse(urlStringBuilder
					.toString(), null);
			log
					.info("SearchHelper.getClosestLocationPostalCode: Successfull response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getClosestLocationPostalCode", ihe);
		}
		String nearestListingPostalCode = findClosestLocationPostalCode(responseDocument);
		log.info("SearchHelper.getClosestLocationPostalCode: Postal Code "
				+ nearestListingPostalCode);
		if (nearestListingPostalCode == null) {
			log
					.info("SearchHelper.getClosestLocationPostalCode: No postal code. Exception.");
			throw new CitysearchException(this.getClass().getName(),
					"getClosestLocationPostalCode", "No locations found.");
		}
		log.info("SearchHelper.getClosestLocationPostalCode: End");
		return nearestListingPostalCode;
	}

	/**
	 * Returns the nearest Postal Code from the Search API Response
	 * 
	 * @param doc
	 * @return String
	 * @throws CitysearchException
	 */
	private String findClosestLocationPostalCode(Document doc)
			throws CitysearchException {
		String closestPostalCode = null;
		if (doc != null && doc.hasRootElement()) {
			Element rootElement = doc.getRootElement();
			List<Element> locationList = rootElement.getChildren("location");
			Double closest = 100000.000D;
			for (int i = 0; i < locationList.size(); i++) {
				Element locationElm = locationList.get(i);
				Element addressElm = locationElm.getChild("address");
				String distanceStr = locationElm.getChildText("distance");
				Double distance = NumberUtils.toDouble(distanceStr);
				String postalCode = addressElm.getChildText("postalcode");
				if ((closestPostalCode == null || distance < closest)
						&& postalCode != null) {
					closestPostalCode = postalCode;
					closest = distance;
				}
			}
		}
		return closestPostalCode;
	}

	/**
	 * Validates the Search API request parameters to get latitude and longitude
	 * 
	 * @param request
	 * @throws CitysearchException
	 */
	private void validateRequest(SearchRequest request)
			throws InvalidRequestParametersException, CitysearchException {
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();
		if (StringUtils.isBlank(request.getWhat())
				&& StringUtils.isBlank(request.getTags())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.WHAT_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getWhere())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.WHERE_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getPublisher())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}

		if (!errors.isEmpty()) {
			throw new InvalidRequestParametersException(this.getClass()
					.getName(), "validateLatitudeLongitudeRequest",
					"Invalid parameters.", errors);
		}
	}

	/**
	 * Constructs Search API request to get latitude and longitude values
	 * 
	 * @param request
	 * @return
	 * @throws CitysearchException
	 */
	private String getSearchRequestQueryString(SearchRequest request)
			throws InvalidRequestParametersException, CitysearchException {
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
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.TAG, request.getTags()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		String rpp = (StringUtils.isBlank(request.getRpp())) ? DEFAULT_RPP
				: request.getRpp();
		apiQueryString.append(HelperUtil.constructQueryParam("rpp", rpp));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		String radius = (StringUtils.isBlank(request.getRadius())) ? DEFAULT_RADIUS
				: request.getRadius();
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.RADIUS, radius));
		return apiQueryString.toString();

	}

	/**
	 * Queries the Search API and returns latitude, longitude values in a String
	 * Array
	 * 
	 * @param request
	 * @return String[]
	 * @throws CitysearchException
	 */
	public String[] getLatitudeLongitude(SearchRequest request)
			throws CitysearchException {
		log.info("SearchHelper.getLatitudeLongitude: Begin");
		validateRequest(request);
		log.info("SearchHelper.getLatitudeLongitude: After validate");
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_SEARCH_URL)
				+ getSearchRequestQueryString(request);
		log.info("SearchHelper.getLatitudeLongitude: Query " + urlString);
		Document responseDocument = null;
		try {
			responseDocument = HelperUtil.getAPIResponse(urlString, null);
			log
					.info("SearchHelper.getLatitudeLongitude: Successfull response.");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getLatitudeLongitude", ihe);
		}
		String[] latLonValues = getLatitudeAndLongitude(responseDocument);
		log
				.info("SearchHelper.getLatitudeLongitude: Lat & Lon "
						+ latLonValues);
		log.info("SearchHelper.getLatitudeLongitude: End");
		return latLonValues;
	}

	public List<NearbyPlace> getNearbyPlaces(SearchRequest request)
			throws CitysearchException {
		log.info("SearchHelper.getNearbyPlaces: Begin");
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PROPERTY_SEARCH_URL));
		urlStringBuilder.append(getQueryString(request));
		log.info("SearchHelper.getNearbyPlaces: Query "
				+ urlStringBuilder.toString());
		Document responseDocument = null;
		try {
			responseDocument = HelperUtil.getAPIResponse(urlStringBuilder
					.toString(), null);
			log.info("SearchHelper.getNearbyPlaces: Successfull response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getNearbyPlaces", ihe);
		}
		// If where is passed, read the lat lon for the where from the region
		// element.
		BigDecimal sourceLatitude = null;
		BigDecimal sourceLongitude = null;
		if (StringUtils.isBlank(request.getLatitude())
				|| StringUtils.isBlank(request.getLongitude())) {
			// Search API has to return these values. If not, its a API bug.
			String[] latlon = getLatitudeAndLongitude(responseDocument);
			sourceLatitude = new BigDecimal(latlon[0]);
			sourceLongitude = new BigDecimal(latlon[1]);
		} else {
			sourceLatitude = new BigDecimal(request.getLatitude());
			sourceLongitude = new BigDecimal(request.getLongitude());
		}
		log.info("SearchHelper.getNearbyPlaces: End");
		return getNearbyPlaces(request, sourceLatitude, sourceLongitude,
				responseDocument);
	}

	private String[] getLatitudeAndLongitude(Document document) {
		String[] latLonValues = new String[2];
		if (document != null && document.hasRootElement()) {
			Element rootElement = document.getRootElement();
			Element region = rootElement.getChild("region");
			if (region != null) {
				String sLat = region.getChildText(CommonConstants.LATITUDE);
				String sLon = region.getChildText(CommonConstants.LONGITUDE);
				if (sLat != null && sLon != null) {
					latLonValues[0] = sLat;
					latLonValues[1] = sLon;
				}
			}
		}
		return latLonValues;
	}

	private List<NearbyPlace> getNearbyPlaces(SearchRequest request,
			BigDecimal sourceLatitude, BigDecimal sourceLongitude, Document doc)
			throws CitysearchException {
		log.info("SearchHelper.getNearbyPlaces: Begin");
		List<NearbyPlace> nearbyPlaces = null;
		if (doc != null && doc.hasRootElement()) {
			SortedMap<Double, List<Element>> elmsSortedByDistance = new TreeMap<Double, List<Element>>();
			Element rootElement = doc.getRootElement();
			List<Element> children = rootElement.getChildren(LOCATION_TAG);
			if (children != null && !children.isEmpty()) {
				int childrenSize = children.size();
				for (Element elm : children) {
					BigDecimal businessLatitude = new BigDecimal(elm
							.getChildText(CommonConstants.LATITUDE));
					BigDecimal businessLongitude = new BigDecimal(elm
							.getChildText(CommonConstants.LONGITUDE));
					double distance = HelperUtil.getDistance(sourceLatitude,
							sourceLongitude, businessLatitude,
							businessLongitude);
					if (childrenSize <= displaySize
							|| distance < CommonConstants.EXTENDED_RADIUS) {
						// Since we are rounding the distance to the 10th,
						// There might be
						// multiple listings with the same distance.
						if (elmsSortedByDistance.containsKey(distance)) {
							elmsSortedByDistance.get(distance).add(elm);
						} else {
							List<Element> elms = new ArrayList<Element>();
							elms.add(elm);
							elmsSortedByDistance.put(distance, elms);
						}
					}
				}
				if (!elmsSortedByDistance.isEmpty()) {
					List<Element> elmsToConvert = new ArrayList<Element>();
					for (int j = 0; j < elmsSortedByDistance.size(); j++) {
						if (elmsToConvert.size() >= displaySize) {
							break;
						}
						Double key = elmsSortedByDistance.firstKey();
						List<Element> elms = elmsSortedByDistance.remove(key);
						for (int idx = 0; idx < elms.size(); idx++) {
							if (elmsToConvert.size() == displaySize) {
								break;
							}
							elmsToConvert.add(elms.get(idx));
						}
					}

					nearbyPlaces = new ArrayList<NearbyPlace>();
					for (Element elm : elmsToConvert) {
						nearbyPlaces.add(toNearbyPlace(request, sourceLatitude,
								sourceLongitude, elm));
					}
					NearbyPlacesHelper.addDefaultImages(nearbyPlaces,
							this.rootPath);
				}
			}
		}
		log.info("NearbyPlacesHelper.getNearbyPlaces: End");
		return nearbyPlaces;
	}

	private NearbyPlace toNearbyPlace(SearchRequest request,
			BigDecimal sourceLat, BigDecimal sourceLon, Element location)
			throws CitysearchException {
		NearbyPlace nearbyPlace = new NearbyPlace();

		Element address = location.getChild(ADDRESS_TAG);

		String addr = null;
		if (address != null) {
			addr = HelperUtil.getLocationString(address
					.getChildText(CommonConstants.CITY), address
					.getChildText(CommonConstants.STATE));
			nearbyPlace.setStreet(address.getChildText(CommonConstants.STREET));
			nearbyPlace.setCity(address.getChildText(CommonConstants.CITY));
			nearbyPlace.setState(address.getChildText(CommonConstants.STATE));
			nearbyPlace.setPostalCode(address
					.getChildText(CommonConstants.POSTALCODE));
			nearbyPlace.setLocation(addr);
		}
		
		String adUnitIdentifier = request.getAdUnitIdentifier();

		StringBuilder nameLengthProp = new StringBuilder(adUnitIdentifier);
		nameLengthProp.append(".");
		nameLengthProp.append(CommonConstants.NAME_LENGTH);

		String name = location.getChildText(CommonConstants.NAME);
		name = HelperUtil.getAbbreviatedString(name, nameLengthProp.toString());
		nearbyPlace.setName(name);

		String rating = location.getChildText(REVIEW_RATING_TAG);
		List<Integer> ratingList = HelperUtil.getRatingsList(rating);
		double ratings = HelperUtil.getRatingValue(rating);
		nearbyPlace.setRating(ratingList);
		nearbyPlace.setRatings(ratings);

		String reviewCount = location.getChildText(REVIEWS_TAG);
		int userReviewCount = HelperUtil.toInteger(reviewCount);
		nearbyPlace.setReviewCount(userReviewCount);

		// Do not use the distance element here. Because the distance element is
		// returned only if latlon is passed.
		String dLat = location.getChildText(CommonConstants.LATITUDE);
		String dLon = location.getChildText(CommonConstants.LONGITUDE);
		BigDecimal destLat = new BigDecimal(dLat);
		BigDecimal destLon = new BigDecimal(dLon);
		double distance = HelperUtil.getDistance(sourceLat, sourceLon, destLat,
				destLon);
		nearbyPlace.setDistance(distance);

		nearbyPlace.setListingId(location.getAttributeValue(LISTING_ID_TAG));
		
		StringBuilder tagLengthProp = new StringBuilder(adUnitIdentifier);
		tagLengthProp.append(".");
		tagLengthProp.append(CommonConstants.TAGLINE_LENGTH);
		String category = location.getChildText(TAGLINE_TAG);
		category = HelperUtil.getAbbreviatedString(category, tagLengthProp
				.toString());
		nearbyPlace.setCategory(category);

		nearbyPlace.setAdDisplayURL(location.getChildText(AD_DISPLAY_URL_TAG));
		nearbyPlace.setAdImageURL(location.getChildText(AD_IMAGE_URL_TAG));
		nearbyPlace.setPhone(location.getChildText(PHONE_TAG));
		nearbyPlace.setOffers(location.getChildText(CommonConstants.OFFERS));

		nearbyPlace.setCallBackFunction(request.getCallBackFunction());
		nearbyPlace.setCallBackUrl(request.getCallBackUrl());

		String adDisplayTrackingUrl = HelperUtil.getTrackingUrl(nearbyPlace
				.getAdDisplayURL(), null, request.getCallBackUrl(), request
				.getDartClickTrackUrl(), nearbyPlace.getListingId(),
				nearbyPlace.getPhone(), request.getPublisher(), request
						.getAdUnitName(), request.getAdUnitSize());
		nearbyPlace.setAdDisplayTrackingURL(adDisplayTrackingUrl);

		String callBackFn = HelperUtil.getCallBackFunctionString(request
				.getCallBackFunction(), nearbyPlace.getListingId(), nearbyPlace
				.getPhone());
		nearbyPlace.setCallBackFunction(callBackFn);

		return nearbyPlace;
	}
}
