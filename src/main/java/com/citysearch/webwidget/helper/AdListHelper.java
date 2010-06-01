package com.citysearch.webwidget.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.AdListBean;
import com.citysearch.webwidget.bean.AdListRequest;
import com.citysearch.webwidget.bean.SearchRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class AdListHelper {

	private final static String PFP_LOCATION_URL = "pfplocation.url";

	private Logger log = Logger.getLogger(getClass());
	private static final String ioExcepMsg = "streamread.error";
	private static final String jdomExcepMsg = "jdom.excep.msg";
	private static final String imagesPropertiesFile = "images.properties";
	private static final String adTag = "ad";
	private static final String locationTag = "location";
	private static final int displaySize = 3;
	private static final String commaString = ",";
	private static final String spaceString = " ";
	private static final String busNameMaxLengthProp = "name.length";
	private static final String taglineMaxLengthProp = "tagline.length";
	private static final int busNameMaxLength = 30;
	private static final int tagLineMaxLength = 30;
	private static final double kmToMile = 0.622;
	private static final int radius = 6371;
	private static final int totalRating = 5;
	private static final int emptyStar = 0;
	private static final int halfStar = 1;
	private static final int fullStar = 2;
	private static final String imageError = "image.properties.error";
	private static Properties imageProperties;
	private static final String apiTypeError = "invalid.apitype";
	protected static final int extendedRadius = 25;
	
	private static final String reviewRatingTag = "overall_review_rating";
    private static final String reviewsTag = "reviews";
    private static final String listingIdTag = "listingId";
    private static final String taglineTag = "tagline";
    private static final String adDisplayURLTag = "ad_display_url";
    private static final String adImageURLTag = "ad_image_url";
    private static final String phoneTag = "phone";
    
	private void validateRequest(AdListRequest request)
			throws CitysearchException {
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();
		if (StringUtils.isBlank(request.getWhat())
				&& StringUtils.isBlank(request.getTags())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.WHAT_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getWhere())
				&& (StringUtils.isBlank(request.getSourceLat()) || StringUtils
						.isBlank(request.getSourceLon()))) {
			errors.add(errorProperties
					.getProperty(CommonConstants.WHERE_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getPublisher())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}

		if (!errors.isEmpty()) {
			throw new CitysearchException(this.getClass().getName(),
					"validateRequest", "Invalid parameters.", errors);
		}
	}

	private String getQueryStringWithGeography(AdListRequest request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();

		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, request.getWhat()));
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.LATITUDE, request.getSourceLat()));
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.LONGITUDE, request.getSourceLon()));
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.TAG, request.getTags()));
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.RADIUS, request.getRadius()));
		return apiQueryString.toString();
	}

	private String getQueryStringWithoutGeography(AdListRequest request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();
		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, request.getWhat()));
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.TAG, request.getTags()));
		return apiQueryString.toString();
	}

	public void getAdList(AdListRequest request) throws CitysearchException {
		validateRequest(request);
		if (StringUtils.isBlank(request.getSourceLat())
				|| StringUtils.isBlank(request.getSourceLon())) {
			SearchRequest sRequest = new SearchRequest();
			sRequest.setWhat(request.getWhat());
			sRequest.setWhere(request.getWhere());
			sRequest.setTags(request.getTags());
			sRequest.setPublisher(request.getPublisher());

			SearchHelper sHelper = new SearchHelper();
			String[] latLon = sHelper.getLatitudeLongitude(sRequest);
			if (latLon.length < 2) {
				request.setSourceLat(latLon[0]);
				request.setSourceLon(latLon[1]);
			}

			Properties properties = PropertiesLoader.getAPIProperties();
			String urlString = properties.getProperty(PFP_LOCATION_URL)
					+ getQueryStringWithGeography(request);
			Document responseDocument = null;
			try {
				responseDocument = HelperUtil.getAPIResponse(urlString);
			} catch (InvalidHttpResponseException ihe) {
				throw new CitysearchException(this.getClass().getName(),
						"getAdList", ihe.getMessage());
			}
			ArrayList<AdListBean> adList = parseXML(responseDocument, request.getSourceLat(),
                    request.getSourceLon(), CommonConstants.PFP_API_TYPE, "/");
			if (adList == null || adList.size() == 0)
			{
				urlString = properties.getProperty(CommonConstants.PFP_WITHOUT_GEOGRAPHY)
				+ getQueryStringWithoutGeography(request);
				responseDocument = null;
				try {
					responseDocument = HelperUtil.getAPIResponse(urlString);
				} catch (InvalidHttpResponseException ihe) {
					throw new CitysearchException(this.getClass().getName(),
							"getAdList", ihe.getMessage());
				}
			}
		}
	}

	public ArrayList<AdListBean> parseXML(Document doc, String sLat,
			String sLon, String apiType, String contextPath)
			throws CitysearchException {
		ArrayList<AdListBean> adList = new ArrayList<AdListBean>();
		try {
			if (doc != null && doc.hasRootElement()) {
				Element rootElement = doc.getRootElement();
				String childElem = getApiChildElementName(apiType);
				List<Element> resultSet = rootElement.getChildren(childElem);
				if (resultSet != null) {
					int size = resultSet.size();
					HashMap<String, String> resultMap;
					// Retrieving values from result xml
					for (int i = 0; i < size; i++) {
						AdListBean adListBean = new AdListBean();
						Element ad = (Element) resultSet.get(i);
						resultMap = processElement(ad);
						adListBean = processMap(resultMap, sLat, sLon);
						if (adListBean != null)
							adList.add(adListBean);
					}
				}
			}
		} catch (Exception excep) {
			String errMsg = PropertiesLoader.getErrorProperties().getProperty(
					CommonConstants.ERROR_METHOD_PARAM)
					+ " parseXML()";
			log.error(errMsg, excep);
			throw new CitysearchException(this.getClass().getName(),
					"parseXML", excep.getMessage());
		}
		Collections.sort(adList);
		adList = getDisplayList(adList, contextPath);
		return adList;
	}

	protected ArrayList<AdListBean> getDisplayList(
			ArrayList<AdListBean> adList, String contextPath)
			throws CitysearchException {
		ArrayList<AdListBean> displayList = new ArrayList<AdListBean>(3);
		if (adList.size() > 3) {
			for (int i = 0; i < displaySize; i++) {
				displayList.add(adList.get(i));
			}
		} else {
			displayList = adList;
		}
		displayList = addDefaultImages(displayList, contextPath);
		return displayList;
	}

	private ArrayList<String> getImageList(String contextPath)
			throws CitysearchException {
		ArrayList<String> imageList = new ArrayList<String>();
		try {
			if (imageProperties == null) {
				imageProperties = PropertiesLoader
						.getProperties(imagesPropertiesFile);
			}
			Enumeration<Object> enumerator = imageProperties.keys();
			while (enumerator.hasMoreElements()) {
				String key = (String) enumerator.nextElement();
				String value = imageProperties.getProperty(key);
				imageList.add(contextPath + "/" + value);
			}

		} catch (Exception excep) {
			String errMsg = PropertiesLoader.getErrorProperties().getProperty(
					imageError);
			log.error(errMsg);
		}

		return imageList;
	}

	private ArrayList<AdListBean> addDefaultImages(
			ArrayList<AdListBean> adList, String contextPath)
			throws CitysearchException {
		AdListBean adListBean;
		ArrayList<String> imageList;
		Random random;
		ArrayList<Integer> indexList = new ArrayList<Integer>(3);
		int imageListSize = 0;
		String imageUrl = "";

		imageList = getImageList(contextPath);
		random = new Random();
		int size = adList.size();

		for (int i = 0; i < size; i++) {
			adListBean = adList.get(i);
			imageUrl = adListBean.getAdImageURL();
			if (StringUtils.isBlank(imageUrl)) {
				int index = 0;
				imageListSize = imageList.size();
				if (imageListSize > 0) {
					do {
						index = random.nextInt(imageListSize);
					} while (indexList.contains(index));
					indexList.add(index);
					imageUrl = imageList.get(index);
					adListBean.setAdImageURL(imageUrl);
				}
			}
			adList.set(i, adListBean);
		}

		return adList;
	}

	protected HashMap<String, String> processElement(Element ad) {
		HashMap<String, String> elementMap = new HashMap<String, String>();
		if (ad != null) {
			String name = ad.getChildText(CommonConstants.NAME);
			if (StringUtils.isNotBlank(name)) {
				elementMap.put(CommonConstants.NAME, name);
				elementMap.put(CommonConstants.CITY, ad
						.getChildText(CommonConstants.CITY));
				elementMap.put(CommonConstants.STATE, ad
						.getChildText(CommonConstants.STATE));
				elementMap.put(CommonConstants.RATING, ad
						.getChildText(reviewRatingTag));
				elementMap.put(CommonConstants.REVIEWCOUNT, ad
						.getChildText(reviewsTag));
				elementMap.put(CommonConstants.LISTING_ID, ad
						.getChildText(listingIdTag));
				elementMap.put(CommonConstants.CATEGORY, ad
						.getChildText(taglineTag));
				elementMap.put(CommonConstants.DLAT, ad
						.getChildText(CommonConstants.LATITUDE));
				elementMap.put(CommonConstants.DLON, ad
						.getChildText(CommonConstants.LONGITUDE));
				elementMap
						.put(CommonConstants.PHONE, ad.getChildText(phoneTag));
				elementMap.put(CommonConstants.DISPLAY_URL, ad
						.getChildText(adDisplayURLTag));
				elementMap.put(CommonConstants.IMAGE_URL, ad
						.getChildText(adImageURLTag));
			}
		}
		return elementMap;
	}

	private AdListBean processMap(HashMap<String, String> resultMap,
			String sLat, String sLon) throws CitysearchException {
		AdListBean adListBean = null;
		if (resultMap != null) {
			// Calculating Distance
			double distance = 0.0;
			String dLat = resultMap.get(CommonConstants.DLAT);
			String dLon = resultMap.get(CommonConstants.DLON);
			String rating = resultMap.get(CommonConstants.RATING);
			String reviewCount = resultMap.get(CommonConstants.REVIEWCOUNT);
			String listingId = resultMap.get(CommonConstants.LISTING_ID);
			String category = resultMap.get(CommonConstants.CATEGORY);
			String name = resultMap.get(CommonConstants.NAME);
			String phone = resultMap.get(CommonConstants.PHONE);
			if (StringUtils.isNotBlank(sLat) && StringUtils.isNotBlank(sLon)
					&& StringUtils.isNotBlank(dLat)
					&& StringUtils.isNotBlank(dLon)) {
				BigDecimal sourceLat = new BigDecimal(sLat);
				BigDecimal sourceLon = new BigDecimal(sLon);
				BigDecimal destLat = new BigDecimal(dLat);
				BigDecimal destLon = new BigDecimal(dLon);
				distance = getDistance(sourceLat, sourceLon, destLat, destLon);
			}

			int[] ratingList = getRatingsList(rating);
			double ratings = getRatingValue(rating);
			int userReviewCount = getUserReviewCount(reviewCount);
			name = getBusinessName(name);
			category = getTagLine(category);
			String location = getLocation(resultMap.get(CommonConstants.CITY),
					resultMap.get(CommonConstants.STATE));

			// Adding to AdListBean
			if (distance < extendedRadius) {
				adListBean = new AdListBean();
				adListBean.setName(name);
				adListBean.setLocation(location);
				adListBean.setRating(ratingList);
				adListBean.setReviewCount(userReviewCount);
				adListBean.setDistance(distance);
				adListBean.setListingId(StringUtils.trim(listingId));
				adListBean.setCategory(category);
				adListBean.setRatings(ratings);
				adListBean.setAdDisplayURL(resultMap
						.get(CommonConstants.DISPLAY_URL));
				adListBean.setAdImageURL(resultMap
						.get(CommonConstants.IMAGE_URL));
				adListBean.setPhone(StringUtils.trim(phone));
			}
		}
		return adListBean;
	}

	protected int[] getRatingsList(String rating) {
		int[] ratingList = new int[totalRating];
		int count = 0;
		if (StringUtils.isNotBlank(rating)) {
			double ratings = (Double.parseDouble(rating)) / 2;
			int userRating = (int) ratings;
			while (count < userRating) {
				ratingList[count++] = fullStar;
			}

			if (ratings % 1 != 0)
				ratingList[count++] = halfStar;

			while (count < totalRating) {
				ratingList[count++] = emptyStar;
			}

		} else {
			for (count = 0; count < totalRating; count++) {
				ratingList[count] = emptyStar;
			}
		}
		return ratingList;
	}

	protected double getRatingValue(String rating) {
		double ratings = 0.0;
		if (StringUtils.isNotBlank(rating)) {
			ratings = (Double.parseDouble(rating)) / 2;
			ratings = Math.floor(ratings * 10) / 10.0;
		}
		return ratings;

	}

	protected int getUserReviewCount(String reviewCount) {
		int userReviewCount = 0;
		if (StringUtils.isNotBlank(reviewCount)) {
			userReviewCount = Integer.parseInt(reviewCount);
		}
		return userReviewCount;
	}

	protected String getBusinessName(String name) throws CitysearchException {
		String value = PropertiesLoader.getAPIProperties().getProperty(
				busNameMaxLengthProp);
		int length = busNameMaxLength;
		if (StringUtils.isNotBlank(value)) {
			try {
				length = Integer.parseInt(value);
			} catch (Exception excep) {
				String errMsg = PropertiesLoader.getErrorProperties()
						.getProperty(CommonConstants.ERROR_METHOD_PARAM)
						+ "getBusinessName()";
				log.error(errMsg, excep);
				throw new CitysearchException(this.getClass().getName(),
						"getBusinessName", excep.getMessage());
			}
		}
		name = StringUtils.abbreviate(name, length);
		return StringUtils.trimToEmpty(name);
	}

	protected String getTagLine(String tagLine) throws CitysearchException {
		String value = PropertiesLoader.getAPIProperties().getProperty(
				taglineMaxLengthProp);
		int length = tagLineMaxLength;
		if (StringUtils.isNotBlank(value)) {
			try {
				length = Integer.parseInt(value);
			} catch (Exception excep) {
				String errMsg = PropertiesLoader.getErrorProperties()
						.getProperty(CommonConstants.ERROR_METHOD_PARAM)
						+ "getTagLine()";
				log.error(errMsg, excep);
				throw new CitysearchException(this.getClass().getName(),
						"getTagLine", excep.getMessage());
			}
		}
		tagLine = StringUtils.abbreviate(tagLine, length);
		return StringUtils.trimToEmpty(tagLine);
	}

	private String getApiChildElementName(String apiType)
			throws CitysearchException {
		String childName;
		if (apiType.equalsIgnoreCase(CommonConstants.PFP_API_TYPE)) {
			childName = adTag;
		} else if (apiType.equalsIgnoreCase(CommonConstants.SEARCH_API_TYPE)) {
			childName = locationTag;
		} else {
			String errMsg = PropertiesLoader.getErrorProperties().getProperty(
					apiTypeError);
			log.error(errMsg);
			throw new CitysearchException(this.getClass().getName(),
					"getApiChildElementName", errMsg);
		}
		return childName;
	}

	protected String getLocation(String city, String state) {
		StringBuffer location = new StringBuffer();
		if (StringUtils.isNotBlank(city))
			location.append(city.trim());
		if (StringUtils.isNotBlank(state)) {
			if (location.length() > 0) {
				location.append(commaString);
				location.append(spaceString);
			}
			location.append(state.trim());
		}
		return location.toString();
	}

	protected double getDistance(BigDecimal sourceLat, BigDecimal sourceLon,
			BigDecimal destLat, BigDecimal destLon) {

		double distance = 0.0;
		double diffOfLat = Math.toRadians(destLat.doubleValue()
				- sourceLat.doubleValue());
		double diffOfLon = Math.toRadians(destLon.doubleValue()
				- sourceLon.doubleValue());
		double sourceLatRad = Math.toRadians(sourceLat.doubleValue());
		double destLatRad = Math.toRadians(destLat.doubleValue());

		double calcResult = Math.sin(diffOfLat / 2) * Math.sin(diffOfLat / 2)
				+ Math.cos(sourceLatRad) * Math.cos(destLatRad)
				* Math.sin(diffOfLon / 2) * Math.sin(diffOfLon / 2);

		calcResult = 2 * Math.atan2(Math.sqrt(calcResult), Math
				.sqrt(1 - calcResult));
		distance = radius * calcResult;
		// Converting from kms to Miles
		distance = distance * kmToMile;
		// Rounding to one decimal place
		distance = Math.floor(distance * 10) / 10.0;
		return distance;
	}
}
