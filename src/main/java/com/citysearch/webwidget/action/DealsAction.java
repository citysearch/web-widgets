package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.DealsResponse;
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
    private DealsResponse dealsResponse;
    private List<HouseAd> houseAds;

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }

    public DealsResponse getDealsResponse() {
        return dealsResponse;
    }

    public void setDealsResponse(DealsResponse dealsResponse) {
        this.dealsResponse = dealsResponse;
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
        // Return only one deal always
        dealsRequest.setDisplaySize(1);
        try {
            AbstractGrouponOffersFacade facade = GrouponOffersFacadeFactory.getFacade(
                    dealsRequest.getPublisher(), dealsRequest.getDisplaySize(),
                    getResourceRootPath());
            dealsResponse = facade.getDeals(dealsRequest);
            if (dealsResponse.getGrouponDeal() == null
                    && dealsResponse.getCitySearchOffer() == null) {
                log.info("Returning backfill from DealsAction");
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL, true);
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_SIZE,
                        CommonConstants.MANTLE_AD_SIZE);
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE,
                        CommonConstants.MANTLE_DISPLAY_SIZE);
                getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL_FOR,
                        CommonConstants.AD_UNIT_NAME_DEALS);
                return "backfill";
            }
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            houseAds = getHouseAds(dealsRequest.getDartClickTrackUrl(), 3);
            return "houseads";
        } catch (Exception e) {
            log.error(e.getMessage());
            houseAds = getHouseAds(dealsRequest.getDartClickTrackUrl(), 3);
            return "houseads";
        }
        log.info("End offersAction execute()");
        return Action.SUCCESS;
    }

}
