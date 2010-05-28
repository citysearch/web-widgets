package com.citysearch.webwidget.bean;

public abstract class AbstractRequest {
	protected String apiKey;
	protected String publisher;
	protected boolean customerOnly;
 
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
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
}
