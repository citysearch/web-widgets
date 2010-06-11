package com.citysearch.webwidget.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public class AbstractCitySearchAction implements ServletRequestAware, ServletResponseAware {
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
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
}
