package com.citysearch.webwidget.facade.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.SearchLocation;
import com.citysearch.webwidget.api.bean.SearchResponse;
import com.citysearch.webwidget.api.proxy.SearchProxy;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.Utils;

public class SearchHelper {
	private Logger log = Logger.getLogger(getClass());
	private String rootPath;
	private Integer displaySize;

	public SearchHelper(String rootPath, Integer displaySize) {
		this.rootPath = rootPath;
		this.displaySize = displaySize;
	}

	public List<NearbyPlace> getNearbyPlaces(RequestBean request)
			throws CitysearchException {
		log.info("SearchHelper.getNearbyPlaces: Begin");
		SearchProxy proxy = new SearchProxy();
		SearchResponse searchResponse = proxy
				.getLocations(request, displaySize);
		BigDecimal sourceLatitude = null;
		BigDecimal sourceLongitude = null;
		if (StringUtils.isBlank(request.getLatitude())
				|| StringUtils.isBlank(request.getLongitude())) {
			sourceLatitude = searchResponse.getLatitude();
			sourceLongitude = searchResponse.getLongitude();
		} else {
			sourceLatitude = new BigDecimal(request.getLatitude());
			sourceLongitude = new BigDecimal(request.getLongitude());
		}
		log.info("SearchHelper.getNearbyPlaces: End");
		return toNearbyPlaces(request, sourceLatitude, sourceLongitude,
				searchResponse.getLocations());
	}

	private List<NearbyPlace> toNearbyPlaces(RequestBean request,
			BigDecimal sourceLatitude, BigDecimal sourceLongitude,
			List<SearchLocation> locations) throws CitysearchException {
		log.info("SearchHelper.toNearbyPlaces: Begin");
		List<NearbyPlace> nearbyPlaces = new ArrayList<NearbyPlace>();
		if (locations != null && !locations.isEmpty()) {
			nearbyPlaces = new ArrayList<NearbyPlace>();
			for (SearchLocation location : locations) {
				nearbyPlaces.add(toNearbyPlace(request, sourceLatitude,
						sourceLongitude, location));
			}
			NearbyPlacesHelper.addDefaultImages(nearbyPlaces, rootPath);
		}
		log.info("SearchHelper.toNearbyPlaces: End");
		return nearbyPlaces;
	}

	private NearbyPlace toNearbyPlace(RequestBean request,
			BigDecimal sourceLat, BigDecimal sourceLon, SearchLocation location)
			throws CitysearchException {
		NearbyPlace nearbyPlace = new NearbyPlace();

		String addr = Utils.getLocationString(location.getCity(), location
				.getState());
		nearbyPlace.setStreet(location.getStreet());
		nearbyPlace.setCity(location.getCity());
		nearbyPlace.setState(location.getState());
		nearbyPlace.setPostalCode(location.getPostalCode());
		nearbyPlace.setLocation(addr);

		String adUnitIdentifier = request.getAdUnitIdentifier();

		StringBuilder nameLengthProp = new StringBuilder(adUnitIdentifier);
		nameLengthProp.append(".");
		nameLengthProp.append(CommonConstants.NAME_LENGTH);

		String name = location.getName();
		name = HelperUtil.getAbbreviatedString(name, nameLengthProp.toString());
		nearbyPlace.setName(name);

		String rating = location.getRating();
		List<Integer> ratingList = HelperUtil.getRatingsList(rating);
		double ratings = HelperUtil.getRatingValue(rating);
		nearbyPlace.setRating(ratingList);
		nearbyPlace.setRatings(ratings);

		String reviewCount = location.getReviewCount();
		int userReviewCount = HelperUtil.toInteger(reviewCount);
		nearbyPlace.setReviewCount(userReviewCount);

		// Do not use the distance element here. Because the distance element is
		// returned only if latlon is passed.
		String dLat = location.getLatitude();
		String dLon = location.getLongitude();
		BigDecimal destLat = new BigDecimal(dLat);
		BigDecimal destLon = new BigDecimal(dLon);
		double distance = HelperUtil.getDistance(sourceLat, sourceLon, destLat,
				destLon);
		nearbyPlace.setDistance(distance);

		nearbyPlace.setListingId(location.getListingId());

		StringBuilder tagLengthProp = new StringBuilder(adUnitIdentifier);
		tagLengthProp.append(".");
		tagLengthProp.append(CommonConstants.TAGLINE_LENGTH);
		String category = location.getCategory();
		category = HelperUtil.getAbbreviatedString(category, tagLengthProp
				.toString());
		nearbyPlace.setCategory(category);

		nearbyPlace.setAdDisplayURL(location.getAdDisplayUrl());
		nearbyPlace.setAdImageURL(location.getImageUrl());
		nearbyPlace.setPhone(location.getPhone());
		nearbyPlace.setOffers(location.getOffers());

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
