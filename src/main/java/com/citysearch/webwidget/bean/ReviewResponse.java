package com.citysearch.webwidget.bean;

/**
 * Response bean for Reviews API
 * 
 * @author Aspert Benjamin
 * 
 */
public class ReviewResponse {
    private Review reviews;

    public ReviewResponse() {
    }

    public ReviewResponse(Review reviews) {
        this.reviews = reviews;
    }

    public Review getReviews() {
        return reviews;
    }

    public void setReviews(Review reviews) {
        this.reviews = reviews;
    }
}
