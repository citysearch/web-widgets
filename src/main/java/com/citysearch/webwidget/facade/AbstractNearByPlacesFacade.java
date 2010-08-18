package com.citysearch.webwidget.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.PFPAd;
import com.citysearch.webwidget.api.bean.PFPResponse;
import com.citysearch.webwidget.api.proxy.PFPProxy;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.helper.NearbyPlacesHelper;
import com.citysearch.webwidget.facade.helper.SearchHelper;

public abstract class AbstractNearByPlacesFacade {
	private Logger log = Logger.getLogger(getClass());
	protected String contextPath;
	protected int displaySize;
	protected PFPProxy pfpProxy;

	protected AbstractNearByPlacesFacade(String contextPath, int displaySize) {
		this.contextPath = contextPath;
		this.displaySize = displaySize;
		this.pfpProxy = new PFPProxy();
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public int getDisplaySize() {
		return displaySize;
	}

	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}

	public abstract NearbyPlacesResponse getNearbyPlaces(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException;

	private List<NearbyPlace> toNearbyPlaces(RequestBean request,
			List<PFPAd> ads) throws CitysearchException {
		List<NearbyPlace> nearbyPlaces = null;
		if (ads != null && !ads.isEmpty()) {
			nearbyPlaces = new ArrayList<NearbyPlace>();
			for (PFPAd ad : ads) {
				nearbyPlaces.add(NearbyPlacesHelper.toNearbyPlace(request, ad));
			}
			NearbyPlacesHelper.addDefaultImages(nearbyPlaces, contextPath);
		}
		return nearbyPlaces;
	}

	private List<NearbyPlace> toNearbyPlacesBackfill(RequestBean request,
			List<PFPAd> ads) throws CitysearchException {
		List<NearbyPlace> nearbyPlaces = null;
		if (ads != null && !ads.isEmpty()) {
			nearbyPlaces = new ArrayList<NearbyPlace>();
			for (PFPAd ad : ads) {
				nearbyPlaces.add(NearbyPlacesHelper.toBackfill(request, ad));
			}
			NearbyPlacesHelper.addDefaultImages(nearbyPlaces, contextPath);
		}
		return nearbyPlaces;
	}

	protected List<NearbyPlace> getAdsFromPFPLocation(RequestBean request)
			throws CitysearchException {
		log.info("NearbyPlacesHelper.getAdsFromPFPLocation: Begin");
		PFPResponse pfpLocationResponse = pfpProxy.getAdsFromPFPLocation(
				request, displaySize);
		return toNearbyPlaces(request, pfpLocationResponse.getLocalPfp());
	}

	protected List<NearbyPlace> getAdsFromPFP(RequestBean request,
			Set<String> listingsToIgnore, int requiredNumberOfAds)
			throws CitysearchException {
		log.info("getAdsFromPFP: Begin");
		PFPResponse pfpResponse = pfpProxy.getAdsFromPFP(request,
				requiredNumberOfAds, listingsToIgnore);
		return toNearbyPlaces(request, pfpResponse.getLocalPfp());
	}

	protected List<NearbyPlace> getNearbyPlacesBackfill(RequestBean request,
			int noOfBackFillNeeded) throws CitysearchException {
		PFPResponse response = pfpProxy.getBackFill(noOfBackFillNeeded);
		List<NearbyPlace> backFillFromPFP = toNearbyPlacesBackfill(request,
				response.getBackfill());
		return backFillFromPFP;
	}

	protected List<NearbyPlace> getSearchResults(RequestBean request,
			int maxNoOfResultsRequired) throws CitysearchException {
		SearchHelper searchHelper = new SearchHelper(contextPath,
				maxNoOfResultsRequired);
		return searchHelper.getNearbyPlaces(request);
	}
}
