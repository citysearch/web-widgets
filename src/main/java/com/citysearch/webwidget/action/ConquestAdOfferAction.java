package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.OffersRequest;
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
    private static final String ACTION_FORWARD_CONQUEST = "conquest";
    private List<Offer> offers;
    private List<HouseAd> houseAds;

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
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
            offers = helper.getOffers(offersRequest);
            if (offers == null || offers.isEmpty()) {
                log.info("Returning backfill from offer");
                return "backfill";
            }
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 3);
        } catch (Exception e) {
            log.error(e.getMessage());
            houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 3);
        }
        log.info("End offersAction execute()");
        return Action.SUCCESS;
    }
}
