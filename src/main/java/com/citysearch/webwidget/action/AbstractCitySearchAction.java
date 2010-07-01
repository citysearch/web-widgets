package com.citysearch.webwidget.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.helper.HouseAdsHelper;

public class AbstractCitySearchAction implements ServletRequestAware, ServletResponseAware {
    public static final String REQUEST_ATTRIBUTE_BACKFILL = "backfill";
    public static final String REQUEST_ATTRIBUTE_ADUNIT_SIZE = "adUnitSize";
    public static final String REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE = "adUnitDisplaySize";
    public static final String REQUEST_ATTRIBUTE_LATITUDE = "latitude";
    public static final String REQUEST_ATTRIBUTE_LONGITUDE = "longitude";

    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }

    public String getResourceRootPath() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(httpRequest.getScheme());
        strBuilder.append("://");
        strBuilder.append(httpRequest.getServerName());
        strBuilder.append(":");
        strBuilder.append(httpRequest.getServerPort());
        strBuilder.append(httpRequest.getContextPath());
        return strBuilder.toString();
    }

    public List<HouseAd> getHouseAds(String dartTrackingUrl, int size) throws CitysearchException {
        List<HouseAd> houseAds = HouseAdsHelper.getHouseAds(getResourceRootPath(), dartTrackingUrl);
        houseAds = houseAds.subList(0, size);
        return houseAds;
    }
}
