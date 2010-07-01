package com.citysearch.webwidget.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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

    private static final String ACTION_FORWARD_CONQUEST = "conquest";

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
        log.info("Begin NearbyPlacesAction");

        Object requestAttrib = getHttpRequest().getAttribute(REQUEST_ATTRIBUTE_BACKFILL);
        boolean backfill = (requestAttrib != null && requestAttrib instanceof Boolean) ? (Boolean) requestAttrib
                : false;
        if (backfill) {
            String adUnitSize = (String) getHttpRequest().getAttribute(
                    REQUEST_ATTRIBUTE_ADUNIT_SIZE);
            Integer displaySize = (Integer) getHttpRequest().getAttribute(
                    REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE);
            String latitude = (String) getHttpRequest().getAttribute(REQUEST_ATTRIBUTE_LATITUDE);
            String longitude = (String) getHttpRequest().getAttribute(REQUEST_ATTRIBUTE_LONGITUDE);
            nearbyPlacesRequest.setAdUnitSize(adUnitSize);
            nearbyPlacesRequest.setDisplaySize(displaySize);
            nearbyPlacesRequest.setLatitude(latitude);
            nearbyPlacesRequest.setLongitude(longitude);
        }

        if (nearbyPlacesRequest.getDisplaySize() == null) {
            nearbyPlacesRequest.setDisplaySize(CommonConstants.DEFAULT_NEARBY_DISPLAY_SIZE);
        }
        if (nearbyPlacesRequest.getAdUnitSize() == null) {
            nearbyPlacesRequest.setAdUnitSize(CommonConstants.MANTLE_AD_SIZE);
        }
        NearbyPlacesHelper helper = new NearbyPlacesHelper(getResourceRootPath());
        String adUnitSize = nearbyPlacesRequest.getAdUnitSize();

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
            // On any exception, want the house ads to be returned.
            // Idea is to not return a blank widget.
            log.error(e.getMessage());
            nearbyPlacesResponse = new NearbyPlacesResponse();
            nearbyPlacesResponse.setHouseAds(getHouseAds(
                    nearbyPlacesRequest.getDartClickTrackUrl(),
                    nearbyPlacesRequest.getDisplaySize()));
        }

        if (adUnitSize != null && adUnitSize.equals(CommonConstants.CONQUEST_AD_SIZE))
            return ACTION_FORWARD_CONQUEST;
        else
            return Action.SUCCESS;
    }
}
