package com.citysearch.webwidget.action;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.ConquestAdOffer;
import com.citysearch.webwidget.bean.ConquestAdOfferRequest;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.OffersRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.ConquestAdOfferHelper;
import com.citysearch.webwidget.helper.OffersHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class ConquestAdOfferAction extends AbstractCitySearchAction implements ModelDriven<ConquestAdOfferRequest> {
	
    private Logger log = Logger.getLogger(getClass());
    private ConquestAdOfferRequest offersRequest = new ConquestAdOfferRequest();
    private List<ConquestAdOffer> offersList;
    private static final String CLICK_TRACKING_URL = "http://ad.doubleclick.net/clk;225291110;48835962;h?";

	public List<ConquestAdOffer> getOffersList() {
		return offersList;
	}

	public void setOffersList(List<ConquestAdOffer> offersList) {
		this.offersList = offersList;
	}
	
    public ConquestAdOfferRequest getOffersRequest() {
        return offersRequest;
    }

    public void setOffersRequest(ConquestAdOfferRequest offersRequest) {
        this.offersRequest = offersRequest;
    }

    public ConquestAdOfferRequest getModel() {
        return offersRequest;
    }
    
    /**
     * Calls the getoffers() method from offersHelper class to get the offers, review count 
     * is fetched for each offer by passing its listing id to profile API
     * 
     * @return String
     * @throws CitysearchException
     */
    public String execute() throws CitysearchException {
        log.info("=========Start offersAction execute()============================ >"); 
        ConquestAdOfferHelper helper = new ConquestAdOfferHelper(getResourceRootPath());        
        try{
        	offersList = (List<ConquestAdOffer>) helper.getOffers(offersRequest);        	
    	    if (offersList.size() == 0) {
    	    	 log.info("Returning backfill from offer");
                 return "backfill";                 
    	    }   
    	    Iterator<ConquestAdOffer> it = offersList.iterator();
    	    while(it.hasNext()){
    	    	 ConquestAdOffer offer = (ConquestAdOffer)it.next();
        		 String listingUrl = null;        		  
        		 if(offer.getProfileUrl() != null){
                     /*
        			 listingUrl = getTrackingUrl(offer.getProfileUrl(),
                    		 CLICK_TRACKING_URL,
                             offer.getListingId(), offersRequest.getPublisher(),
                             offersRequest.getAdUnitName(),offersRequest.getAdUnitSize());
                     */        
        		 }else{
        			 listingUrl = "";
        		 }
                 offer.setProfileUrl(listingUrl);       	    	
    	    }
        }catch(InvalidRequestParametersException ihre){
        	log.error(ihre.getDetailedMessage());
            throw ihre;
        }catch (CitysearchException cse){
        	log.error(cse.getMessage());
            throw cse;
        }
        log.info("=========End offersAction execute()============================ >");
        return Action.SUCCESS;
    }
}
