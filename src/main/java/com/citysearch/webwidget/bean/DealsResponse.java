package com.citysearch.webwidget.bean;

import java.util.List;

public class DealsResponse {
    private GrouponDeal grouponDeal;
    private Offer citySearchOffer;
    private List<NearbyPlace> places;
    private List<NearbyPlace> backfill;
    private List<NearbyPlace> searchResults;
    private List<HouseAd> houseAds;

    public List<NearbyPlace> getBackfill() {
        return backfill;
    }

    public void setBackfill(List<NearbyPlace> backfill) {
        this.backfill = backfill;
    }

    public List<NearbyPlace> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<NearbyPlace> searchResults) {
        this.searchResults = searchResults;
    }

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }

    public GrouponDeal getGrouponDeal() {
        return grouponDeal;
    }

    public void setGrouponDeal(GrouponDeal grouponDeal) {
        this.grouponDeal = grouponDeal;
    }

    public Offer getCitySearchOffer() {
        return citySearchOffer;
    }

    public void setCitySearchOffer(Offer citySearchOffer) {
        this.citySearchOffer = citySearchOffer;
    }

    public List<NearbyPlace> getPlaces() {
        return places;
    }

    public void setPlaces(List<NearbyPlace> places) {
        this.places = places;
    }

}
