package com.citysearch.webwidget.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.helper.HouseAdsHelper;
import com.opensymphony.xwork2.Action;

public class HouseAdsAction extends AbstractCitySearchAction {
    private Logger log = Logger.getLogger(getClass());
    private String dartClickTrackUrl;
    private List<HouseAd> houseAds;

    public String getDartClickTrackUrl() {
        return dartClickTrackUrl;
    }

    public void setDartClickTrackUrl(String dartClickTrackUrl) {
        this.dartClickTrackUrl = dartClickTrackUrl;
    }

    public List<HouseAd> getHouseAds() {
        return houseAds;
    }

    public void setHouseAds(List<HouseAd> houseAds) {
        this.houseAds = houseAds;
    }

    public String execute() throws CitysearchException {
        try {
            houseAds = HouseAdsHelper.getHouseAds(getResourceRootPath(), dartClickTrackUrl);
        } catch (CitysearchException cse) {
            log.error(cse.getMessage());
            throw cse;
        }
        return Action.SUCCESS;
    }
}
