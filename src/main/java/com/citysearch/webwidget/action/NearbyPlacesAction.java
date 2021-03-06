package com.citysearch.webwidget.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.AbstractNearByPlacesFacade;
import com.citysearch.webwidget.facade.NearByPlacesFacadeFactory;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.OneByOneTrackingUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class NearbyPlacesAction extends AbstractCitySearchAction implements
		ModelDriven<RequestBean> {

	private Logger log = Logger.getLogger(getClass());

	private static final String ACTION_FORWARD_CONQUEST = "conquest";

	private RequestBean nearbyPlacesRequest = new RequestBean();
	private NearbyPlacesResponse nearbyPlacesResponse;

	private boolean backfill;

	public RequestBean getModel() {
		return nearbyPlacesRequest;
	}

	public RequestBean getNearbyPlacesRequest() {
		return nearbyPlacesRequest;
	}

	public void setNearbyPlacesRequest(RequestBean nearbyPlacesRequest) {
		this.nearbyPlacesRequest = nearbyPlacesRequest;
	}

	public List<NearbyPlace> getSearchResults() {
		if (nearbyPlacesResponse == null
				|| nearbyPlacesResponse.getSearchResults() == null) {
			return new ArrayList<NearbyPlace>();
		}
		return nearbyPlacesResponse.getSearchResults();
	}

	public List<NearbyPlace> getNearbyPlaces() {
		if (nearbyPlacesResponse == null
				|| nearbyPlacesResponse.getNearbyPlaces() == null) {
			return new ArrayList<NearbyPlace>();
		}
		return nearbyPlacesResponse.getNearbyPlaces();
	}

	public List<NearbyPlace> getBackfill() {
		if (nearbyPlacesResponse == null
				|| nearbyPlacesResponse.getBackfill() == null) {
			return new ArrayList<NearbyPlace>();
		}
		return nearbyPlacesResponse.getBackfill();
	}

	public List<HouseAd> getHouseAds() {
		if (nearbyPlacesResponse == null
				|| nearbyPlacesResponse.getHouseAds() == null) {
			return new ArrayList<HouseAd>();
		}
		return nearbyPlacesResponse.getHouseAds();
	}

	public String execute() throws CitysearchException {
		log.info("Begin NearbyPlacesAction");

		Object requestAttrib = getHttpRequest().getAttribute(
				REQUEST_ATTRIBUTE_BACKFILL);
		backfill = (requestAttrib != null && requestAttrib instanceof Boolean) ? (Boolean) requestAttrib
				: false;
		String backfillFor = null;
		if (backfill) {
			String adUnitSize = (String) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_ADUNIT_SIZE);
			Integer displaySize = (Integer) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE);
			String latitude = (String) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_LATITUDE);
			String longitude = (String) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_LONGITUDE);
			backfillFor = (String) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_BACKFILL_FOR);
			nearbyPlacesRequest.setAdUnitSize(adUnitSize);
			nearbyPlacesRequest
					.setAdUnitName(CommonConstants.AD_UNIT_NAME_NEARBY);
			nearbyPlacesRequest.setDisplaySize(displaySize);
			nearbyPlacesRequest.setLatitude(latitude);
			nearbyPlacesRequest.setLongitude(longitude);
		}
		if (nearbyPlacesRequest.getDisplaySize() == null) {
			nearbyPlacesRequest
					.setDisplaySize(CommonConstants.DEFAULT_NEARBY_DISPLAY_SIZE);
		}
		if (nearbyPlacesRequest.getAdUnitSize() == null) {
			nearbyPlacesRequest.setAdUnitSize(CommonConstants.MANTLE_AD_SIZE);
		}

		String adUnitSize = nearbyPlacesRequest.getAdUnitSize();

		try {
			AbstractNearByPlacesFacade facade = NearByPlacesFacadeFactory
					.getFacade(nearbyPlacesRequest.getPublisher(),
							getResourceRootPath(), nearbyPlacesRequest
									.getDisplaySize());
			nearbyPlacesResponse = facade.getNearbyPlaces(nearbyPlacesRequest);
		} catch (InvalidRequestParametersException ihre) {
			log.error(ihre.getDetailedMessage());
			nearbyPlacesResponse = new NearbyPlacesResponse();
			nearbyPlacesResponse.setHouseAds(getHouseAds(nearbyPlacesRequest
					.getDartClickTrackUrl(), nearbyPlacesRequest
					.getDisplaySize()));
		} catch (Exception e) {
			// On any exception, want the house ads to be returned.
			// Idea is to not return a blank widget.
			log.error(e.getMessage());
			// Log the stacktrace also for now
			StackTraceElement[] elms = e.getStackTrace();
			for (int k = 0; k < elms.length; k++) {
				log.error(elms[k]);
			}
			nearbyPlacesResponse = new NearbyPlacesResponse();
			nearbyPlacesResponse.setHouseAds(getHouseAds(nearbyPlacesRequest
					.getDartClickTrackUrl(), nearbyPlacesRequest
					.getDisplaySize()));
		}

		set1x1TrackingPixel(nearbyPlacesRequest.getAdUnitName(),
				nearbyPlacesRequest.getAdUnitSize());
		if (backfill) {
			set1x1TrackingPixelForBackfilledUnit(backfillFor,
					nearbyPlacesRequest.getAdUnitSize());
		}
		log.info("End NearbyPlacesAction");

		if (adUnitSize != null
				&& adUnitSize.equals(CommonConstants.CONQUEST_AD_SIZE))
			return ACTION_FORWARD_CONQUEST;
		else
			return Action.SUCCESS;
	}

	private void set1x1TrackingPixel(String adunitName, String adunitSize) {
		int backfillSize = getBackfill().size();
		int searchResultsSize = getSearchResults().size();
		int houseAdsSize = getHouseAds().size();
		int pfpResultsSize = getNearbyPlaces().size();
		try {
			String oneByOneTrackingUrl = OneByOneTrackingUtil
					.get1x1TrackingUrl(adunitName, adunitSize, pfpResultsSize,
							backfillSize, searchResultsSize, houseAdsSize);
			setOneByOneTrackingUrl(oneByOneTrackingUrl);
		} catch (CitysearchException exp) {
			// DO not throw the exception.
			log.error(exp.getMessage());
		}
	}

	private void set1x1TrackingPixelForBackfilledUnit(String adunitName,
			String adunitSize) {
		try {
			String oneByOneTrackingUrlForOriginal = OneByOneTrackingUtil
					.get1x1TrackingUrl(adunitName, adunitSize, null, null,
							null, null);
			setOneByOneTrackingUrlForOriginal(oneByOneTrackingUrlForOriginal);
		} catch (CitysearchException exp) {
			// DO not throw the exception.
			log.error(exp.getMessage());
		}
	}
}
