package com.citysearch.webwidget.bean;

import java.util.List;

public class OffersResponse {
    private List<Offer> offers;
    private List<NearbyPlace> backfill;
    private List<HouseAd> houseAds;

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public List<NearbyPlace> getBackfill() {
        return backfill;
    }

    public void setBackfill(List<NearbyPlace> backfill) {
        this.backfill = backfill;
    }

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }
}
