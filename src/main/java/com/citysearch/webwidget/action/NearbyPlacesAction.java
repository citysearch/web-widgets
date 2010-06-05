package com.citysearch.webwidget.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.NearbyPlacesHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class NearbyPlacesAction implements ModelDriven<NearbyPlacesRequest>, ServletRequestAware,
        ServletResponseAware {
    private Logger log = Logger.getLogger(getClass());
    private NearbyPlacesRequest nearbyPlacesRequest = new NearbyPlacesRequest();
    private List<NearbyPlace> nearbyPlaces;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

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

    public String execute() throws CitysearchException {

        NearbyPlacesHelper helper = new NearbyPlacesHelper();
        try {
            nearbyPlaces = helper.getNearbyPlaces(nearbyPlacesRequest);
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
            }
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
