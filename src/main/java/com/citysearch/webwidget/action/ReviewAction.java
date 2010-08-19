package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.AbstractReviewFacade;
import com.citysearch.webwidget.facade.ReviewFacadeFactory;
import com.citysearch.webwidget.util.CommonConstants;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

/**
 * This class processes the Reviews request, and gets the Reviews Response in
 * the execute() method
 * 
 * @author Aspert Benjamin
 * 
 */
public class ReviewAction extends AbstractCitySearchAction implements
		ModelDriven<RequestBean> {
	private Logger log = Logger.getLogger(getClass());
	private RequestBean reviewRequest = new RequestBean();
	private Review review;
	private List<HouseAd> houseAds;

	public List<HouseAd> getHouseAds() {
		return houseAds;
	}

	public void setHouseAds(List<HouseAd> houseAds) {
		this.houseAds = houseAds;
	}

	public RequestBean getReviewRequest() {
		return reviewRequest;
	}

	public void setReviewRequest(RequestBean reviewRequest) {
		this.reviewRequest = reviewRequest;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public RequestBean getModel() {
		return reviewRequest;
	}

	/**
	 * Calls the getLatestReview() method from ReviewHelper class to get the
	 * latest Review Returns the Response status
	 * 
	 * @return String
	 * @throws CitysearchException
	 */
	public String execute() throws CitysearchException {
		log.info("Start review action");
		reviewRequest.setAdUnitName(CommonConstants.AD_UNIT_NAME_REVIEW);
		if (reviewRequest.getAdUnitSize() == null
				|| reviewRequest.getAdUnitSize().trim().length() == 0) {
			reviewRequest.setAdUnitSize(CommonConstants.MANTLE_AD_SIZE);
			reviewRequest.setDisplaySize(CommonConstants.MANTLE_DISPLAY_SIZE);
		}
		if (reviewRequest.getDisplaySize() == null
				|| reviewRequest.getDisplaySize() == 0) {
			reviewRequest.setDisplaySize(CommonConstants.MANTLE_DISPLAY_SIZE);
		}
		try {
			AbstractReviewFacade facade = ReviewFacadeFactory.getFacade(
					reviewRequest.getPublisher(), getResourceRootPath(),
					reviewRequest.getDisplaySize());
			review = facade.getLatestReview(reviewRequest);

			if (review == null) {
				log.info("Returning backfill from review");
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL, true);
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_ADUNIT_SIZE,
						reviewRequest.getAdUnitSize());
				getHttpRequest().setAttribute(
						REQUEST_ATTRIBUTE_ADUNIT_DISPLAY_SIZE,
						reviewRequest.getDisplaySize());
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LATITUDE,
						reviewRequest.getLatitude());
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_LONGITUDE,
						reviewRequest.getLongitude());
				getHttpRequest().setAttribute(REQUEST_ATTRIBUTE_BACKFILL_FOR,
						CommonConstants.AD_UNIT_NAME_REVIEW);
				return "backfill";
			}
			log.info("End review action");
		} catch (InvalidRequestParametersException ihre) {
			log.error(ihre.getDetailedMessage());
			houseAds = getHouseAds(reviewRequest.getDartClickTrackUrl(), 3);
		} catch (Exception cse) {
			log.error(cse.getMessage());
			StackTraceElement[] elms = cse.getStackTrace();
			for (int k = 0; k < elms.length; k++) {
				log.error(elms[k]);
			}
			houseAds = getHouseAds(reviewRequest.getDartClickTrackUrl(), 3);
		}
		return Action.SUCCESS;
	}
}
