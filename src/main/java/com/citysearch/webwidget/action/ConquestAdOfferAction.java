package com.citysearch.webwidget.action;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.ConquestAdOffer;
import com.citysearch.webwidget.bean.ConquestAdOfferRequest;
import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.ConquestAdOfferHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class ConquestAdOfferAction extends AbstractCitySearchAction implements
        ModelDriven<ConquestAdOfferRequest> {

    private Logger log = Logger.getLogger(getClass());
    private ConquestAdOfferRequest offersRequest = new ConquestAdOfferRequest();
    private List<ConquestAdOffer> offersList;
    private List<HouseAd> houseAds;

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }

    public List<ConquestAdOffer> getOffersList() {
        return offersList;
    }

    public void setOffersList(List<ConquestAdOffer> offersList) {
        this.offersList = offersList;
    }

    public ConquestAdOfferRequest getOffersRequest() {
        return offersRequest;
    }

    public void setOffersRequest(ConquestAdOfferRequest offersRequest) {
        this.offersRequest = offersRequest;
    }

    public ConquestAdOfferRequest getModel() {
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
        log.info("=========Start offersAction execute()============================ >");
        ConquestAdOfferHelper helper = new ConquestAdOfferHelper(getResourceRootPath());
        try {
            //TODO: should use the offer helper.
            offersList = helper.getOffers(offersRequest);
            if (offersList == null || offersList.isEmpty()) {
                log.info("Returning backfill from offer");
                return "backfill";
            }
            Iterator<ConquestAdOffer> it = offersList.iterator();
            while (it.hasNext()) {
                ConquestAdOffer offer = (ConquestAdOffer) it.next();
                String listingUrl = null;
                if (offer.getProfileUrl() != null) {
                    /*
                     * listingUrl = getTrackingUrl(offer.getProfileUrl(), CLICK_TRACKING_URL,
                     * offer.getListingId(), offersRequest.getPublisher(),
                     * offersRequest.getAdUnitName(),offersRequest.getAdUnitSize());
                     */
                } else {
                    listingUrl = "";
                }
                offer.setProfileUrl(listingUrl);
            }
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 2);
        } catch (CitysearchException cse) {
            log.error(cse.getMessage());
            houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 2);
        }
        log.info("=========End offersAction execute()============================ >");
        return Action.SUCCESS;
    }
}
