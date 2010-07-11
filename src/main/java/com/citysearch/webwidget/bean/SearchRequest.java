package com.citysearch.webwidget.bean;

/**
 * Request bean for Search API
 * 
 * @author Aspert Benjamin
 * 
 */
public class SearchRequest extends AbstractRequest {
    private String rpp;
    private String tags;

    public SearchRequest() {
        super();
    }

    public SearchRequest(AbstractRequest request) {
        super();
        setPublisher(request.getPublisher());
        setDartClickTrackUrl(request.getDartClickTrackUrl());
        setCallBackFunction(request.getCallBackFunction());
        setCallBackUrl(request.getCallBackUrl());
        setAdUnitName(request.getAdUnitName());
        setAdUnitSize(request.getAdUnitSize());
        setLatitude(request.getLatitude());
        setLongitude(request.getLongitude());
        setWhat(request.getWhat());
        setWhere(request.getWhere());
        setRadius(request.getRadius());
    }

    public String getRpp() {
        return rpp;
    }

    public void setRpp(String rpp) {
        this.rpp = rpp;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}
