package com.citysearch.webwidget.api.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.api.bean.GrouponResponse;
import com.citysearch.webwidget.api.bean.GrouponCondition;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

public class GrouponProxy extends AbstractProxy {
	private Logger log = Logger.getLogger(getClass());
	private static final String GROUPON_URL_PROPERTY = "groupon.url";
	private static final String GROUPON_API_KEY_PROPERTY = "groupon.apikey";

	private String getGrouponQueryString(RequestBean request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();

		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties.getProperty(GROUPON_API_KEY_PROPERTY);
		apiQueryString.append(constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(constructQueryParam(
				APIFieldNameConstants.LATITUDE, request.getLatitude()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(constructQueryParam(
				APIFieldNameConstants.LONGITUDE, request.getLongitude()));

		return apiQueryString.toString();
	}

	private void validate(RequestBean request)
			throws InvalidRequestParametersException {
		log.info("Start GrouponProxy validate()");
		List<String> errors = new ArrayList<String>();
		if (StringUtils.isBlank(request.getLatitude())
				|| StringUtils.isBlank(request.getLongitude())) {
			errors.add("Invalid Latitude and/or Longitude.");
		}
		if (!errors.isEmpty()) {
			throw new InvalidRequestParametersException(this.getClass()
					.getName(), "RequestBean.validate()",
					"Invalid parameters.", errors);
		}
		log.info("End RequestBean validate()");
	}

	private GrouponResponse toOffer(Element dealElm) {
		GrouponResponse offer = new GrouponResponse();
		offer.setId(dealElm.getChildText("id"));
		offer.setDealUrl(dealElm.getChildText("deal_url"));
		offer.setTitle(dealElm.getChildText("title"));
		offer.setSmallImageUrl(dealElm.getChildText("small_image_url"));
		offer.setMediumImageUrl(dealElm.getChildText("medium_image_url"));
		offer.setLargeImageUrl(dealElm.getChildText("large_image_url"));
		offer.setDivisionId(dealElm.getChildText("division_id"));
		offer.setDivisionName(dealElm.getChildText("division_name"));
		offer.setLatitude(dealElm.getChildText("division_lat"));
		offer.setLongitude(dealElm.getChildText("division_lng"));
		offer.setVendorId(dealElm.getChildText("vendor_id"));
		offer.setVendorName(dealElm.getChildText("vendor_name"));
		offer.setVendorWebsite(dealElm.getChildText("vendor_website_url"));
		offer.setStatus(dealElm.getChildText("status"));
		offer.setStartDate(dealElm.getChildText("start_date"));
		offer.setEnddate(dealElm.getChildText("end_date"));
		offer.setTipped(Boolean.parseBoolean(dealElm.getChildText("tipped")));
		offer.setTippingPoint(dealElm.getChildText("tipping_point"));
		offer.setTippedDate(dealElm.getChildText("tipped_date"));
		offer
				.setSoldOut(Boolean.parseBoolean(dealElm
						.getChildText("sold_out")));
		offer.setQuantitySold(dealElm.getChildText("quantity_sold"));
		offer.setPrice(dealElm.getChildText("price"));
		offer.setValue(dealElm.getChildText("value"));
		offer.setDiscountAmount(dealElm.getChildText("discount_amount"));
		offer.setDiscountPercent(dealElm.getChildText("discount_percent"));

		List<Element> conditionElms = dealElm.getChildren("conditions");
		if (conditionElms != null && !conditionElms.isEmpty()) {
			List<GrouponCondition> conditions = new ArrayList<GrouponCondition>();
			for (Element conditionElm : conditionElms) {
				GrouponCondition condition = new GrouponCondition();
				condition.setLimitedQuantity(Boolean.parseBoolean(conditionElm
						.getChildText("limited_quantity")));
				condition.setInitialQuantity(conditionElm
						.getChildText("initial_quantity"));
				condition.setQuantityRemaining(conditionElm
						.getChildText("quantity_remaining"));
				condition.setMinimumPurchase(conditionElm
						.getChildText("minimum_purchase"));
				condition.setMaximumPurchase(conditionElm
						.getChildText("maximum_purchase"));
				condition.setExpirationDate(conditionElm
						.getChildText("expiration_date"));

				List<Element> detailElms = conditionElm.getChildren("details");
				if (detailElms != null && !detailElms.isEmpty()) {
					List<String> details = new ArrayList<String>();
					for (Element detailElm : detailElms) {
						details.add(detailElm.getChildText("detail"));
					}
					condition.setDetails(details);
				}

				conditions.add(condition);
			}
		}
		return offer;
	}

	private List<GrouponResponse> parse(Element rootElement, int requiredNoOfOffers) {
		List<GrouponResponse> offers = new ArrayList<GrouponResponse>();
		Element dealsElm = rootElement.getChild("deals");
		List<Element> deals = rootElement.getChildren("deal");
		for (Element dealElm : deals) {
			if (offers.size() == requiredNoOfOffers)
				break;
			GrouponResponse offer = toOffer(dealElm);
			offers.add(offer);
		}
		return offers;
	}

	public List<GrouponResponse> getOffers(RequestBean request,
			int requiredNoOfOffers) throws InvalidRequestParametersException,
			CitysearchException {
		validate(request);
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(GROUPON_URL_PROPERTY));
		urlStringBuilder.append(getGrouponQueryString(request));
		log.info("GrouponProxy.getOffers: Query: "
				+ urlStringBuilder.toString());
		Document grouponResponse = null;
		try {
			grouponResponse = getAPIResponse(urlStringBuilder.toString(), null);
			log.info("GrouponProxy.getOffers: successful response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getOffers", ihe);
		}
		if (grouponResponse != null && grouponResponse.hasRootElement()) {
			Element rootElement = grouponResponse.getRootElement();
			String code = rootElement.getAttributeValue("code");
			String message = rootElement.getAttributeValue("message");
			if (code != null && code.equals("0") && message != null
					&& message.equalsIgnoreCase("OK")) {
				return parse(rootElement, requiredNoOfOffers);
			}
		}
		return null;
	}
}
