package com.citysearch.webwidget.facade;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.GrouponResponse;
import com.citysearch.webwidget.api.proxy.GrouponProxy;
import com.citysearch.webwidget.bean.DealsResponse;
import com.citysearch.webwidget.bean.GrouponDeal;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;

public class UrbanSpoonFacade extends AbstractGrouponOffersFacade {
    private Logger log = Logger.getLogger(getClass());

    protected UrbanSpoonFacade(String contextPath, int displaySize) {
        super(contextPath, displaySize);
    }

    public DealsResponse getDeals(RequestBean request) throws InvalidRequestParametersException,
            CitysearchException {
        validate(request);
        GrouponProxy proxy = new GrouponProxy();
        List<GrouponResponse> dealsFromGroupon = proxy.getOffers(request, displaySize);
        DealsResponse dealsResponse = new DealsResponse();
        if (dealsFromGroupon != null && !dealsFromGroupon.isEmpty()) {
            GrouponResponse response = dealsFromGroupon.get(0);
            GrouponDeal deal = toGrouponDeal(request, response);
            if (deal != null) {
                dealsResponse.setGrouponDeal(deal);
                // Reset the what so that the ads are relevant
                request.setWhat(deal.getVendorName());
                // If no groupon deal and no city search offer, then we should be returning the
                // nearby
                // ad-unit. No logic requried here.
                // If either groupon on cs offer is found, then return two nearby places.
                AbstractNearByPlacesFacade facade = new UrbanSpoonNearbyPlacesFacade(contextPath, 2);
                NearbyPlacesResponse nearbyResponse = facade.getNearbyPlaces(request);
                List<NearbyPlace> places = new ArrayList<NearbyPlace>();
                dealsResponse.setPlaces(nearbyResponse.getNearbyPlaces());
                dealsResponse.setSearchResults(nearbyResponse.getSearchResults());
                dealsResponse.setBackfill(nearbyResponse.getBackfill());
                dealsResponse.setHouseAds(nearbyResponse.getHouseAds());
            } else {
                AbstractOffersFacade facade = new ConquestOffersFacade(contextPath, 1);
                List<Offer> offers = facade.getOffers(request);
                if (offers != null && !offers.isEmpty()) {
                    dealsResponse.setCitySearchOffer(offers.get(0));
                }
            }
        }
        return dealsResponse;
    }
}
