package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.AbstractOffersFacade;
import com.citysearch.webwidget.facade.OffersFacadeFactory;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.OneByOneTrackingUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class ConquestOffersAction extends AbstractCitySearchAction implements
		ModelDriven<RequestBean> {
	private static final Integer MAX_OFFER_DESCRIPTION_SIZE = 105;
	private static final Integer MAX_OFFER_TITLE_SIZE = 40;
	private static final Integer MAX_OFFER_LISTING_NAME_SIZE = 30;

	private Logger log = Logger.getLogger(getClass());
	private RequestBean offersRequest = new RequestBean();
	private Offer offer;
	private List<HouseAd> houseAds;

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public List<HouseAd> getHouseAds() {
		return houseAds;
	}

	public void setHouseAds(List<HouseAd> houseAds) {
		this.houseAds = houseAds;
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
		offersRequest.setAdUnitName(CommonConstants.AD_UNIT_NAME_OFFERS);
		if (offersRequest.getDisplaySize() == null
				|| offersRequest.getDisplaySize() == 0) {
			offersRequest.setDisplaySize(1);
		}

		try {

			AbstractOffersFacade facade = OffersFacadeFactory.getFacade(
					offersRequest.getPublisher(), getResourceRootPath(),
					offersRequest.getDisplaySize());
			List<Offer> offers = facade.getOffers(offersRequest);
			if (offers == null || offers.isEmpty()) {
				log.info("Returning backfill from offer");
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL, true);
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_SIZE,
						CommonConstants.CONQUEST_AD_SIZE);
				getHttpRequest().setAttribute(
						REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE,
						CommonConstants.CONQUEST_DISPLAY_SIZE);
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LATITUDE,
						offersRequest.getLatitude());
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LONGITUDE,
						offersRequest.getLongitude());
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL_FOR,
						CommonConstants.AD_UNIT_NAME_OFFERS);
				return "backfill";
			} else {
				// Format bigger text for display
				offer = offers.get(0);
				String offerDescription = offer.getOfferDescription();
				if (offerDescription != null
						&& offerDescription.trim().length() > MAX_OFFER_DESCRIPTION_SIZE) {
					offerDescription = StringUtils.abbreviate(offerDescription,
							MAX_OFFER_DESCRIPTION_SIZE);
				}
				offer.setOfferDescription(offerDescription);

				String offerTitle = offer.getOfferTitle();
				if (offerTitle != null
						&& offerTitle.trim().length() > MAX_OFFER_TITLE_SIZE) {
					offerTitle = StringUtils.abbreviate(offerTitle,
							MAX_OFFER_TITLE_SIZE);
				}
				offer.setOfferTitle(offerTitle);
				log.info("My offer title is :" + offerTitle.length()
						+ ", which is:" + offerTitle);

				String listingName = offer.getListingName();
				if (listingName != null
						&& listingName.trim().length() > MAX_OFFER_LISTING_NAME_SIZE) {
					listingName = StringUtils.abbreviate(listingName,
							MAX_OFFER_LISTING_NAME_SIZE);
				}
				offer.setListingName(listingName);
			}
		} catch (InvalidRequestParametersException ihre) {
			log.error(ihre.getDetailedMessage());
			houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 2);
		} catch (Exception e) {
			log.error(e.getMessage());
			houseAds = getHouseAds(offersRequest.getDartClickTrackUrl(), 2);
		}
		set1x1TrackingPixel(offersRequest.getAdUnitName(), offersRequest
				.getAdUnitSize());

		log.info("End offersAction execute()");
		return Action.SUCCESS;
	}

	private void set1x1TrackingPixel(String adunitName, String adunitSize) {
		try {
			String oneByOneTrackingUrl = OneByOneTrackingUtil
					.get1x1TrackingUrl(adunitName, adunitSize, 0, 0, 0, 0);
			setOneByOneTrackingUrl(oneByOneTrackingUrl);
		} catch (CitysearchException exp) {
			// DO not throw the exception.
			log.error(exp.getMessage());
		}
	}
}
