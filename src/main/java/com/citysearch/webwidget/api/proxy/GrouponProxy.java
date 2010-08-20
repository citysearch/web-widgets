package com.citysearch.webwidget.api.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.api.bean.GrouponOffer;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
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
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.LATITUDE, request.getLatitude()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
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

	private GrouponOffer toOffer(Element dealElm) {
		GrouponOffer offer = new GrouponOffer();
		return offer;
	}

	private List<GrouponOffer> parse(Element rootElement) {
		List<GrouponOffer> offers = new ArrayList<GrouponOffer>();
		Element dealsElm = rootElement.getChild("deals");
		List<Element> deals = rootElement.getChildren("deal");
		for (Element dealElm : deals) {
			GrouponOffer offer = toOffer(dealElm);
			offers.add(offer);
		}
		return offers;
	}

	public List<GrouponOffer> getOffers(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
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
				return parse(rootElement);
			}
		}
		return null;
	}
}
