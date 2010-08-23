package com.citysearch.webwidget.api.bean;

import java.util.List;

public class GrouponCondition {
	private boolean limitedQuantity;
	private String initialQuantity;
	private String quantityRemaining;
	private String minimumPurchase;
	private String maximumPurchase;
	private String expirationDate;
	private List<String> details;

	public boolean isLimitedQuantity() {
		return limitedQuantity;
	}

	public void setLimitedQuantity(boolean limitedQuantity) {
		this.limitedQuantity = limitedQuantity;
	}

	public String getInitialQuantity() {
		return initialQuantity;
	}

	public void setInitialQuantity(String initialQuantity) {
		this.initialQuantity = initialQuantity;
	}

	public String getQuantityRemaining() {
		return quantityRemaining;
	}

	public void setQuantityRemaining(String quantityRemaining) {
		this.quantityRemaining = quantityRemaining;
	}

	public String getMinimumPurchase() {
		return minimumPurchase;
	}

	public void setMinimumPurchase(String minimumPurchase) {
		this.minimumPurchase = minimumPurchase;
	}

	public String getMaximumPurchase() {
		return maximumPurchase;
	}

	public void setMaximumPurchase(String maximumPurchase) {
		this.maximumPurchase = maximumPurchase;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

}
