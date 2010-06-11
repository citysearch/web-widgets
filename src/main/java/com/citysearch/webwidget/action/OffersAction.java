package com.citysearch.webwidget.action;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.Offers;
import com.citysearch.webwidget.bean.OffersRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.NearbyPlacesHelper;
import com.citysearch.webwidget.helper.OffersHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;


public class OffersAction extends AbstractCitySearchAction implements ModelDriven<OffersRequest> {
    private Logger log = Logger.getLogger(getClass());
    private OffersRequest offersRequest = new OffersRequest();
    private Offers offers;

    public String execute() {

        OffersHelper helper = new OffersHelper(getResourceRootPath());
//        try {
//
//
//
//        } catch (InvalidRequestParametersException ihre) {
//            log.error(ihre.getDetailedMessage());
//            throw ihre;
//        } catch (CitysearchException cse) {
//            log.error(cse.getMessage());
//            throw cse;
//        }

//        NearbyPlacesHelper helper = new NearbyPlacesHelper(getResourceRootPath());
//        try {
//            nearbyPlaces = helper.getNearbyPlaces(nearbyPlacesRequest);
//            if (nearbyPlaces != null && !nearbyPlaces.isEmpty()) {
//                for (NearbyPlace alb : nearbyPlaces) {
//                    alb.setCallBackFunction(nearbyPlacesRequest.getCallBackFunction());
//                    alb.setCallBackUrl(nearbyPlacesRequest.getCallBackUrl());
//                    String listingUrl = null;
//                    String callBackUrl = nearbyPlacesRequest.getCallBackUrl();
//                    if (callBackUrl != null && callBackUrl.trim().length() > 0) {
//                        listingUrl = callBackUrl.replace("$l", alb.getListingId());
//                        // Probably need to go to the properties file
//                        listingUrl = "http://ad.doubleclick.net/clk;225291110;48835962;h?"
//                                + listingUrl.replace("$p", alb.getPhone());
//                    } else {
//                        listingUrl = alb.getAdDisplayURL();
//                    }
//                    alb.setListingUrl(listingUrl);
//
//                    // Set the call back function JS function here.
//                    // Its messy to build the string in the JSP.
//                    String callBackFn = alb.getCallBackFunction();
//                    if (callBackFn != null && callBackFn.trim().length() > 0) {
//                        // Should produce javascript:fnName('param1','param2')
//                        StringBuilder strBuilder = new StringBuilder("javascript:");
//                        strBuilder.append(callBackFn);
//                        strBuilder.append("('");
//                        strBuilder.append(alb.getListingId());
//                        strBuilder.append("','");
//                        strBuilder.append(alb.getPhone());
//                        strBuilder.append("')");
//
//                        alb.setCallBackFunction(strBuilder.toString());
//                    }
//                }
//            }
//        } catch (InvalidRequestParametersException ihre) {
//            log.error(ihre.getDetailedMessage());
//            throw ihre;
//        } catch (CitysearchException cse) {
//            log.error(cse.getMessage());
//            throw cse;
//        }

        return Action.SUCCESS;
    }

    public OffersRequest getReviewRequest() {
        return offersRequest;
    }

    public void setOffersRequest(OffersRequest offersRequest) {
        this.offersRequest = offersRequest;
    }

    public Offers getOffers() {
        return offers;
    }

    public void setOffers(Offers offers) {
        this.offers = offers;
    }

    public OffersRequest getModel() {
        return offersRequest;
    }
}
