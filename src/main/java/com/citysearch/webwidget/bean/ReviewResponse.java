package com.citysearch.webwidget.bean;

// TODO: javadocs
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
