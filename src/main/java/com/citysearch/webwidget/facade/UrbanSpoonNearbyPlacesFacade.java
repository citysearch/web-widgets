package com.citysearch.webwidget.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.HouseAdsUtil;

public class UrbanSpoonNearbyPlacesFacade extends AbstractNearByPlacesFacade {
    private Logger log = Logger.getLogger(getClass());

    protected UrbanSpoonNearbyPlacesFacade(String contextPath, int displaySize) {
        super(contextPath, displaySize);
    }

    public NearbyPlacesResponse getNearbyPlaces(RequestBean request)
            throws InvalidRequestParametersException, CitysearchException {
        request.validate();
        return findInsiderPagesNearbyPlaces(request);
    }

    private NearbyPlacesResponse findInsiderPagesNearbyPlaces(RequestBean request)
            throws CitysearchException {
        NearbyPlacesResponse response = new NearbyPlacesResponse();

        List<NearbyPlace> nearbyPlaces = null;

        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())) {
            nearbyPlaces = getAdsFromPFPLocation(request);
        }
        List<NearbyPlace> backfill = null;
        List<HouseAd> houseAds = null;
        List<NearbyPlace> searchResults = null;
        int noOfAdsNeeded = (nearbyPlaces == null || nearbyPlaces.isEmpty()) ? this.displaySize
                : this.displaySize - nearbyPlaces.size();
        if (noOfAdsNeeded > 0) {
            nearbyPlaces = (nearbyPlaces == null) ? new ArrayList<NearbyPlace>() : nearbyPlaces;
            if (!StringUtils.isBlank(request.getWhere())) {
                Set<String> listingIds = new TreeSet<String>();
                for (NearbyPlace nbp : nearbyPlaces)
                    listingIds.add(nbp.getListingId());
                List<NearbyPlace> pfpResults = getAdsFromPFP(request, listingIds, noOfAdsNeeded);
                if (pfpResults != null && !pfpResults.isEmpty()) {
                    pfpResults = (pfpResults.size() > noOfAdsNeeded) ? pfpResults.subList(0,
                            noOfAdsNeeded) : pfpResults;
                    nearbyPlaces.addAll(pfpResults);
                }
            }
            int noOfBackFillNeeded = (nearbyPlaces == null || nearbyPlaces.isEmpty()) ? this.displaySize
                    : this.displaySize - nearbyPlaces.size();
            if (noOfBackFillNeeded > 0) {
                backfill = getNearbyPlacesBackfill(request, noOfBackFillNeeded);
                int noOfSearchResultsNeeded = (backfill == null || backfill.isEmpty()) ? noOfBackFillNeeded
                        : noOfBackFillNeeded - backfill.size();
                if (noOfSearchResultsNeeded > 0) {
                    searchResults = getSearchResults(request, noOfSearchResultsNeeded);
                    int noOfHouseAdsNeeded = (searchResults == null || searchResults.isEmpty()) ? noOfSearchResultsNeeded
                            : noOfSearchResultsNeeded - searchResults.size();
                    if (noOfHouseAdsNeeded > 0) {
                        houseAds = HouseAdsUtil.getHouseAds(getContextPath(),
                                request.getDartClickTrackUrl());
                        houseAds = houseAds.subList(0, noOfHouseAdsNeeded);
                    } else if (noOfHouseAdsNeeded < 0) {
                        searchResults = searchResults.subList(0, noOfSearchResultsNeeded);
                    }
                } else if (noOfSearchResultsNeeded < 0) {
                    backfill = backfill.subList(0, noOfBackFillNeeded);
                }
            }
        }
        response.setNearbyPlaces(nearbyPlaces);
        response.setBackfill(backfill);
        response.setSearchResults(searchResults);
        response.setHouseAds(houseAds);
        return response;
    }

}
