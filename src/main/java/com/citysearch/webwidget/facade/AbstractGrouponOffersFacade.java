package com.citysearch.webwidget.facade;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.GrouponCondition;
import com.citysearch.webwidget.api.bean.GrouponResponse;
import com.citysearch.webwidget.bean.DealsResponse;
import com.citysearch.webwidget.bean.GrouponDeal;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;
import com.citysearch.webwidget.util.Utils;

public abstract class AbstractGrouponOffersFacade {
	private Logger log = Logger.getLogger(getClass());
	private static final String DATE_FORMAT = "reviewdate.format";
	private static final String GROUPON_TRACKING_URL_KEY = "groupon.tracking.url";
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
		if (StringUtils.isBlank(request.getWhere())) {
			errors.add("Where is required.");
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

	public GrouponDeal toGrouponDeal(RequestBean request,
			GrouponResponse response) throws CitysearchException {
		String adUnitIdentifier = request.getAdUnitIdentifier();

		GrouponDeal deal = new GrouponDeal();

		deal.setId(response.getId());

		String dealUrl = response.getDealUrl();
		dealUrl = Utils.getThirdPartyTrackingUrl(dealUrl, request
				.getDartClickTrackUrl(), GROUPON_TRACKING_URL_KEY);
		deal.setDealUrl(dealUrl);

		StringBuilder titleLengthProp = new StringBuilder(adUnitIdentifier);
		titleLengthProp.append(".");
		titleLengthProp.append(CommonConstants.TITLE_LENGTH);
		String title = response.getTitle();
		title = Utils.getAbbreviatedString(title, titleLengthProp.toString());
		deal.setTitle(title);

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

		SimpleDateFormat formatter = new SimpleDateFormat(PropertiesLoader
				.getAPIProperties().getProperty(DATE_FORMAT));
		Date date = Utils.parseDate(response.getEnddate(), formatter);
		DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
		deal.setEnddate(df.format(date));

		deal.setTipped(response.isTipped());
		deal.setTippingPoint(response.getTippingPoint());
		deal.setTippedDate(response.getTippedDate());
		deal.setSoldOut(response.isSoldOut());
		deal.setQuantitySold(response.getQuantitySold());
		if (response.getPrice() != null) {
			String price = response.getPrice();
			price = StringUtils.replace(price, "USD", "");
			String decimalPart = StringUtils.substringAfter(price, ".");
			if (decimalPart != null && decimalPart.trim().length() > 0) {
				if (!StringUtils.isNumeric(decimalPart)
						|| NumberUtils.toDouble(decimalPart) == 0) {
					price = StringUtils.substringBefore(price, ".");
				}
			} else {
				price = StringUtils.substringBefore(price, ".");
			}

			deal.setPrice("$" + price);
		}
		if (response.getValue() != null) {
			String value = response.getValue();
			value = StringUtils.replace(value, "USD", "");
			String decimalPart = StringUtils.substringAfter(value, ".");
			if (decimalPart != null && decimalPart.trim().length() > 0) {
				if (!StringUtils.isNumeric(decimalPart)
						|| NumberUtils.toDouble(decimalPart) == 0) {
					value = StringUtils.substringBefore(value, ".");
				}
			} else {
				value = StringUtils.substringBefore(value, ".");
			}
			deal.setValue("$" + value);
		}
		deal.setDiscountAmount(response.getDiscountAmount());
		deal.setDiscountPercent(response.getDiscountPercent());

		GrouponCondition condition = response.getCondition();
		if (condition != null) {
			List<String> details = condition.getDetails();
			if (details != null && !details.isEmpty()) {
				List<String> dealdetails = new ArrayList<String>();
				StringBuilder detailLengthProp = new StringBuilder(
						adUnitIdentifier);
				detailLengthProp.append(".");
				detailLengthProp.append(CommonConstants.GROUPON_DETAIL_LENGTH);
				for (String d : details) {
					String detail = Utils.getAbbreviatedString(d,
							detailLengthProp.toString());
					dealdetails.add(detail);
				}
				deal.setDetails(dealdetails);
			}
		}
		return deal;
	}

	public abstract DealsResponse getDeals(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException;
}
