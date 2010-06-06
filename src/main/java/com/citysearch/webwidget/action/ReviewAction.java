package com.citysearch.webwidget.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

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
public class ReviewAction implements ModelDriven<ReviewRequest>, ServletRequestAware,
        ServletResponseAware {
    private Logger log = Logger.getLogger(getClass());
    private ReviewRequest reviewRequest = new ReviewRequest();
    private Review review;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void setServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
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

    public String getRequestUrl() {
        return httpRequest.getRequestURL().toString();
    }

    /**
     * Calls the getLatestReview() method from ReviewHelper class to get the latest Review Returns
     * the Response status
     * 
     * @return String
     * @throws CitysearchException
     */
    public String execute() throws CitysearchException {
        ReviewHelper helper = new ReviewHelper();
        try {
            review = helper.getLatestReview(reviewRequest);
            if (review == null) {
                return "backfill";
            }
        } catch (InvalidRequestParametersException ihre) {
            log.error(ihre.getDetailedMessage());
            throw ihre;
        } catch (CitysearchException cse) {
            log.error(cse.getMessage());
            throw cse;
        }
        return Action.SUCCESS;
    }
}
