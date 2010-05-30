package com.citysearch.webwidget.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Address;
import com.citysearch.webwidget.bean.Profile;
import com.citysearch.webwidget.bean.ProfileRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class ProfileHelper {

	private final static String property_profile_url = "profile.url";
	private static final String listingid_err_msg = "listingid.errmsg";
	protected static final String location = "location";
	private static final String street = "street";
	private static final String city = "city";
	private static final String state = "state";
	private static final String postalCode = "postal_code";
	private static final String address = "address";
	private static final String contactInfo = "contact_info";
	private static final String phone = "display_phone";
	private static final String urls = "urls";
	private static final String profileURL = "profile_url";
	private static final String sendToFriendURL = "send_to_friend_url";
	private static final String images = "images";
	private static final String image = "image";
	private static final String imageURL = "image_url";
	private static List<String> imageList;
	private static final String imagePropertiesFile = "review.image.properties";

	/**
	 * Validates the request. If any of the parameters are missing, throws
	 * Citysearch Exception
	 * 
	 * @throws CitysearchException
	 */
	public void validateRequest(ProfileRequest request)
			throws CitysearchException {
		List<String> errors = new ArrayList<String>();
		Properties errorProperties = PropertiesLoader.getErrorProperties();
		if (StringUtils.isBlank(request.getApiKey())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.API_KEY_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getPublisher())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
		}
		if (StringUtils.isBlank(request.getListingId())) {
			errors.add(errorProperties.getProperty(listingid_err_msg));
		}
		if (StringUtils.isBlank(request.getClientIP())) {
			errors.add(errorProperties
					.getProperty(CommonConstants.CLIENT_IP_ERROR_CODE));
		}
		if (!errors.isEmpty()) {
			throw new CitysearchException(this.getClass().getName(),
					"validateRequest", "Invalid parameters.", errors);
		}
	}

	/**
	 * Constructs the Profile API query string with all the supplied parameters
	 * 
	 * @return
	 * @throws CitysearchException
	 */
	private String getQueryString(ProfileRequest request)
			throws CitysearchException {
		// Reflection Probably???
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, request.getApiKey()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.LISTING_ID, request.getListingId()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.INFOUSA_ID, request.getInfoUSAId()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PHONE, request.getPhone()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.ALL_RESULTS, String.valueOf(request
						.isAllResults())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.CUSTOMER_ONLY, String.valueOf(request
						.isCustomerOnly())));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.REVIEW_COUNT, request.getReviewCount()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.CLIENT_IP, request.getClientIP()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.CALLBACK, request.getCallback()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PLACEMENT, request.getPlacement()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.FORMAT, request.getFormat()));
		strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
		strBuilder.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.NO_LOG, String
						.valueOf(request.getNolog())));
		return strBuilder.toString();
	}

	/**
	 * Connects to the Profile API and processes the response sent by API for
	 * Reviews Response
	 * 
	 * @return
	 * @throws CitysearchException
	 */
	public Profile getProfile(ProfileRequest request)
			throws CitysearchException {
		validateRequest(request);
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(property_profile_url)
				+ getQueryString(request);
		Document responseDocument = null;
		try {
			responseDocument = HelperUtil.getAPIResponse(urlString);
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getProfile", ihe.getMessage());
		}
		Profile profile = parseProfileForReviews(responseDocument);
		return profile;
	}

	/**
	 * Parses xml response and returns the Reviews object
	 * 
	 * @param doc
	 * @param review
	 * @return
	 * @throws CitysearchException
	 */
	public Profile parseProfileForReviews(Document doc)
			throws CitysearchException {
		Profile profile = null;
		if (doc != null && doc.hasRootElement()) {
			Element locationElem = doc.getRootElement().getChild(location);
			if (locationElem != null) {
				profile = new Profile();
				profile.setAddress(getAddress(locationElem.getChild(address)));
				profile.setPhone(getPhone(locationElem.getChild(contactInfo)));
				Element url = locationElem.getChild(urls);
				if (url != null) {
					profile.setProfileUrl(url.getChildText(profileURL));
					profile.setSendToFriendUrl(url
							.getChildText(sendToFriendURL));
				}
				profile.setImageUrl(getImage(locationElem.getChild(images)));
			}
		}
		return profile;
	}

	/**
	 * Parses the address element received in response
	 * 
	 * @param addressElem
	 * @return
	 * @throws CitysearchException
	 */
	private Address getAddress(Element addressElem) throws CitysearchException {
		Address address = null;
		if (addressElem != null) {
			address = new Address();
			address.setStreet(addressElem.getChildText(street));
			address.setCity(addressElem.getChildText(city));
			address.setState(addressElem.getChildText(state));
			address.setPostalCode(addressElem.getChildText(postalCode));
		}
		return address;
	}

	/**
	 * Gets the phone number from contact info element
	 * 
	 * @param contactInfo
	 * @return
	 */
	private String getPhone(Element contactInfo) {
		if (contactInfo != null) {
			return contactInfo.getChildText(phone);
		}
		return null;
	}

	/**
	 * Gets the image url from xml. If no image url is found, returns the stock
	 * image related to the business category
	 * 
	 * @param images
	 * @return
	 * @throws CitysearchException
	 */
	private String getImage(Element images) throws CitysearchException {
		String imageurl = null;
		if (images != null) {
			List<Element> imageList = images.getChildren(image);
			int size = imageList.size();
			for (int index = 0; index < size; index++) {
				Element image = imageList.get(index);
				if (image != null) {
					imageurl = image.getChildText(imageURL);
					if (StringUtils.isNotBlank(imageurl)) {
						break;
					}
				}
			}
		}
		/*
		 * if (StringUtils.isBlank(imageurl)) { if (imageList == null) {
		 * imageList = getImageList(imagePropertiesFile); } if (imageList !=
		 * null) { int listSize = imageList.size(); int index = new
		 * Random().nextInt(listSize); imageurl = imageList.get(index); } }
		 */
		return imageurl;
	}
}
