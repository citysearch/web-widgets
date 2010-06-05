package com.citysearch.webwidget.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.citysearch.webwidget.bean.AdListBean;
import com.citysearch.webwidget.bean.NearbyPlacesRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.AdListHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class NearbyPlacesAction implements ModelDriven<NearbyPlacesRequest>, ServletRequestAware,
        ServletResponseAware {
    private Logger log = Logger.getLogger(getClass());
    private NearbyPlacesRequest adListRequest = new NearbyPlacesRequest();
    private List<AdListBean> adList;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public NearbyPlacesRequest getModel() {
        return adListRequest;
    }

    public NearbyPlacesRequest getAdListRequest() {
        return adListRequest;
    }

    public void setAdListRequest(NearbyPlacesRequest adListRequest) {
        this.adListRequest = adListRequest;
    }

    public List<AdListBean> getAdList() {
        return adList;
    }

    public void setAdList(List<AdListBean> adList) {
        this.adList = adList;
    }

    public String execute() throws CitysearchException {

        AdListHelper helper = new AdListHelper();
        try {
            adList = helper.getAdList(adListRequest);
            if (adList != null && !adList.isEmpty()) {
                for (AdListBean alb : adList) {
                    alb.setCallBackFunction(adListRequest.getCallBackFunction());
                    alb.setCallBackUrl(adListRequest.getCallBackUrl());
                    String listingUrl = null;
                    String callBackUrl = adListRequest.getCallBackUrl();
                    if (callBackUrl != null && callBackUrl.trim().length() > 0) {
                        listingUrl = callBackUrl.replace("$l", alb.getListingId());
                        //Probably need to go to the properties file
                        listingUrl = "http://ad.doubleclick.net/clk;225291110;48835962;h?"
                                + listingUrl.replace("$p", alb.getPhone());
                    } else {
                        listingUrl = alb.getAdDisplayURL();
                    }
                    alb.setListingUrl(listingUrl);
                    
                    //Set the call back function JS function here.
                    //Its messy to build the string in the JSP.
                    String callBackFn = alb.getCallBackFunction();
                    if (callBackFn != null && callBackFn.trim().length() > 0)
                    {
                        //Should produce javascript:fnName('param1','param2')
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
