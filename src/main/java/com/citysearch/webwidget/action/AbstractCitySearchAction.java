package com.citysearch.webwidget.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.HouseAdsUtil;

public class AbstractCitySearchAction implements ServletRequestAware,
		ServletResponseAware {
	public static final String REQUEST_ATTRIBUTE_BACKFILL = "backfill";
	public static final String REQUEST_ATTRIBUTE_ADUNIT_SIZE = "adUnitSize";
	public static final String REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE = "adUnitDisplaySize";
	public static final String REQUEST_ATTRIBUTE_LATITUDE = "latitude";
	public static final String REQUEST_ATTRIBUTE_LONGITUDE = "longitude";
	public static final String REQUEST_ATTRIBUTE_BACKFILL_FOR = "backfillFor";

	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private String oneByOneTrackingUrl;
	// 1x1 tracking for the adunit that was actually requested.
	// Used only when the nearby is a backfill.
	// If backfill, render the nearby 1x1 tracking and the tracking for the
	// actual adunit that was requested. The way we can track the nearby
	// backfill impression for adunits other than nearby.
	private String oneByOneTrackingUrlForOriginal;

	public String getOneByOneTrackingUrl() {
		return oneByOneTrackingUrl;
	}

	public void setOneByOneTrackingUrl(String oneByOneTrackingUrl) {
		this.oneByOneTrackingUrl = oneByOneTrackingUrl;
	}

	public String getOneByOneTrackingUrlForOriginal() {
		return oneByOneTrackingUrlForOriginal;
	}

	public void setOneByOneTrackingUrlForOriginal(
			String oneByOneTrackingUrlForOriginal) {
		this.oneByOneTrackingUrlForOriginal = oneByOneTrackingUrlForOriginal;
	}

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

	public List<HouseAd> getHouseAds(String dartTrackingUrl, int size)
			throws CitysearchException {
		List<HouseAd> houseAds = HouseAdsUtil.getHouseAds(
				getResourceRootPath(), dartTrackingUrl);
		houseAds = houseAds.subList(0, size);
		return houseAds;
	}
}
