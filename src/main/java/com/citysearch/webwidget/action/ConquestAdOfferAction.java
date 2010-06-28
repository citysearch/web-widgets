package com.citysearch.webwidget.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.OffersRequest;
import com.citysearch.webwidget.bean.OffersResponse;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.OffersHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class ConquestAdOfferAction extends AbstractCitySearchAction implements
        ModelDriven<OffersRequest> {

    private Logger log = Logger.getLogger(getClass());
    private OffersRequest offersRequest = new OffersRequest();
    private static final Integer DEFAULT_DISPLAY_SIZE = 1;
    private static final String AD_UNIT_NAME = "conquestAd";
    private OffersResponse offersResponse;
    private static final String ACTION_FORWARD_CONQUEST = "conquest";

    public List<Offer> getOffers() {
        if (offersResponse == null || offersResponse.getOffers() == null) {
            return new ArrayList<Offer>();
        }
        return offersResponse.getOffers();
    }

    public List<NearbyPlace> getBackfill() {
        if (offersResponse == null || offersResponse.getBackfill() == null) {
            return new ArrayList<NearbyPlace>();
        }
        return offersResponse.getBackfill();
    }

    public List<HouseAd> getHouseAds() {
        if (offersResponse == null || offersResponse.getHouseAds() == null) {
            return new ArrayList<HouseAd>();
        }
        return offersResponse.getHouseAds();
    }

    public OffersResponse getOffersResponse() {
        return offersResponse;
    }

    public void setOffersResponse(OffersResponse offersResponse) {
        this.offersResponse = offersResponse;
    }

    public OffersRequest getOffersRequest() {
        return offersRequest;
    }

    public void setOffersRequest(OffersRequest offersRequest) {
        this.offersRequest = offersRequest;
    }

    public OffersRequest getModel() {
        return offersRequest;
    }

    /**
     * Calls the getoffers() method from offersHelper class to get the offers, review count is
     * fetched for each offer by passing its listing id to profile API
     * 
     * @return String
     * @throws CitysearchException
     */
    public String execute() throws CitysearchException {
        log.info("Start offersAction execute()");
        if (offersRequest.getDisplaySize() == null || offersRequest.getDisplaySize() == 0) {
            offersRequest.setDisplaySize(DEFAULT_DISPLAY_SIZE);
        }
        if (offersRequest.getAdUnitName() == null
                || offersRequest.getAdUnitName().trim().length() == 0) {
            offersRequest.setAdUnitName(AD_UNIT_NAME);
        }
        OffersHelper helper = new OffersHelper(getResourceRootPath());
        try {
            offersResponse = helper.getOffers(offersRequest);
            if (offersResponse == null || offersResponse.getOffers().isEmpty()) {              
                OffersHelper offersHelper = new OffersHelper(getResourceRootPath());
                List nearByPlaces = offersHelper.getNearByPlaces(offersRequest,
                        getResourceRootPath());
                nearByPlaces = nearByPlaces.subList(0, DEFAULT_DISPLAY_SIZE);  
                offersResponse = new OffersResponse();
                offersResponse.setBackfill(nearByPlaces);      
                return ACTION_FORWARD_CONQUEST;
               // return "backfill";
            }
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            offersResponse = new OffersResponse();
            offersResponse.setHouseAds(getHouseAds(offersRequest.getDartClickTrackUrl(),
                    offersRequest.getDisplaySize()));
        } catch (Exception e) {
            log.error(e.getMessage());
            offersResponse = new OffersResponse();
            offersResponse.setHouseAds(getHouseAds(offersRequest.getDartClickTrackUrl(),
                    offersRequest.getDisplaySize()));
        }
        log.info("End offersAction execute()");
        return Action.SUCCESS;
    }
}
