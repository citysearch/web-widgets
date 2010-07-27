package com.citysearch.webwidget.action.project.yellow;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.action.AbstractCitySearchAction;
import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.NearbyPlacesHelper;
import com.citysearch.webwidget.util.CommonConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class NearbyPlacesAction extends AbstractCitySearchAction implements
        ModelDriven<NearbyPlacesRequest> {
    private Logger log = Logger.getLogger(getClass());

    private static final Integer DEFAULT_DISPLAY_SIZE = 2;
    private static final String ADUNIT_SIZE = "600x83";
    private NearbyPlacesRequest nearbyPlacesRequest = new NearbyPlacesRequest();
    private NearbyPlacesResponse nearbyPlacesResponse;

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
        if (nearbyPlacesResponse == null || nearbyPlacesResponse.getSearchResults() == null) {
            return new ArrayList<NearbyPlace>();
        }
        return nearbyPlacesResponse.getSearchResults();
    }

    public List<NearbyPlace> getNearbyPlaces() {
        if (nearbyPlacesResponse == null || nearbyPlacesResponse.getNearbyPlaces() == null) {
            return new ArrayList<NearbyPlace>();
        }
        return nearbyPlacesResponse.getNearbyPlaces();
    }

    public List<NearbyPlace> getBackfill() {
        if (nearbyPlacesResponse == null || nearbyPlacesResponse.getBackfill() == null) {
            return new ArrayList<NearbyPlace>();
        }
        return nearbyPlacesResponse.getBackfill();
    }

    public List<HouseAd> getHouseAds() {
        if (nearbyPlacesResponse == null || nearbyPlacesResponse.getHouseAds() == null) {
            return new ArrayList<HouseAd>();
        }
        return nearbyPlacesResponse.getHouseAds();
    }

    public String execute() throws CitysearchException {
        log.info("Begin Project yellow NearbyPlacesAction");
        
        //Publisher is Yelp for project yellow always. 
        nearbyPlacesRequest.setPublisher(CommonConstants.PUBLISHER_PROJECT_YELLOW);
        
        Object requestAttrib = getHttpRequest().getAttribute(REQUEST_ATTRIBUTE_BACKFILL);
        boolean backfill = (requestAttrib != null && requestAttrib instanceof Boolean) ? (Boolean) requestAttrib
                : false;
        if (backfill) {
            String latitude = (String) getHttpRequest().getAttribute(REQUEST_ATTRIBUTE_LATITUDE);
            String longitude = (String) getHttpRequest().getAttribute(REQUEST_ATTRIBUTE_LONGITUDE);
            nearbyPlacesRequest.setLatitude(latitude);
            nearbyPlacesRequest.setLongitude(longitude);
        }

        nearbyPlacesRequest.setDisplaySize(DEFAULT_DISPLAY_SIZE);
        nearbyPlacesRequest.setAdUnitSize(ADUNIT_SIZE);

        NearbyPlacesHelper helper = new NearbyPlacesHelper(getResourceRootPath());
        //Important for project yellow
        nearbyPlacesRequest.setValidUrl(true);
        try {
            nearbyPlacesResponse = helper.getNearbyPlaces(nearbyPlacesRequest);
            log.info("End NearbyPlacesAction");
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            nearbyPlacesResponse = new NearbyPlacesResponse();
            nearbyPlacesResponse.setHouseAds(getHouseAds(
                    nearbyPlacesRequest.getDartClickTrackUrl(),
                    nearbyPlacesRequest.getDisplaySize()));
        } catch (Exception e) {
            log.error(e.getMessage());
            StackTraceElement[] elms = e.getStackTrace();
            for (int k = 0; k < elms.length; k++) {
                log.error(elms[k]);
            }
            nearbyPlacesResponse = new NearbyPlacesResponse();
            nearbyPlacesResponse.setHouseAds(getHouseAds(
                    nearbyPlacesRequest.getDartClickTrackUrl(),
                    nearbyPlacesRequest.getDisplaySize()));
        }

        return Action.SUCCESS;
    }
}
