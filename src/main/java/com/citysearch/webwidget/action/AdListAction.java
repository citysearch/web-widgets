package com.citysearch.webwidget.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.citysearch.webwidget.bean.AdListBean;
import com.citysearch.webwidget.bean.AdListRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.AdListHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class AdListAction implements ModelDriven<AdListRequest>, ServletRequestAware,
        ServletResponseAware {
    private Logger log = Logger.getLogger(getClass());
    private AdListRequest adListRequest = new AdListRequest();
    private List<AdListBean> adList;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public AdListRequest getModel() {
        return adListRequest;
    }

    public AdListRequest getAdListRequest() {
        return adListRequest;
    }

    public void setAdListRequest(AdListRequest adListRequest) {
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
