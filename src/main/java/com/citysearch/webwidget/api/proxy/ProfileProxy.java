package com.citysearch.webwidget.api.proxy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.api.bean.LocationProfile;
import com.citysearch.webwidget.api.bean.ReviewResponse;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.exception.InvalidRequestParametersException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;
import com.citysearch.webwidget.util.Utils;

public class ProfileProxy extends AbstractProxy {
	private Logger log = Logger.getLogger(getClass());
	private final static String PROPERTY_PROFILE_URL = "profile.url";
	private static final String LSITING_ID_ERR_MSG = "listingid.errmsg";
	protected static final String LOCATION = "location";
	private static final String STREET = "street";
	private static final String CITY = "city";
	private static final String STATE = "state";
	private static final String POSTAL_CODE = "postal_code";
	private static final String ADDRESS = "address";
	private static final String CONTACT_INFO = "contact_info";
	private static final String PHONE = "display_phone";
	private static final String URLS = "urls";
	private static final String PROFILE_URL = "profile_url";
	private static final String SEND_TO_FRIEND_URL = "send_to_friend_url";
	private static final String IMAGES = "images";
	private static final String IMAGE = "image";
	private static final String IMAGE_URL = "image_url";
	private static final String IMAGE_PROPERTIES_FILE = "review.image.properties";
	private static final String COMMA_STRING = ",";
	private static HashMap<String, List<String>> imageMap;
	private static final String CATEGORIES = "categories";
	private static final String CATEGORY = "category";
	private static final String CATEGORY_NAME = "name";
	private static final String ID = "id";

	private static final String REVIEWS_URL = "reviews_url";
	private static final String WEBSITE_URL = "website_url";
	private static final String MENU_URL = "menu_url";
	private static final String RESERVATION_URL = "reservation_url";
	private static final String MAP_URL = "map_url";

	private static final String DATE_FORMAT = "reviewdate.format";

	private static final String TOTAL_USER_REVIEWS = "total_user_reviews";
	private static final String REVIEWS = "reviews";

	private void validateRequest(RequestBean request)
			throws CitysearchException {
		List<String> errors = new ArrayList<String>();
		if (StringUtils.isBlank(request.getPublisher())) {
			errors.add("Publisher is required.");
		}
		if (StringUtils.isBlank(request.getListingId())) {
			errors.add("Listing is required.");
		}
		if (StringUtils.isBlank(request.getClientIP())) {
			errors.add("Client IP is required.");
		}
		if (!errors.isEmpty()) {
			throw new InvalidRequestParametersException(this.getClass()
					.getName(), "validateRequest", "Invalid parameters.",
					errors);
		}
	}

	/**
	 * Constructs the Profile API query string with all the supplied parameters
	 * and returns query string
	 * 
	 * @param request
	 * @return String
	 * @throws CitysearchException
	 */
	private String getProfileQueryString(RequestBean request)
			throws CitysearchException {
		Map<String, String> parameters = new HashMap<String, String>();
		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		parameters.put(APIFieldNameConstants.API_KEY, apiKey);
		parameters.put(APIFieldNameConstants.PUBLISHER, request.getPublisher());
		parameters
				.put(APIFieldNameConstants.LISTING_ID, request.getListingId());
		parameters.put(APIFieldNameConstants.CLIENT_IP, request.getClientIP());
		parameters.put(APIFieldNameConstants.PLACEMENT, request
				.getPlacementString());
		return getQueryString(parameters);
	}

	/**
	 * Reads the images from a properties file, adds them to a map The
	 * properties file contains properties of the format key=category,imageurl
	 * Each category is added as a key in the Map and imageurls are added to the
	 * list and set as value in the Map The Map contains <key,value> =
	 * <category,list of image urls>
	 * 
	 * @param contextPath
	 * @return ArrayList
	 * @throws CitysearchException
	 */
	private void getImageMap() throws CitysearchException {
		List<String> imageList;
		Properties imageProperties = null;
		if (imageProperties == null) {
			imageProperties = PropertiesLoader
					.getProperties(IMAGE_PROPERTIES_FILE);
		}

		Set<String> imageKeySet;
		imageMap = new HashMap<String, List<String>>();
		Enumeration<Object> enumerator = imageProperties.keys();
		while (enumerator.hasMoreElements()) {
			String key = (String) enumerator.nextElement();
			String value = imageProperties.getProperty(key);
			String values[] = value.split(COMMA_STRING);
			if (StringUtils.isNotBlank(values[0])
					&& StringUtils.isNotBlank(values[1])) {
				imageKeySet = imageMap.keySet();
				if (imageKeySet.contains(values[0])) {
					imageList = imageMap.get(values[0]);
				} else {
					imageList = new ArrayList<String>();
				}
				imageList.add(values[1]);
				imageMap.put(values[0], imageList);
			}
		}

	}

	/**
	 * Returns the stock image url, if no image url is returned by API response
	 * Parses the categories element and checks if the any of the category child
	 * elements matches with the categories present in the "imageMap" If
	 * present, picks up the imageurl randomly from the list of images for that
	 * category from the "imageMap" object
	 * 
	 * @param categories
	 * @return imageurl
	 * @throws CitysearchException
	 */
	private String getStockImage(Element categories, String contextPath)
			throws CitysearchException {
		if (imageMap == null) {
			getImageMap();
		}
		String imageURL = null;
		if (categories != null && imageMap != null) {
			List<Element> categoryList = categories.getChildren(CATEGORY);
			int size = categoryList.size();
			Set<String> imageKeySet = imageMap.keySet();
			Random randomizer = new Random();
			for (int index = 0; index < size; index++) {
				Element category = categoryList.get(index);
				if (category != null) {
					String name = category.getAttributeValue(CATEGORY_NAME);
					if (StringUtils.isNotBlank(name)
							&& imageKeySet.contains(name)) {
						List<String> imageList = imageMap.get(name);
						int listSize = imageList.size();
						int imgIndex = randomizer.nextInt(listSize);
						StringBuilder imageUrlBuilder = new StringBuilder();
						if (!StringUtils.isBlank(contextPath)) {
							imageUrlBuilder.append(contextPath);
						}
						imageUrlBuilder.append(imageList.get(imgIndex));
						imageURL = imageUrlBuilder.toString();
						break;
					}
				}
			}
		}
		return imageURL;
	}

