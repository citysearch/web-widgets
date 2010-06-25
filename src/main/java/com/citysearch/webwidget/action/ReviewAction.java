package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.helper.ReviewHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

/**
 * This class processes the Reviews request, and gets the Reviews Response in the execute() method
 * 
 * @author Aspert Benjamin
 * 
 */
public class ReviewAction extends AbstractCitySearchAction implements ModelDriven<ReviewRequest> {
    private Logger log = Logger.getLogger(getClass());
    private ReviewRequest reviewRequest = new ReviewRequest();
    private Review review;
    private List<HouseAd> houseAds;

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }

    public ReviewRequest getReviewRequest() {
        return reviewRequest;
    }

    public void setReviewRequest(ReviewRequest reviewRequest) {
        this.reviewRequest = reviewRequest;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public ReviewRequest getModel() {
        return reviewRequest;
    }

    /**
     * Calls the getLatestReview() method from ReviewHelper class to get the latest Review Returns
     * the Response status
     * 
     * @return String
     * @throws CitysearchException
     */
    public String execute() throws CitysearchException {
        ReviewHelper helper = new ReviewHelper(getResourceRootPath());
        log.info("Start review action ============================ >");
        try {
            review = helper.getLatestReview(reviewRequest);
            if (review == null) {
                log.info("Returning backfill from review");
                return "backfill";
            }
            log.info("End review action ============================ >");
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            houseAds = getHouseAds(reviewRequest.getDartClickTrackUrl(), 3);
        } catch (Exception cse) {
            log.error(cse.getMessage());
            houseAds = getHouseAds(reviewRequest.getDartClickTrackUrl(), 3);
        }
        return Action.SUCCESS;
    }
}
