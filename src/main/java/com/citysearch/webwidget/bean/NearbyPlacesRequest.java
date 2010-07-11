package com.citysearch.webwidget.bean;

/**
 * Request object for PFP API
 * 
 * @author Aspert Benjamin
 * 
 */
public class NearbyPlacesRequest extends AbstractRequest {
    private String tags;

    public NearbyPlacesRequest() {
        super();
    }

    public NearbyPlacesRequest(AbstractRequest request) {
        super();
        setPublisher(request.getPublisher());
        setDartClickTrackUrl(request.getDartClickTrackUrl());
        setCallBackFunction(request.getCallBackFunction());
        setCallBackUrl(request.getCallBackUrl());
        setAdUnitName(request.getAdUnitName());
        setAdUnitSize(request.getAdUnitSize());
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