	/**
	 * Gets the image url from xml. If no image url is found, returns the stock
	 * image related to the business category
	 * 
	 * @param images
	 * @return String
	 * @throws CitysearchException
	 */
	private String getImage(Element images, Element categories,
			String contextPath) throws CitysearchException {
		String imageurl = null;
		if (images != null) {
			List<Element> imageList = images.getChildren(IMAGE);
			int size = imageList.size();
			for (int index = 0; index < size; index++) {
				Element image = imageList.get(index);
				if (image != null) {
					imageurl = image.getChildText(IMAGE_URL);
					if (StringUtils.isNotBlank(imageurl)) {
						break;
					}
				}
			}
		}

		if (StringUtils.isBlank(imageurl)) {
			imageurl = getStockImage(categories, contextPath);
		}

		return imageurl;
	}

	private LocationProfile parseToProfile(Document document, String contextPath)
			throws CitysearchException {
		LocationProfile response = null;
		if (document != null && document.hasRootElement()) {
			Element locationElem = document.getRootElement().getChild(LOCATION);
			if (locationElem != null) {
				response = new LocationProfile();

				Element addressElem = locationElem.getChild(ADDRESS);
				if (addressElem != null) {
					response.setStreet(addressElem.getChildText(STREET));
					response.setCity(addressElem.getChildText(CITY));
					response.setState(addressElem.getChildText(STATE));
					response.setPostalCode(addressElem
							.getChildText(POSTAL_CODE));
				}

				Element contactInfo = locationElem.getChild(CONTACT_INFO);
				if (contactInfo != null) {
					response.setPhone(contactInfo.getChildText(PHONE));
				}

				Element urlElm = locationElem.getChild(URLS);
				if (urlElm != null) {
					response.setProfileUrl(urlElm.getChildText(PROFILE_URL));
					response.setSendToFriendUrl(urlElm
							.getChildText(SEND_TO_FRIEND_URL));
					response.setReviewsUrl(urlElm.getChildText(REVIEWS_URL));
					response.setWebsiteUrl(urlElm.getChildText(WEBSITE_URL));
					response.setMenuUrl(urlElm.getChildText(MENU_URL));
					response.setReservationUrl(urlElm
							.getChildText(RESERVATION_URL));
					response.setMapUrl(urlElm.getChildText(MAP_URL));
				}

				Element review = locationElem.getChild(REVIEWS);
				if (review != null) {
					response.setReviewCount(review
							.getChildText(TOTAL_USER_REVIEWS));
				}
				response.setImageUrl(getImage(locationElem.getChild(IMAGES),
						locationElem.getChild(CATEGORIES), contextPath));
			}
		}
		return response;
	}

	private LocationProfile parseToLatestReview(Document document,
			String contextPath) throws CitysearchException {
		LocationProfile response = null;
		if (document != null && document.hasRootElement()) {
			Element locationElm = document.getRootElement().getChild(LOCATION);
			if (locationElm != null) {
				response = parseToProfile(document, contextPath);
				response.setListingId(locationElm.getChildText(ID));
				Element reviewsElm = locationElm.getChild("reviews");
				List<Element> reviews = reviewsElm.getChildren("review");
				SortedMap<Date, Element> reviewMap = new TreeMap<Date, Element>();
				if (reviews != null && !reviews.isEmpty()) {
					SimpleDateFormat formatter = new SimpleDateFormat(
							PropertiesLoader.getAPIProperties().getProperty(
									DATE_FORMAT));
					for (Element reviewElm : reviews) {
						String dateStr = reviewElm.getChildText("review_date");
						Date date = Utils.parseDate(dateStr, formatter);
						if (date != null) {
							reviewMap.put(date, reviewElm);
						}
					}
					Element reviewElem = reviewMap.get(reviewMap.lastKey());
					ReviewResponse reviewResponse = ReviewProxy
							.toReviewResponse(reviewElem);
					response.setReview(reviewResponse);
				}
			}
		}
		return response;
	}

	public LocationProfile getProfile(RequestBean request, String contextPath)
			throws InvalidRequestParametersException, CitysearchException {
		validateRequest(request);
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_PROFILE_URL)
				+ getProfileQueryString(request);
		log.info(urlString);
		Document responseDocument = null;
		try {
			responseDocument = getAPIResponse(urlString, null);
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getProfile", ihe);
		}
		return parseToProfile(responseDocument, contextPath);
	}

	public LocationProfile getProfileAndLatestReview(RequestBean request,
			String contextPath) throws InvalidRequestParametersException,
			CitysearchException {
		validateRequest(request);
		Properties properties = PropertiesLoader.getAPIProperties();
		String urlString = properties.getProperty(PROPERTY_PROFILE_URL)
				+ getProfileQueryString(request);
		log.info(urlString);
		Document responseDocument = null;
		try {
			responseDocument = getAPIResponse(urlString, null);
		} catch (InvalidHttpResponseException ihe) {
			throw new CitysearchException(this.getClass().getName(),
					"getProfileAndLatestReview", ihe);
		}
		return parseToLatestReview(responseDocument, contextPath);
	}
}
