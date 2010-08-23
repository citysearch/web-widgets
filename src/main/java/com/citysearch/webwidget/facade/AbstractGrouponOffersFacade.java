package com.citysearch.webwidget.facade;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.GrouponResponse;
import com.citysearch.webwidget.api.proxy.GrouponProxy;
import com.citysearch.webwidget.bean.GrouponDeal;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;

public abstract class AbstractGrouponOffersFacade {
	private Logger log = Logger.getLogger(getClass());
	protected String contextPath;
	protected int displaySize;

	protected AbstractGrouponOffersFacade(String contextPath, int displaySize) {
		this.contextPath = contextPath;
		this.displaySize = displaySize;
	}

	protected void validate(RequestBean request)
			throws InvalidRequestParametersException {
		List<String> errors = new ArrayList<String>();
		if (StringUtils.isBlank(request.getPublisher())) {
			errors.add("Publisher is required.");
		}
		if (StringUtils.isBlank(request.getWhat())) {
			errors.add("What is required.");
		}
		if (StringUtils.isBlank(request.getLatitude())
				|| StringUtils.isBlank(request.getLongitude())) {
			errors.add("Invalid Latitude and/or Longitude.");
		}
		if (StringUtils.isBlank(request.getClientIP())) {
			errors.add("Client IP is required.");
		}
		if (!errors.isEmpty()) {
			throw new InvalidRequestParametersException(this.getClass()
					.getName(), "AbstractGrouponOffersFacade.validate()",
					"Invalid parameters.", errors);
		}
	}

	public GrouponDeal toGrouponDeal(GrouponResponse response) {
		GrouponDeal deal = new GrouponDeal();

		deal.setId(response.getId());
		deal.setDealUrl(response.getDealUrl());
		deal.setTitle(response.getTitle());
		deal.setSmallImageUrl(response.getSmallImageUrl());
		deal.setMediumImageUrl(response.getMediumImageUrl());
		deal.setLargeImageUrl(response.getLargeImageUrl());
		deal.setDivisionId(response.getDivisionId());
		deal.setDivisionName(response.getDivisionName());
		deal.setLatitude(response.getLatitude());
		deal.setLongitude(response.getLongitude());
		deal.setVendorId(response.getVendorId());
		deal.setVendorName(response.getVendorName());
		deal.setVendorWebsite(response.getVendorWebsite());
		deal.setStatus(response.getStatus());
		deal.setStartDate(response.getStartDate());
		deal.setEnddate(response.getEnddate());
		deal.setTipped(response.isTipped());
		deal.setTippingPoint(response.getTippingPoint());
		deal.setTippedDate(response.getTippedDate());
		deal.setSoldOut(response.isSoldOut());
		deal.setQuantitySold(response.getQuantitySold());
		deal.setPrice(response.getPrice());
		deal.setValue(response.getValue());
		deal.setDiscountAmount(response.getDiscountAmount());
		deal.setDiscountPercent(response.getDiscountPercent());

		return deal;
	}

	public List<GrouponDeal> getDeals(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		validate(request);
		GrouponProxy proxy = new GrouponProxy();
		List<GrouponResponse> dealsFromGroupon = proxy.getOffers(request,
				displaySize);
		List<GrouponDeal> deals = new ArrayList<GrouponDeal>();
		if (dealsFromGroupon != null && !dealsFromGroupon.isEmpty()) {
			for (GrouponResponse response : dealsFromGroupon) {
				GrouponDeal deal = toGrouponDeal(response);
				deals.add(deal);
			}
		}
		return deals;
	}
}