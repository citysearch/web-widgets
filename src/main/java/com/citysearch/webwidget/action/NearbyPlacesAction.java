package com.citysearch.webwidget.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.NearbyPlacesHelper;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class NearbyPlacesAction extends AbstractCitySearchAction implements
		ModelDriven<NearbyPlacesRequest> {

	private Logger log = Logger.getLogger(getClass());

	private static final String ACTION_FORWARD_CONQUEST = "conquest";

	// 1x1 tracking pixel for 300x250 Adunit. P is the # of PFP results. B is
	// the # of backfill. S is the # of search results. H is the number of house
	// ads.
	private static final String TRACKING_1x1_NEARBY_300x250_3P_0B_0S_0H = "dart.track.300x250.3P-0B-0S-0H";
	private static final String TRACKING_1x1_NEARBY_300x250_2P_0B_0S_0H = "dart.track.300x250.2P-0B-0S-0H";
	private static final String TRACKING_1x1_NEARBY_300x250_1P_0B_0S_0H = "dart.track.300x250.1P-0B-0S-0H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_3B_0S_0H = "dart.track.300x250.0P-3B-0S-0H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_2B_1S_0H = "dart.track.300x250.0P-2B-1S-0H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_2B_0S_1H = "dart.track.300x250.0P-2B-0S-1H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_1B_2S_0H = "dart.track.300x250.0P-1B-2S-0H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_1B_1S_1H = "dart.track.300x250.0P-1B-1S-1H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_1B_0S_2H = "dart.track.300x250.0P-1B-0S-2H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_0B_3S_0H = "dart.track.300x250.0P-0B-3S-0H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_0B_2S_1H = "dart.track.300x250.0P-0B-2S-1H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_0B_1S_2H = "dart.track.300x250.0P-0B-1S-2H";
	private static final String TRACKING_1x1_NEARBY_300x250_0P_0B_0S_3H = "dart.track.300x250.0P-0B-0S-3H";

	private static final String TRACKING_1x1_NEARBY_645x100_2P_0B_0S_0H = "dart.track.645x100.0O-2P-0B-0S-0H";
	private static final String TRACKING_1x1_NEARBY_645x100_1P_1B_0S_0H = "dart.track.645x100.0O-1P-1B-0S-0H";
	private static final String TRACKING_1x1_NEARBY_645x100_1P_0B_1S_0H = "dart.track.645x100.0O-1P-0B-1S-0H";
	private static final String TRACKING_1x1_NEARBY_645x100_1P_0B_0S_1H = "dart.track.645x100.0O-1P-0B-0S-1H";
	private static final String TRACKING_1x1_NEARBY_645x100_0P_2B_0S_0H = "dart.track.645x100.0O-0P-2B-0S-0H";
	private static final String TRACKING_1x1_NEARBY_645x100_0P_1B_1S_0H = "dart.track.645x100.0O-0P-1B-1S-0H";
	private static final String TRACKING_1x1_NEARBY_645x100_0P_1B_0S_1H = "dart.track.645x100.0O-0P-1B-0S-1H";
	private static final String TRACKING_1x1_NEARBY_645x100_0P_0B_2S_0H = "dart.track.645x100.0O-0P-0B-2S-0H";
	private static final String TRACKING_1x1_NEARBY_645x100_0P_0B_1S_1H = "dart.track.645x100.0O-0P-0B-1S-1H";
	private static final String TRACKING_1x1_NEARBY_645x100_0P_0B_0S_2H = "dart.track.645x100.0O-0P-0B-0S-2H";

	private NearbyPlacesRequest nearbyPlacesRequest = new NearbyPlacesRequest();
	private NearbyPlacesResponse nearbyPlacesResponse;
	private String oneByOneTrackingUrl;
	// 1x1 tracking for the adunit that was actually requested.
	// Used only when the nearby is a backfill.
	// If backfill, render the nearby 1x1 tracking and the tracking for the
	// actual adunit that was requested. The way we can track the nearby
	// backfill impression for adunits other than nearby.
	private String oneByOneTrackingUrlForOriginal;
	private boolean backfill;

	public String getOneByOneTrackingUrlForOriginal() {
		return oneByOneTrackingUrlForOriginal;
	}

	public void setOneByOneTrackingUrlForOriginal(
			String oneByOneTrackingUrlForOriginal) {
		this.oneByOneTrackingUrlForOriginal = oneByOneTrackingUrlForOriginal;
	}

	public String getOneByOneTrackingUrl() {
		return oneByOneTrackingUrl;
	}

	public void setOneByOneTrackingUrl(String oneByOneTrackingUrl) {
		this.oneByOneTrackingUrl = oneByOneTrackingUrl;
	}

	public NearbyPlacesRequest getModel() {
		return nearbyPlacesRequest;
	}

	public NearbyPlacesRequest getNearbyPlacesRequest() {
		return nearbyPlacesRequest;
	}

	public void setNearbyPlacesRequest(NearbyPlacesRequest nearbyPlacesRequest) {
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
		log.info("Begin ProjectYellow.NearbyPlacesAction");

		Object requestAttrib = getHttpRequest().getAttribute(
				REQUEST_ATTRIBUTE_BACKFILL);
		backfill = (requestAttrib != null && requestAttrib instanceof Boolean) ? (Boolean) requestAttrib
				: false;
		if (backfill) {
			String adUnitSize = (String) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_ADUNIT_SIZE);
			Integer displaySize = (Integer) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE);
			String latitude = (String) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_LATITUDE);
			String longitude = (String) getHttpRequest().getAttribute(
					REQUEST_ATTRIBUTE_LONGITUDE);
			nearbyPlacesRequest.setAdUnitSize(adUnitSize);
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
		NearbyPlacesHelper helper = new NearbyPlacesHelper(
				getResourceRootPath());
		String adUnitSize = nearbyPlacesRequest.getAdUnitSize();
		nearbyPlacesRequest.setValidUrl(false);
		try {
			nearbyPlacesResponse = helper.getNearbyPlaces(nearbyPlacesRequest);
			set1x1TrackingPixel(nearbyPlacesRequest.getAdUnitSize());
			log.info("End NearbyPlacesAction");
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

		if (adUnitSize != null
				&& adUnitSize.equals(CommonConstants.CONQUEST_AD_SIZE))
			return ACTION_FORWARD_CONQUEST;
		else
			return Action.SUCCESS;
	}

	private void set1x1TrackingPixel(String adUnitSize)
			throws CitysearchException {
		String trackingUrlKey = null;
		int backfillSize = getBackfill().size();
		int searchResultsSize = getSearchResults().size();
		int houseAdsSize = getHouseAds().size();
		int pfpResultsSize = getNearbyPlaces().size();
		if (adUnitSize != null
				&& adUnitSize.equalsIgnoreCase(CommonConstants.MANTLE_AD_SIZE)) {
			trackingUrlKey = TRACKING_1x1_NEARBY_300x250_3P_0B_0S_0H;
			if (pfpResultsSize == 2 && backfillSize == 0
					&& searchResultsSize == 0 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_2P_0B_0S_0H;
			} else if (pfpResultsSize == 1 && backfillSize == 0
					&& searchResultsSize == 0 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_1P_0B_0S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 3
					&& searchResultsSize == 0 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_3B_0S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 2
					&& searchResultsSize == 1 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_2B_1S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 2
					&& searchResultsSize == 0 && houseAdsSize == 1) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_2B_0S_1H;
			} else if (pfpResultsSize == 0 && backfillSize == 1
					&& searchResultsSize == 2 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_1B_2S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 1
					&& searchResultsSize == 1 && houseAdsSize == 1) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_1B_1S_1H;
			} else if (pfpResultsSize == 0 && backfillSize == 1
					&& searchResultsSize == 0 && houseAdsSize == 2) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_1B_0S_2H;
			} else if (pfpResultsSize == 0 && backfillSize == 0
					&& searchResultsSize == 3 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_0B_3S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 0
					&& searchResultsSize == 2 && houseAdsSize == 1) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_0B_2S_1H;
			} else if (pfpResultsSize == 0 && backfillSize == 0
					&& searchResultsSize == 1 && houseAdsSize == 2) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_0B_1S_2H;
			} else if (pfpResultsSize == 0 && backfillSize == 0
					&& searchResultsSize == 0 && houseAdsSize == 3) {
				trackingUrlKey = TRACKING_1x1_NEARBY_300x250_0P_0B_0S_3H;
			}
		} else if (adUnitSize != null
				&& adUnitSize
						.equalsIgnoreCase(CommonConstants.CONQUEST_AD_SIZE)) {
			trackingUrlKey = TRACKING_1x1_NEARBY_645x100_2P_0B_0S_0H;

			if (pfpResultsSize == 1 && backfillSize == 1
					&& searchResultsSize == 0 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_1P_1B_0S_0H;
			} else if (pfpResultsSize == 1 && backfillSize == 0
					&& searchResultsSize == 1 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_1P_0B_1S_0H;
			} else if (pfpResultsSize == 1 && backfillSize == 0
					&& searchResultsSize == 0 && houseAdsSize == 1) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_1P_0B_0S_1H;
			} else if (pfpResultsSize == 0 && backfillSize == 2
					&& searchResultsSize == 0 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_0P_2B_0S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 1
					&& searchResultsSize == 1 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_0P_1B_1S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 1
					&& searchResultsSize == 0 && houseAdsSize == 1) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_0P_1B_0S_1H;
			} else if (pfpResultsSize == 0 && backfillSize == 0
					&& searchResultsSize == 2 && houseAdsSize == 0) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_0P_0B_2S_0H;
			} else if (pfpResultsSize == 0 && backfillSize == 0
					&& searchResultsSize == 1 && houseAdsSize == 1) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_0P_0B_1S_1H;
			} else if (pfpResultsSize == 0 && backfillSize == 0
					&& searchResultsSize == 0 && houseAdsSize == 2) {
				trackingUrlKey = TRACKING_1x1_NEARBY_645x100_0P_0B_0S_2H;
			}
		}
		Properties properties = PropertiesLoader.getAPIProperties();
		oneByOneTrackingUrl = properties.getProperty(trackingUrlKey);
		
		if (backfill)
		{
			//TODO set the tracking for the actual adunit.
		}
	}
}
