package com.citysearch.webwidget.bean;

public class OffersRequest extends AbstractRequest {
    private String version;
    private String placement;
    private String tag;
    private String page;
    private String rpp;
    private String expiresBefore;
    private String customerHasbudget;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getRpp() {
        return rpp;
    }

    public void setRpp(String rpp) {
        this.rpp = rpp;
    }

    public String getExpiresBefore() {
        return expiresBefore;
    }

    public void setExpiresBefore(String expiresBefore) {
        this.expiresBefore = expiresBefore;
    }

    public String getCustomerHasbudget() {
        return customerHasbudget;
    }

    public void setCustomerHasbudget(String customerHasbudget) {
        this.customerHasbudget = customerHasbudget;
    }
}
