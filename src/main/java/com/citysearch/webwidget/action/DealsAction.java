package com.citysearch.webwidget.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.GrouponDeal;
import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.AbstractGrouponOffersFacade;
import com.citysearch.webwidget.facade.GrouponOffersFacadeFactory;
import com.citysearch.webwidget.util.CommonConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class DealsAction extends AbstractCitySearchAction implements ModelDriven<RequestBean> {
    private Logger log = Logger.getLogger(getClass());

    private RequestBean dealsRequest = new RequestBean();
    private List<GrouponDeal> deals;
    private List<HouseAd> houseAds;

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }

    public List<GrouponDeal> getDeals() {
        if (deals == null) {
            return new ArrayList<GrouponDeal>();
        }
        return deals;
    }

    public void setDeals(List<GrouponDeal> deals) {
        this.deals = deals;
    }

    public RequestBean getModel() {
        return dealsRequest;
    }

    public RequestBean getDealsRequest() {
        return dealsRequest;
    }

    public void setDealsRequest(RequestBean dealsRequest) {
        this.dealsRequest = dealsRequest;
    }

    public String execute() throws CitysearchException {
        try {
            AbstractGrouponOffersFacade facade = GrouponOffersFacadeFactory.getFacade(
                    dealsRequest.getPublisher(), dealsRequest.getDisplaySize(),
                    getResourceRootPath());
            deals = facade.getDeals(dealsRequest);

            if (deals == null || deals.isEmpty()) {
                log.info("Returning backfill from Deals");
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL, true);
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_SIZE,
                        dealsRequest.getAdUnitSize());
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE,
                        dealsRequest.getDisplaySize());
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LATITUDE,
                        dealsRequest.getLatitude());
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LONGITUDE,
                        dealsRequest.getLongitude());
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL_FOR,
                        CommonConstants.AD_UNIT_NAME_DEALS);
                return "backfill";
            }
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            houseAds = getHouseAds(dealsRequest.getDartClickTrackUrl(), 3);
        } catch (Exception e) {
            log.error(e.getMessage());
            houseAds = getHouseAds(dealsRequest.getDartClickTrackUrl(), 3);
        }
        log.info("End offersAction execute()");
        return Action.SUCCESS;
    }

}
