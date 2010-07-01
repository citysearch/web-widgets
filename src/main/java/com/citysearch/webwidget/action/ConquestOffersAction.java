package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.OffersRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.OffersHelper;
import com.citysearch.webwidget.util.CommonConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class ConquestOffersAction extends AbstractCitySearchAction implements
        ModelDriven<OffersRequest> {
    private static final Integer MAX_OFFER_DESCRIPTION_SIZE = 125;
    private static final Integer MAX_OFFER_TITLE_SIZE = 90;
    private static final Integer MAX_OFFER_LISTING_NAME_SIZE = 30;
    
    private Logger log = Logger.getLogger(getClass());
    private OffersRequest offersRequest = new OffersRequest();
    private Offer offer;
    private List<HouseAd> houseAds;

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
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
            offersRequest.setDisplaySize(1);
        }
        if (offersRequest.getAdUnitName() == null
                || offersRequest.getAdUnitName().trim().length() == 0) {
            offersRequest.setAdUnitName(CommonConstants.AD_UNIT_NAME_OFFERS);
        }
        OffersHelper helper = new OffersHelper(getResourceRootPath(),
                offersRequest.getDisplaySize());
        try {
            List<Offer> offers = helper.getOffers(offersRequest);
            if (offers == null || offers.isEmpty()) {
                log.info("Returning backfill from offer");
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL, true);
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_SIZE,
                        CommonConstants.CONQUEST_AD_SIZE);
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE,
                        CommonConstants.CONQUEST_DISPLAY_SIZE);
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LATITUDE,
                        offersRequest.getLatitude());
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LONGITUDE,
                        offersRequest.getLongitude());
                return "backfill";
            } else {
                // Format bigger text for display
                offer = offers.get(0);
                String offerDescription = offer.getOfferDescription();
                if (offerDescription != null
                        && offerDescription.trim().length() > MAX_OFFER_DESCRIPTION_SIZE) {
                    offerDescription = StringUtils.abbreviate(offerDescription,
                            MAX_OFFER_DESCRIPTION_SIZE);
                }
                offer.setOfferDescription(offerDescription);

                String offerTitle = offer.getOfferTitle();
                if (offerTitle != null && offerTitle.trim().length() > MAX_OFFER_TITLE_SIZE) {
                    offerTitle = StringUtils.abbreviate(offerTitle, MAX_OFFER_TITLE_SIZE);
                }
                offer.setOfferTitle(offerTitle);
                
                String listingName = offer.getListingName();
                if (listingName != null && listingName.trim().length() > MAX_OFFER_LISTING_NAME_SIZE) {
                    listingName = StringUtils.abbreviate(listingName, MAX_OFFER_LISTING_NAME_SIZE);
                }
                offer.setListingName(listingName);
            }
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 2);
        } catch (Exception e) {
            log.error(e.getMessage());
            houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 2);
        }
        log.info("End offersAction execute()");
        return Action.SUCCESS;
    }
}
