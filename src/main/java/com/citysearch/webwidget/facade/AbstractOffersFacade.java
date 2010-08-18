package com.citysearch.webwidget.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.api.bean.OfferAPIBean;
import com.citysearch.webwidget.api.proxy.OfferProxy;
import com.citysearch.webwidget.bean.Offer;
import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.facade.helper.ProfileHelper;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;
import com.citysearch.webwidget.util.Utils;

public abstract class AbstractOffersFacade {
	private Logger log = Logger.getLogger(getClass());
	private final static String PROPERTY_CITYSEARCH_COUPON_URL = "citysearch.coupon.url";
	protected String contextPath;
	protected int displaySize;
	protected AbstractOffersFacade(String contextPath, int displaySize) {
		this.contextPath = contextPath;
		this.displaySize = displaySize;
	}

	protected List<Offer> addDefaultImages(List<Offer> offers, String path)
			throws CitysearchException {
		if (offers != null && !offers.isEmpty()) {
			List<String> imageList = HelperUtil.getImages(path);
			if (imageList != null && !imageList.isEmpty()) {
				ArrayList<Integer> indexList = new ArrayList<Integer>(3);
				Random randomizer = new Random();
				for (int i = 0; i < offers.size(); i++) {
					Offer offer = offers.get(i);
					String imageUrl = offer.getImageUrl();
					if (StringUtils.isBlank(imageUrl)) {
						int index = 0;
						do {
							index = randomizer.nextInt(imageList.size());
						} while (indexList.contains(index));
						indexList.add(index);
						imageUrl = imageList.get(index);
						offer.setImageUrl(imageUrl);
					}
					offers.set(i, offer);
				}
			}
		}
		return offers;
	}

	private Offer toOffer(RequestBean request, OfferAPIBean offerApiBean)
			throws CitysearchException {
		Offer offer = new Offer();

		if (!StringUtils.isBlank(request.getLatitude())
				&& !StringUtils.isBlank(request.getLongitude())) {
			BigDecimal sourceLatitude = new BigDecimal(request.getLatitude());
			BigDecimal sourceLongitude = new BigDecimal(request.getLongitude());
			BigDecimal businessLatitude = new BigDecimal(offerApiBean
					.getLatitude());
			BigDecimal businessLongitude = new BigDecimal(offerApiBean
					.getLongitude());
			double distance = HelperUtil.getDistance(sourceLatitude,
					sourceLongitude, businessLatitude, businessLongitude);
			offer.setDistance(String.valueOf(distance));
		} else {
			offer.setDistance(null);
		}
		offer.setCity(offerApiBean.getCity());
		offer.setState(offerApiBean.getState());
		String location = Utils.getLocationString(offer.getCity(), offer
				.getState());
		offer.setLocation(location);
		offer.setAttributionSrc(offerApiBean.getAttributionSrc());
		String ratingVal = offerApiBean.getRating();
		List<Integer> ratingList = Utils.getRatingsList(ratingVal);
		offer.setListingRating(ratingList);
		offer.setReviewCount(Utils.toInteger(offerApiBean.getReviewCount()));
		offer.setImageUrl(offerApiBean.getImageUrl());
		offer.setLatitude(offerApiBean.getLatitude());
		offer.setListingId(offerApiBean.getListingId());
		offer.setLongitude(offerApiBean.getLongitude());
		offer.setOfferId(offerApiBean.getOfferId());

		String adUnitIdentifier = request.getAdUnitIdentifier();

		String offerTitle = offerApiBean.getOfferTitle();
		StringBuilder titleLengthProp = new StringBuilder(adUnitIdentifier);
		titleLengthProp.append(".");
		titleLengthProp.append(CommonConstants.TITLE_LENGTH);
		offerTitle = HelperUtil.getAbbreviatedString(offerTitle,
				titleLengthProp.toString());
		offer.setOfferTitle(offerTitle);
		offer.setOfferShortTitle(offerTitle);

		String offerdesc = offerApiBean.getOfferDescription();
		StringBuilder descLengthProp = new StringBuilder(adUnitIdentifier);
		descLengthProp.append(".");
		descLengthProp.append(CommonConstants.DESCRIPTION_LENGTH);
		offerdesc = HelperUtil.getAbbreviatedString(offerdesc, descLengthProp
				.toString());
		offer.setOfferDescription(offerdesc);

		String name = offerApiBean.getListingName();
		StringBuilder nameLengthProp = new StringBuilder(adUnitIdentifier);
		nameLengthProp.append(".");
		nameLengthProp.append(CommonConstants.NAME_LENGTH);
		name = HelperUtil.getAbbreviatedString(name, nameLengthProp.toString());
		offer.setListingName(name);

		offer.setReferenceId(offerApiBean.getReferenceId());
		offer.setStreet(offerApiBean.getStreet());
		offer.setZip(offerApiBean.getZip());
		return offer;
	}

