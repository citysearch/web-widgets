package com.citysearch.webwidget.action.project.yellow;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.action.AbstractCitySearchAction;
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
import com.citysearch.webwidget.util.PropertiesLoader;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class NearbyPlacesAction extends AbstractCitySearchAction implements
		ModelDriven<RequestBean> {
	private Logger log = Logger.getLogger(getClass());

	private static final String RETURN_HOUSEADS_PROPERTY = "projectyellow.return.houseads";
	private static final Integer DEFAULT_DISPLAY_SIZE = 2;
	private static final String ADUNIT_SIZE = "660x80";
	private RequestBean nearbyPlacesRequest = new RequestBean();
	private NearbyPlacesResponse nearbyPlacesResponse;

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
		log.info("Begin Project yellow NearbyPlacesAction");
		// Publisher is Yelp for project yellow always.
		nearbyPlacesRequest
				.setPublisher(CommonConstants.PUBLISHER_PROJECT_YELLOW);
		nearbyPlacesRequest.setAdUnitName(CommonConstants.AD_UNIT_NAME_NEARBY);
		nearbyPlacesRequest.setDisplaySize(DEFAULT_DISPLAY_SIZE);
		nearbyPlacesRequest.setAdUnitSize(ADUNIT_SIZE);

		try {
			AbstractNearByPlacesFacade facade = NearByPlacesFacadeFactory
					.getFacade(nearbyPlacesRequest.getPublisher(),
							getResourceRootPath(), nearbyPlacesRequest
									.getDisplaySize());
			nearbyPlacesResponse = facade.getNearbyPlaces(nearbyPlacesRequest);
			log.info("End NearbyPlacesAction");
		} catch (InvalidRequestParametersException ihre) {
			log.error(ihre.getDetailedMessage());
			nearbyPlacesResponse = new NearbyPlacesResponse();
			nearbyPlacesResponse.setHouseAds(getHouseAds(nearbyPlacesRequest
					.getDartClickTrackUrl(), nearbyPlacesRequest
					.getDisplaySize()));
		} catch (Exception e) {
			log.error(e.getMessage());
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

		// For project yellow make the return of house ads configurable.
		// We still want to return the tracking pixel but not the actual ad.
		Properties appProperties = PropertiesLoader.getApplicationProperties();
		if (appProperties.containsKey(RETURN_HOUSEADS_PROPERTY)
				&& !Boolean.parseBoolean((String) appProperties
						.get(RETURN_HOUSEADS_PROPERTY))) {
			nearbyPlacesResponse.setHouseAds(null);
		}
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
}
