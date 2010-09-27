package com.citysearch.webwidget.facade;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.GrouponResponse;
import com.citysearch.webwidget.api.proxy.GrouponProxy;
import com.citysearch.webwidget.bean.DealsResponse;
import com.citysearch.webwidget.bean.GrouponDeal;
import com.citysearch.webwidget.bean.NearbyPlacesResponse;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

public class UrbanSpoonFacade extends AbstractGrouponOffersFacade {
    private Logger log = Logger.getLogger(getClass());
    private static final String GROUPON_CLICKTHROUGH_URL = "groupon.secondary.tracking.url";
    private static final String GROUPON_CITYGRID_CLICKTHROUGH_URL = "groupon.citygrid.tracking.url";
    
    protected UrbanSpoonFacade(String contextPath, int displaySize) {
        super(contextPath, displaySize);
    }

    public DealsResponse getDeals(RequestBean request) throws InvalidRequestParametersException,
            CitysearchException {
        validate(request);
        //Initialize tracking Url's
        String originalDartTrackingUrl = request.getDartClickTrackUrl();
        Properties apiProperties = PropertiesLoader.getAPIProperties();
        String grouponClickThroughUrl = apiProperties.getProperty(GROUPON_CLICKTHROUGH_URL);
        String grouponCityGridClickThroughUrl = apiProperties.getProperty(GROUPON_CITYGRID_CLICKTHROUGH_URL);
        
        StringBuilder grouponTrackingUrl = new StringBuilder();
        if (originalDartTrackingUrl != null) {
            grouponTrackingUrl.append(originalDartTrackingUrl);
        }
        if (grouponClickThroughUrl != null) {
            grouponTrackingUrl.append(grouponClickThroughUrl);
        }
        
        StringBuilder cityGridTrackingUrl = new StringBuilder();
        if (originalDartTrackingUrl != null) {
            cityGridTrackingUrl.append(originalDartTrackingUrl);
        }
        if (grouponCityGridClickThroughUrl != null) {
            cityGridTrackingUrl.append(grouponCityGridClickThroughUrl);
        }
        
        GrouponProxy proxy = new GrouponProxy();
        List<GrouponResponse> dealsFromGroupon = proxy.getOffers(request, displaySize);
        DealsResponse dealsResponse = new DealsResponse();
        //dealsFromGroupon = null; Uncomment to force CS Offer
        if (dealsFromGroupon != null && !dealsFromGroupon.isEmpty()) {
            GrouponResponse response = dealsFromGroupon.get(0);
            request.setDartClickTrackUrl(grouponTrackingUrl.toString());
            GrouponDeal deal = toGrouponDeal(request, response);
            dealsResponse.setGrouponDeal(deal);
            /*
            // Reset the what so that the ads are relevant
            request.setWhat(deal.getVendorName());

            AbstractNearByPlacesFacade facade = new UrbanSpoonNearbyPlacesFacade(contextPath, 1);
            request.setDartClickTrackUrl(cityGridTrackingUrl.toString());
            NearbyPlacesResponse nearbyResponse = facade.getNearbyPlaces(request);

            dealsResponse.setPlaces(nearbyResponse.getNearbyPlaces());
            dealsResponse.setSearchResults(nearbyResponse.getSearchResults());
            dealsResponse.setBackfill(nearbyResponse.getBackfill());
            dealsResponse.setHouseAds(nearbyResponse.getHouseAds());
			*/
        } else {
            //Set the adunit name to offer so that we use the correct field properites.
            request.setAdUnitName(CommonConstants.AD_UNIT_NAME_OFFERS);
            AbstractOffersFacade facade = new ConquestOffersFacade(contextPath, 1);
            request.setDartClickTrackUrl(cityGridTrackingUrl.toString());
            List<Offer> offers = facade.getOffers(request);
            if (offers != null && !offers.isEmpty()) {
                dealsResponse.setCitySearchOffer(offers.get(0));
            }
            //Revert it back so that 1x1 are generated properly.
            request.setAdUnitName(CommonConstants.AD_UNIT_NAME_DEALS);
        }
        return dealsResponse;
    }
}
