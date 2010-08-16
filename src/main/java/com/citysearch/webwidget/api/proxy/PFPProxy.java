package com.citysearch.webwidget.api.proxy;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.api.bean.PFPAd;
import com.citysearch.webwidget.api.bean.PFPResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

public class PFPProxy extends AbstractProxy {
	private Logger log = Logger.getLogger(getClass());

	private final static String PFP_LOCATION_URL = "pfplocation.url";
	private final static String PFP_URL = "pfp.url";
	private static final String AD_TAG = "ad";
	private static final String TYPE_TAG = "type";
	private static final String REVIEW_RATING_TAG = "overall_review_rating";
	private static final String REVIEWS_TAG = "reviews";
	private static final String LISTING_ID_TAG = "listingId";
	private static final String TAGLINE_TAG = "tagline";
	private static final String AD_DISPLAY_URL_TAG = "ad_display_url";
	private static final String AD_IMAGE_URL_TAG = "ad_image_url";
	private static final String PHONE_TAG = "phone";
	private static final String DESC_TAG = "description";
	private static final String ZIP_TAG = "zip";
	private static final String AD_DESTINATION_URL = "ad_destination_url";
	private static final String AD_TYPE_PFP = "local PFP";
	private static final String AD_TYPE_BACKFILL = "backfill";

	private PFPResponse parse(Document document) {
		PFPResponse response = new PFPResponse();
		if (document != null && document.hasRootElement()) {
			Element rootElement = document.getRootElement();
			List<Element> children = rootElement.getChildren(AD_TAG);
			if (children != null && !children.isEmpty()) {
				for (Element elm : children) {
					String adType = StringUtils
							.trim(elm.getChildText(TYPE_TAG));
					if (adType != null && adType.equalsIgnoreCase(AD_TYPE_PFP)) {
						PFPAd ad = toPFPAd(elm);
						response.getLocalPfp().add(ad);
					} else if (adType != null
							&& adType.equalsIgnoreCase(AD_TYPE_BACKFILL)) {
						PFPAd ad = toPFPAdBackfill(elm);
						response.getBackfill().add(ad);
					}
				}
			}
		}
		return response;
	}

	private PFPAd toPFPAd(Element adElm) {
		PFPAd ad = new PFPAd();
		ad.setName(adElm.getChildText(CommonConstants.NAME));
		ad.setRating(adElm.getChildText(REVIEW_RATING_TAG));
		ad.setReviewCount(adElm.getChildText(REVIEWS_TAG));
		ad.setDistance(adElm.getChildText(CommonConstants.DISTANCE));
		ad.setCategory(adElm.getChildText(TAGLINE_TAG));
		ad.setListingId(adElm.getChildText(LISTING_ID_TAG));
		ad.setAdDisplayUrl(adElm.getChildText(AD_DISPLAY_URL_TAG));
		ad.setImageUrl(adElm.getChildText(AD_IMAGE_URL_TAG));
		ad.setPhone(adElm.getChildText(PHONE_TAG));
		ad.setOffers(adElm.getChildText(CommonConstants.OFFERS));
		ad.setDescription(adElm.getChildText(DESC_TAG));
		ad.setStreet(adElm.getChildText(CommonConstants.STREET));
		ad.setCity(adElm.getChildText(CommonConstants.CITY));
		ad.setState(adElm.getChildText(CommonConstants.STATE));
		ad.setPostalCode(adElm.getChildText(ZIP_TAG));
		ad.setAdDestinationUrl(adElm.getChildText(AD_DESTINATION_URL));
		return ad;
	}

	private PFPAd toPFPAdBackfill(Element adElm) {
		PFPAd ad = new PFPAd();
		String category = adElm.getChildText(TAGLINE_TAG);
		if (StringUtils.isNotBlank(category)) {
			category = category.replaceAll("<b>", "");
			category = category.replaceAll("</b>", "");
			ad.setCategory(category);
		}
		ad.setImageUrl(adElm.getChildText(AD_IMAGE_URL_TAG));

		String description = adElm.getChildText(DESC_TAG);
		if (StringUtils.isNotBlank(description)) {
			description = description.replaceAll("<b>", "");
			description = description.replaceAll("</b>", "");
			ad.setDescription(description);
		}

		ad.setOffers(adElm.getChildText(CommonConstants.OFFERS));
		ad.setAdDisplayUrl(adElm.getChildText(AD_DISPLAY_URL_TAG));
		ad.setAdDestinationUrl(adElm.getChildText(AD_DESTINATION_URL));
		ad.setListingId(adElm.getChildText(LISTING_ID_TAG));
		ad.setPhone(adElm.getChildText(PHONE_TAG));
		return ad;
	}

	public PFPResponse getAdsFromPFPLocation(RequestBean request,
			int requiredNoOfAds, int extendedRadius)
			throws InvalidRequestParametersException, CitysearchException {
		request.validate();
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PFP_LOCATION_URL));
		urlStringBuilder.append(getLatLonQueryString(request));
		log.info("PFPProxy.getAdsFromPFPLocation: Query: "
				+ urlStringBuilder.toString());
		Document pfpLocationResponse = null;
		try {
			pfpLocationResponse = getAPIResponse(urlStringBuilder.toString(),
					null);
			log.info("PFPProxy.getAdsFromPFPLocation: successful response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getAdsFromPFPLocation", ihe);
		}
		return parse(pfpLocationResponse);
	}

	public PFPResponse getAdsFromPFP(RequestBean request, int requiredNoIfAds)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("PFPProxy.getAdsFromPFP: Begin");
		request.validate();
		Properties properties = PropertiesLoader.getAPIProperties();
		StringBuilder urlStringBuilder = new StringBuilder(properties
				.getProperty(PFP_URL));
		urlStringBuilder.append(getWhereQueryString(request));
		log.info("PFPProxy.getAdsFromPFP: Query: "
				+ urlStringBuilder.toString());
		Document pfpResponse = null;
		try {
			pfpResponse = getAPIResponse(urlStringBuilder.toString(), null);
			log.info("PFPProxy.getAdsFromPFP: successful response");
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getAdsFromPFP", ihe);
		}
		return parse(pfpResponse);
	}
}
