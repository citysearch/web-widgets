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

public class YelpNearByPlacesFacade extends AbstractNearByPlacesFacade {
	private Logger log = Logger.getLogger(getClass());

	protected YelpNearByPlacesFacade(String contextPath, int displaySize) {
		super(contextPath, displaySize);
	}

	@Override
	public NearbyPlacesResponse getNearbyPlaces(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("YelpNearByPlacesFacade.getNearbyPlaces: Begin");
		request.validate();
		return findNearbyPlaces(request);
	}

	private NearbyPlacesResponse findNearbyPlaces(RequestBean request)
			throws CitysearchException {
		request.setRotation(true);
		NearbyPlacesResponse response = new NearbyPlacesResponse();
		List<NearbyPlace> nearbyPlaces = getAdsFromPFPLocationCustom(request);
		List<NearbyPlace> backfill = null;
		List<HouseAd> houseAds = null;
		List<NearbyPlace> searchResults = null;
		int noOfBackFillNeeded = (nearbyPlaces == null || nearbyPlaces
				.isEmpty()) ? displaySize : displaySize - nearbyPlaces.size();
		if (noOfBackFillNeeded > 0) {
			backfill = getNearbyPlacesBackfill(request, noOfBackFillNeeded);
			int noOfHouseAdsNeeded = (backfill == null || backfill.isEmpty()) ? noOfBackFillNeeded
					: noOfBackFillNeeded - backfill.size();
			if (noOfHouseAdsNeeded > 0) {
				houseAds = HouseAdsUtil.getHouseAds(getContextPath(), request
						.getDartClickTrackUrl());
				houseAds = houseAds.subList(0, noOfHouseAdsNeeded);
			} else if (noOfHouseAdsNeeded < 0) {
				backfill = backfill.subList(0, noOfBackFillNeeded);
			}
		}

		response.setNearbyPlaces(nearbyPlaces);
		response.setBackfill(backfill);
		response.setSearchResults(searchResults);
		response.setHouseAds(houseAds);
		return response;
	}
}