	private List<Offer> toOffers(RequestBean request, List<OfferAPIBean> offers)
			throws CitysearchException {
		log.info("Start OffersHelper parseXML");
		List<Offer> offersList = null;
		if (offers != null && !offers.isEmpty()) {
			offersList = new ArrayList<Offer>();
			for (OfferAPIBean apiBean : offers) {
				Offer offer = toOffer(request, apiBean);
				offersList.add(offer);
			}
			addDefaultImages(offersList, contextPath);
		}
		log.info("End OffersHelper parseXML");
		return offersList;
	}

	public List<Offer> getOffers(RequestBean request)
			throws InvalidRequestParametersException, CitysearchException {
		log.info("Start offersHelper getOffers()");
		request.validate();
		request.setCustomerHasbudget("true");
		OfferProxy proxy = new OfferProxy();
		List<OfferAPIBean> apiResponse = proxy.getOffers(request, displaySize);
		List<Offer> offersList = toOffers(request, apiResponse);
		ProfileHelper profileHelper = new ProfileHelper(contextPath);
		Properties properties = PropertiesLoader.getAPIProperties();
		if (offersList != null && !offersList.isEmpty()) {
			for (Offer offer : offersList) {
				request.setListingId(offer.getListingId());
				Profile profile = profileHelper.getProfile(request);
				if (profile != null) {
					offer.setReviewCount(HelperUtil.toInteger(profile
							.getReviewCount()));
					offer.setProfileUrl(profile.getProfileUrl());
					offer.setPhone(HelperUtil.parsePhone(profile.getPhone()));

					offer.setCallBackFunction(request.getCallBackFunction());
					offer.setCallBackUrl(request.getCallBackUrl());

					String profileTrackingUrl = HelperUtil.getTrackingUrl(
							profile.getProfileUrl(), null, request
									.getCallBackUrl(), request
									.getDartClickTrackUrl(), offer
									.getListingId(), profile.getPhone(),
							request.getPublisher(), request.getAdUnitName(),
							request.getAdUnitSize());
					offer.setProfileTrackingUrl(profileTrackingUrl);

					String callBackFn = HelperUtil.getCallBackFunctionString(
							request.getCallBackFunction(),
							offer.getListingId(), profile.getPhone());
					offer.setCallBackFunction(callBackFn);

					StringBuilder couponUrl = new StringBuilder(properties
							.getProperty(PROPERTY_CITYSEARCH_COUPON_URL));
					couponUrl.append(HelperUtil.constructQueryParam(
							CommonConstants.LISTING_ID, offer.getListingId()));
					couponUrl.append(CommonConstants.SYMBOL_AMPERSAND);
					couponUrl.append(HelperUtil.constructQueryParam("offerId",
							offer.getOfferId()));

					String couponTrackingUrl = HelperUtil.getTrackingUrl(
							couponUrl.toString(), null, null, request
									.getDartClickTrackUrl(), offer
									.getListingId(), profile.getPhone(),
							request.getPublisher(), request.getAdUnitName(),
							request.getAdUnitSize());
					offer.setCouponUrl(couponTrackingUrl);
				}
			}
		}

		if (offersList != null && !offersList.isEmpty()
				&& offersList.size() < request.getDisplaySize()) {

			for (Offer offer : offersList) {
				request.setListingId(offer.getListingId());
				Profile profile = profileHelper
						.getProfileAndHighestReview(request);
				offer.setProfile(profile);
			}
		}

		log.info("End offersHelper getOffers()");
		return offersList;
	}
}
