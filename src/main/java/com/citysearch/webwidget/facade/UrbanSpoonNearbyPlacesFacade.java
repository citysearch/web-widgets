package com.citysearch.webwidget.facade;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.HouseAdsUtil;

public class UrbanSpoonNearbyPlacesFacade extends AbstractNearByPlacesFacade {
	private Logger log = Logger.getLogger(getClass());

	protected UrbanSpoonNearbyPlacesFacade(String contextPath, int displaySize) {
		super(contextPath, displaySize);
	}

	public NearbyPlacesResponse getNearbyPlaces(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		request.validate();
		return findNearbyPlaces(request);
	}

	private NearbyPlacesResponse findNearbyPlaces(RequestBean request)
			throws CitysearchException {
		NearbyPlacesResponse response = new NearbyPlacesResponse();

		List<HouseAd> houseAds = null;
		int noOfSearchResultsNeeded = this.displaySize;
		List<NearbyPlace> searchResults = getSearchResults(request,
				this.displaySize);
		int noOfHouseAdsNeeded = (searchResults == null || searchResults
				.isEmpty()) ? noOfSearchResultsNeeded : noOfSearchResultsNeeded
				- searchResults.size();
		if (noOfHouseAdsNeeded > 0) {
			houseAds = HouseAdsUtil.getHouseAds(getContextPath(), request
					.getDartClickTrackUrl());
			houseAds = houseAds.subList(0, noOfHouseAdsNeeded);
		} else if (noOfHouseAdsNeeded < 0) {
			searchResults = searchResults.subList(0, noOfSearchResultsNeeded);
		}

		response.setNearbyPlaces(null);
		response.setBackfill(null);
		response.setSearchResults(searchResults);
		response.setHouseAds(houseAds);
		return response;
	}

}
