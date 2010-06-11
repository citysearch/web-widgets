package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.HouseAdsHelper;
import com.citysearch.webwidget.helper.NearbyPlacesHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class NearbyPlacesAction extends AbstractCitySearchAction implements
        ModelDriven<NearbyPlacesRequest> {
    private Logger log = Logger.getLogger(getClass());
    private NearbyPlacesRequest nearbyPlacesRequest = new NearbyPlacesRequest();
    private List<NearbyPlace> nearbyPlaces;
    private List<HouseAd> houseAds;

    public NearbyPlacesRequest getModel() {
        return nearbyPlacesRequest;
    }

    public NearbyPlacesRequest getNearbyPlacesRequest() {
        return nearbyPlacesRequest;
    }

    public void setNearbyPlacesRequest(NearbyPlacesRequest nearbyPlacesRequest) {
        this.nearbyPlacesRequest = nearbyPlacesRequest;
    }

    public List<NearbyPlace> getNearbyPlaces() {
        return nearbyPlaces;
    }

    public void setNearbyPlaces(List<NearbyPlace> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
    }

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }

    public String execute() throws CitysearchException {

        log.info("Begin NearbyPlacesAction");
        NearbyPlacesHelper helper = new NearbyPlacesHelper(getResourceRootPath());

        try {
            nearbyPlaces = helper.getNearbyPlaces(nearbyPlacesRequest);
            //nearbyPlaces = null; //TODO: REMOVE!!!
            if (nearbyPlaces != null && !nearbyPlaces.isEmpty()) {
                for (NearbyPlace alb : nearbyPlaces) {
                    alb.setCallBackFunction(nearbyPlacesRequest.getCallBackFunction());
                    alb.setCallBackUrl(nearbyPlacesRequest.getCallBackUrl());
                    String listingUrl = null;
                    String callBackUrl = nearbyPlacesRequest.getCallBackUrl();
                    if (callBackUrl != null && callBackUrl.trim().length() > 0) {
                        listingUrl = callBackUrl.replace("$l", alb.getListingId());
                        // Probably need to go to the properties file
                        listingUrl = "http://ad.doubleclick.net/clk;225291110;48835962;h?"
                                + listingUrl.replace("$p", alb.getPhone());
                    } else {
                        listingUrl = alb.getAdDisplayURL();
                    }
                    alb.setListingUrl(listingUrl);

                    // Set the call back function JS function here.
                    // Its messy to build the string in the JSP.
                    String callBackFn = alb.getCallBackFunction();
                    if (callBackFn != null && callBackFn.trim().length() > 0) {
                        // Should produce javascript:fnName('param1','param2')
                        StringBuilder strBuilder = new StringBuilder("javascript:");
                        strBuilder.append(callBackFn);
                        strBuilder.append("('");
                        strBuilder.append(alb.getListingId());
                        strBuilder.append("','");
                        strBuilder.append(alb.getPhone());
                        strBuilder.append("')");

                        alb.setCallBackFunction(strBuilder.toString());
                    }
                }
            } else {
                nearbyPlaces = helper.getNearbyPlacesBackfill();
                if (nearbyPlaces == null || nearbyPlaces.isEmpty())
                {
                    houseAds = HouseAdsHelper.getHouseAds();
                    return "houseads";
                }
                return "backfill";
            }
            log.info("End NearbyPlacesAction");
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            throw ihre;
        } catch (CitysearchException cse) {
            log.error(cse.getMessage());
            throw cse;
        }
        return Action.SUCCESS;
    }
}
