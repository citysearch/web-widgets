package com.citysearch.webwidget.api.proxy;

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

import com.citysearch.webwidget.api.bean.SearchLocation;
import com.citysearch.webwidget.api.bean.SearchResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class SearchProxy extends AbstractProxy {
	public final static String PROPERTY_SEARCH_URL = "search.url";
	private static final String LOCATION_TAG = "location";
	private Logger log = Logger.getLogger(getClass());

	private static final String ADDRESS_TAG = "address";
	private static final String LISTING_ID_TAG = "id";
	private static final String REVIEWS_TAG = "userreviewcount";
	private static final String TAGLINE_TAG = "samplecategories";
	private static final String PHONE_TAG = "phonenumber";
	private static final String AD_DISPLAY_URL_TAG = "profile";
	private static final String AD_IMAGE_URL_TAG = "image";
	private static final String REVIEW_RATING_TAG = "rating";

	private SearchResponse parseLocations(Document doc,
			BigDecimal sourceLatitude, BigDecimal sourceLongitude,
			int requiredNoOfLocations) throws CitysearchException {
		log.info("SearchProxy.parseLocations: Begin");
		SearchResponse response = new SearchResponse();
		List<SearchLocation> locations = null;
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
					if (childrenSize <= requiredNoOfLocations) {
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
						if (elmsToConvert.size() >= requiredNoOfLocations) {
							break;
						}
						Double key = elmsSortedByDistance.firstKey();
						List<Element> elms = elmsSortedByDistance.remove(key);
						for (int idx = 0; idx < elms.size(); idx++) {
							if (elmsToConvert.size() == requiredNoOfLocations) {
								break;
							}
							elmsToConvert.add(elms.get(idx));
						}
					}

					locations = new ArrayList<SearchLocation>();
					for (Element elm : elmsToConvert) {
						locations.add(toSearchLocation(elm));
					}
					response.setLocations(locations);
				}
			}
		}
		log.info("SearchProxy.parseLocations: End");
		return response;
	}

	private SearchLocation toSearchLocation(Element location) {
		SearchLocation searchLoc = new SearchLocation();
		Element address = location.getChild(ADDRESS_TAG);
		if (address != null) {
			searchLoc.setStreet(address.getChildText(CommonConstants.STREET));
			searchLoc.setCity(address.getChildText(CommonConstants.CITY));
			searchLoc.setState(address.getChildText(CommonConstants.STATE));
			searchLoc.setPostalCode(address
					.getChildText(CommonConstants.POSTALCODE));
		}
		searchLoc.setName(location.getChildText(CommonConstants.NAME));
		searchLoc.setRating(location.getChildText(REVIEW_RATING_TAG));
		searchLoc.setReviewCount(location.getChildText(REVIEWS_TAG));
		searchLoc.setLatitude(location.getChildText(CommonConstants.LATITUDE));
		searchLoc
				.setLongitude(location.getChildText(CommonConstants.LONGITUDE));
		searchLoc.setListingId(location.getAttributeValue(LISTING_ID_TAG));
		searchLoc.setCategory(location.getChildText(TAGLINE_TAG));
		searchLoc.setAdDisplayUrl(location.getChildText(AD_DISPLAY_URL_TAG));
		searchLoc.setImageUrl(location.getChildText(AD_IMAGE_URL_TAG));
		searchLoc.setPhone(location.getChildText(PHONE_TAG));
		searchLoc.setOffers(location.getChildText(CommonConstants.OFFERS));
		return searchLoc;
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

	/**
	 * Validates the request parameters, calls Search API and returns the
	 * closest Postal Code from the response
	 * 
	 * @param request
	 * @return String
	 * @throws CitysearchException
	 */
	public String getClosestLocationPostalCode(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("SearchProxy.getClosestLocationPostalCode: Begin");
		request.validate();
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PROPERTY_SEARCH_URL));
		urlStringBuilder.append(getQueryString(request));
		log.info("SearchProxy.getClosestLocationPostalCode: Query "
				+ urlStringBuilder.toString());
		Document responseDocument = null;
		try {
			responseDocument = getAPIResponse(urlStringBuilder.toString(), null);
			log
					.info("SearchProxy.getClosestLocationPostalCode: Successfull response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getClosestLocationPostalCode", ihe);
		}
		String nearestListingPostalCode = findClosestLocationPostalCode(responseDocument);
		log.info("SearchProxy.getClosestLocationPostalCode: Postal Code "
				+ nearestListingPostalCode);
		if (nearestListingPostalCode == null) {
			log
					.info("SearchProxy.getClosestLocationPostalCode: No postal code. Exception.");
			throw new CitysearchException(this.getClass().getName(),
					"getClosestLocationPostalCode", "No locations found.");
		}
		log.info("SearchProxy.getClosestLocationPostalCode: End");
		return nearestListingPostalCode;
	}

	/**
	 * Queries the Search API and returns latitude, longitude values in a String
	 * Array
	 * 
	 * @param request
	 * @return String[]
	 * @throws CitysearchException
	 */
	public String[] getLatitudeLongitude(RequestBean request)
			throws CitysearchException {
		log.info("SearchProxy.getLatitudeLongitude: Begin");
		request.validate();
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_SEARCH_URL)
				+ getWhereQueryString(request);
		log.info("SearchProxy.getLatitudeLongitude: Query " + urlString);
		Document responseDocument = null;
		try {
			responseDocument = getAPIResponse(urlString, null);
			log.info("SearchProxy.getLatitudeLongitude: Successfull response.");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getLatitudeLongitude", ihe);
		}
		String[] latLonValues = getLatitudeAndLongitude(responseDocument);
		log.info("SearchProxy.getLatitudeLongitude: Lat & Lon " + latLonValues);
		log.info("SearchProxy.getLatitudeLongitude: End");
		return latLonValues;
	}

	public SearchResponse getLocations(RequestBean request,
			int requiredNumberOfLocations) throws CitysearchException {
		log.info("SearchProxy.getNearbyPlaces: Begin");
		request.validate();
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PROPERTY_SEARCH_URL));
		urlStringBuilder.append(getQueryString(request));
		log.info("SearchProxy.getNearbyPlaces: Query "
				+ urlStringBuilder.toString());
		Document responseDocument = null;
		try {
			responseDocument = getAPIResponse(urlStringBuilder.toString(), null);
			log.info("SearchProxy.getNearbyPlaces: Successfull response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getListings", ihe);
		}

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

		SearchResponse response = parseLocations(responseDocument,
				sourceLatitude, sourceLongitude, requiredNumberOfLocations);
		response.setLatitude(sourceLatitude);
		response.setLongitude(sourceLongitude);

		log.info("SearchProxy.getListings: End");
		return response;
	}
}
