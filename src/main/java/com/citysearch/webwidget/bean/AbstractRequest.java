package com.citysearch.webwidget.bean;

public abstract class AbstractRequest {
	protected String api_key;
	protected String publisher;
	protected boolean customerOnly;
	protected String format;
 
	public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

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
