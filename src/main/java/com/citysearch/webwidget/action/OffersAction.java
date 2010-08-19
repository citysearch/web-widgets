package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.AbstractOffersFacade;
import com.citysearch.webwidget.facade.OffersFacadeFactory;
import com.citysearch.webwidget.util.CommonConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class OffersAction extends AbstractCitySearchAction implements
		ModelDriven<RequestBean> {
	private Logger log = Logger.getLogger(getClass());
	private static final Integer DEFAULT_DISPLAY_SIZE = 2;
	private static final String AD_UNIT_NAME = "offer";
	private RequestBean offersRequest = new RequestBean();
	private List<Offer> offers;
	private List<HouseAd> houseAds;

	public List<HouseAd> getHouseAds() {
		return houseAds;
	}

	public void setHouseAds(List<HouseAd> houseAds) {
		this.houseAds = houseAds;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	public RequestBean getOffersRequest() {
		return offersRequest;
	}

	public void setOffersRequest(RequestBean offersRequest) {
		this.offersRequest = offersRequest;
	}

	public RequestBean getModel() {
		return offersRequest;
	}

	/**
	 * Calls the getoffers() method from offersHelper class to get the offers,
	 * review count is fetched for each offer by passing its listing id to
	 * profile API
	 * 
	 * @return String
	 * @throws CitysearchException
	 */
	public String execute() throws CitysearchException {
		log.info("Start offersAction execute()");
		offersRequest.setAdUnitName(AD_UNIT_NAME);
		if (offersRequest.getDisplaySize() == null
				|| offersRequest.getDisplaySize() == 0
				|| offersRequest.getDisplaySize() > DEFAULT_DISPLAY_SIZE) {
			offersRequest.setDisplaySize(DEFAULT_DISPLAY_SIZE);
		}

		try {
			AbstractOffersFacade facade = OffersFacadeFactory.getFacade(
					offersRequest.getPublisher(), getResourceRootPath(),
					offersRequest.getDisplaySize());
			offers = facade.getOffers(offersRequest);
			if (offers == null || offers.isEmpty()) {
				log.info("Returning backfill from offer");
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL, true);
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_SIZE,
						CommonConstants.MANTLE_AD_SIZE);
				getHttpRequest().setAttribute(
						REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE,
						CommonConstants.MANTLE_DISPLAY_SIZE);
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LATITUDE,
						offersRequest.getLatitude());
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LONGITUDE,
						offersRequest.getLongitude());
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL_FOR,
						CommonConstants.AD_UNIT_NAME_OFFERS);
				return "backfill";
			}
		} catch (InvalidRequestParametersException ihre) {
			log.error(ihre.getDetailedMessage());
			houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 3);
		} catch (Exception e) {
			log.error(e.getMessage());
			houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 3);
		}
		log.info("End offersAction execute()");
		return Action.SUCCESS;
	}
}
