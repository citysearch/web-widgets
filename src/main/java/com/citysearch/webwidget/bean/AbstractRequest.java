package com.citysearch.webwidget.bean;

/**
 * The abstract class that contains the common Request field across APIs
 * 
 * @author Aspert Benjamin
 * 
 */
public abstract class AbstractRequest {
    protected String publisher;
    protected boolean customerOnly;
    protected String format;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public boolean isCustomerOnly() {
        return customerOnly;
    }

    public void setCustomerOnly(boolean customerOnly) {
        this.customerOnly = customerOnly;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
