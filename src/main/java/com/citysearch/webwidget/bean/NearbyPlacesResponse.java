package com.citysearch.webwidget.bean;

import java.util.List;

public class NearbyPlacesResponse {
    private List<NearbyPlace> nearbyPlaces;
    private List<NearbyPlace> backfill;
    private List<HouseAd> houseAds;

    public List<NearbyPlace> getNearbyPlaces() {
        return nearbyPlaces;
    }

    public void setNearbyPlaces(List<NearbyPlace> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
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
