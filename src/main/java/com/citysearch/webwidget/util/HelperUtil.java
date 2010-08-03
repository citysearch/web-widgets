package com.citysearch.webwidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;

/**
 * Helper class that contains generic methods used across all APIs
 * 
 * @author Aspert Benjamin
 * 
 */
public class HelperUtil {

	private static Logger log = Logger.getLogger(HelperUtil.class);

	private static final int TOTAL_RATING = 5;
	private static final int EMPTY_STAR = 0;
	private static final int HALF_STAR = 1;
	private static final int FULL_STAR = 2;
	private static final double KM_TO_MILE = 0.622;
	private static final int RADIUS = 6371;
	private static final String COMMA = ",";
	private static final String SPACE = " ";

	/**
	 * Helper method to build a string in name=value format. Used in building
	 * http query string.
	 * 
	 * @param name
	 * @param value
	 * @return String
	 * @throws CitysearchException
	 */
	public static String constructQueryParam(String name, String value)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();
		if (StringUtils.isNotBlank(value)) {
			apiQueryString.append(name);
			apiQueryString.append("=");
			try {
				value = URLEncoder.encode(value, "UTF-8");
				apiQueryString.append(value);
			} catch (UnsupportedEncodingException excep) {
				throw new CitysearchException("HelperUtil",
						"constructQueryParam", excep);
			}
		}
		return apiQueryString.toString();
	}

	/**
	 * Converts the InputSteam to a document and returns it
	 * 
	 * @param input
	 * @return Document
	 * @throws IOException
	 * @throws CitysearchException
	 */
	public static Document buildFromStream(InputStream input)
			throws IOException, CitysearchException {
		Document document = null;
		try {
			if (input != null) {
				SAXBuilder builder = new SAXBuilder();
				document = builder.build(input);
			}
		} catch (JDOMException jde) {
			throw new CitysearchException("HelperUtil", "buildFromStream", jde);
		} catch (IOException ioe) {
			throw new CitysearchException("HelperUtil", "buildFromStream", ioe);
		} finally {
			input.close();
		}
		return document;
	}

	/**
	 * Connects to the url using HttpConnection. In case of error returns
	 * InvalidHttpResponseException otherwise converts the response to
	 * org.jdom.Document and returns it
	 * 
	 * @param url
	 * @return Document
	 * @throws CitysearchException
	 * @throws InvalidHttpResponseException
	 */
	public static Document getAPIResponse(String url,
			Map<String, String> headers) throws CitysearchException,
			InvalidHttpResponseException {
		HttpURLConnection connection = null;
		Document xmlDocument = null;
		try {
			connection = HttpConnection.getConnection(url, headers);
			if (connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE) {
				InputStream is = connection.getInputStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				String str = writer.toString();
				log
						.error("******************* API ERROR ************************");
				log.error("URL: " + url);
				log.error("Response XML: " + str);
				log
						.error("******************* END API ERROR ************************");
				throw new InvalidHttpResponseException(connection
						.getResponseCode(), "Invalid HTTP Status Code.");
			}
			InputStream iStream = connection.getInputStream();
			xmlDocument = buildFromStream(iStream);
		} catch (IOException ioe) {
			throw new CitysearchException("HelperUtil", "getAPIResponse", ioe);
		} finally {
			if (connection != null) {
				HttpConnection.closeConnection(connection);
			}
		}
		return xmlDocument;
	}

	/**
	 * Parses the dateStr to Date object as per the formatter format
	 * 
	 * @param dateStr
	 * @param formatter
	 * @return Date
	 * @throws CitysearchException
	 */
	public static Date parseDate(String dateStr, SimpleDateFormat formatter)
			throws CitysearchException {
		Date date = null;
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException excep) {
			throw new CitysearchException("HelperUtil", "parseDate", excep);
		}
		return date;
	}

	/**
	 * Calculate the ratings value and determines the rating stars to be
	 * displayed Returns what type of star to be displayed in an array E.g.for
	 * 3.5 rating the array will have values {2,2,2,1,0} where 2 represents full
	 * star, 1 half star and 0 empty star
	 * 
	 * @param rating
	 * @return
	 */
	public static List<Integer> getRatingsList(String rating) {
		List<Integer> ratingList = new ArrayList<Integer>();
		if (StringUtils.isNotBlank(rating)) {
			double ratings = (Double.parseDouble(rating)) / 2;
			int userRating = (int) ratings;
			while (ratingList.size() < userRating) {
				ratingList.add(FULL_STAR);
			}

			if (ratings % 1 != 0)
				ratingList.add(HALF_STAR);

			while (ratingList.size() < TOTAL_RATING) {
				ratingList.add(EMPTY_STAR);
			}

		} else {
			for (int count = 0; count < TOTAL_RATING; count++) {
				ratingList.add(EMPTY_STAR);
			}
		}
		return ratingList;
	}

	/**
	 * This method takes the source latitude, longitude and destination
	 * latitude, longitude to calculate the distance between two points and
	 * returns the distance
	 * 
	 * @param sourceLat
	 * @param sourceLon
	 * @param destLat
	 * @param destLon
	 * @return double
	 */
	public static double getDistance(BigDecimal sourceLatitude,
			BigDecimal sourceLongitude, BigDecimal destLatitude,
			BigDecimal destLongitude) {

		double distance = 0.0;
		double diffOfLat = Math.toRadians(destLatitude.doubleValue()
				- sourceLatitude.doubleValue());
		double diffOfLon = Math.toRadians(destLongitude.doubleValue()
				- sourceLongitude.doubleValue());
		double sourceLatRad = Math.toRadians(sourceLatitude.doubleValue());
		double destLatRad = Math.toRadians(destLatitude.doubleValue());

		double calcResult = Math.sin(diffOfLat / 2) * Math.sin(diffOfLat / 2)
				+ Math.cos(sourceLatRad) * Math.cos(destLatRad)
				* Math.sin(diffOfLon / 2) * Math.sin(diffOfLon / 2);

		calcResult = 2 * Math.atan2(Math.sqrt(calcResult), Math
				.sqrt(1 - calcResult));
		distance = RADIUS * calcResult;
		// Converting from kms to Miles
		distance = distance * KM_TO_MILE;
		// Rounding to one decimal place
		distance = Math.floor(distance * 10) / 10.0;
		return distance;
	}

	public static double getRatingValue(String rating) {
		double ratings = 0.0;
		if (StringUtils.isNotBlank(rating)) {
			ratings = (Double.parseDouble(rating)) / 2;
			ratings = Math.floor(ratings * 10) / 10.0;
		}
		return ratings;

	}

	public static int toInteger(String stringToConvert) {
		int intValue = 0;
		if (StringUtils.isNotBlank(stringToConvert)) {
			intValue = Integer.parseInt(stringToConvert);
		}
		return intValue;
	}

	public static String getLocationString(String city, String state) {
		StringBuilder location = new StringBuilder();
		if (StringUtils.isNotBlank(city))
			location.append(city.trim());
		if (StringUtils.isNotBlank(state)) {
			if (location.length() > 0) {
				location.append(COMMA);
				location.append(SPACE);
			}
			location.append(state.trim());
		}
		return location.toString();
	}

	public static String getAbbreviatedString(String stringToAbbreviate,
			String apiPropertyName, int defaultLength)
			throws CitysearchException {
		String value = PropertiesLoader.getAPIProperties().getProperty(
				apiPropertyName);
		int length = 0;
		if (StringUtils.isNotBlank(value)) {
			length = NumberUtils.toInt(value);
		}
		if (length == 0) {
			length = defaultLength;
		}
		String abbreviatedString = StringUtils.abbreviate(stringToAbbreviate,
				length);
		return StringUtils.trimToEmpty(abbreviatedString);
	}

	public static String getAbbreviatedString(String stringToAbbreviate,
			String propertyName) throws CitysearchException {
		Properties prop = PropertiesLoader.getFieldProperties();
		if (prop.containsKey(propertyName)) {
			String value = prop.getProperty(propertyName);
			if (StringUtils.isNotBlank(value)) {
				int length = NumberUtils.toInt(value);
				stringToAbbreviate = StringUtils.abbreviate(stringToAbbreviate,
						length);
			}
		}
		return stringToAbbreviate;
	}

	public static List<String> getImages(String contextPath)
			throws CitysearchException {
		List<String> imageList = new ArrayList<String>();
		Properties imageProperties = PropertiesLoader
				.getProperties(CommonConstants.IMAGES_PROPERTIES_FILE);
		Enumeration<Object> enumerator = imageProperties.keys();
		while (enumerator.hasMoreElements()) {
			String key = (String) enumerator.nextElement();
			String value = imageProperties.getProperty(key);
			if (value != null && !value.startsWith("http")) {
				StringBuilder strBuilder = new StringBuilder(contextPath);
				strBuilder.append(value);
				value = strBuilder.toString();
			}
			imageList.add(value);
		}
		return imageList;
	}

	public static String getCallBackFunctionString(String callBackFunction,
			String listingId, String phone) {
		if (callBackFunction != null && callBackFunction.trim().length() > 0) {
			// Should produce javascript:fnName('param1','param2')
			StringBuilder strBuilder = new StringBuilder("javascript:");
			strBuilder.append(callBackFunction);
			strBuilder.append("(\"");
			strBuilder.append(listingId);
			strBuilder.append("\",\"");
			strBuilder.append(phone);
			strBuilder.append("\")");
			return strBuilder.toString();
		}
		return "";
	}

	public static String getTrackingUrl(String adDisplayURL,
			String adDestinationUrl, String callBackUrl,
			String dartTrackingUrl, String listingId, String phone,
			String publisher, String adUnitName, String adUnitSize)
			throws CitysearchException {
		String urlToTrack = adDisplayURL;
		// takes care of callBackUrl
		if (callBackUrl != null && callBackUrl.trim().length() > 0) {
			callBackUrl = callBackUrl.replace("$l", listingId);
			callBackUrl = callBackUrl.replace("$p", phone);
			urlToTrack = callBackUrl;
		}
		// adds http:// if it does not specify one
		if (!urlToTrack.startsWith("http://")) {
			StringBuilder strb = new StringBuilder("http://");
			strb.append(urlToTrack);
			urlToTrack = strb.toString();
		}
		try {
			// get prod destination id
			URL url = new URL(urlToTrack);
			String host = url.getHost();
			int prodDetId = 12; // Click outside Citysearch
			if (host.indexOf("citysearch.com") != -1) {
				prodDetId = 16;
			}

			StringBuilder strBuilder = new StringBuilder();
			// dart tracking goes first
			if (dartTrackingUrl != null) {
				strBuilder.append(dartTrackingUrl);
			}

			String destinationUrl = null;
			if (adDestinationUrl != null
					&& adDestinationUrl.trim().length() > 0) {
				// in house click tracker goes next
				StringBuilder destinationUrlBuilder = new StringBuilder();
				destinationUrlBuilder.append(adDestinationUrl);
				destinationUrlBuilder.append("&placement=");
				destinationUrlBuilder.append(publisher + "_" + adUnitName + "_"
						+ adUnitSize);
				if (adDestinationUrl.indexOf("&directUrl=") == -1) {
					destinationUrlBuilder.append("&directUrl=");
					destinationUrlBuilder.append(URLEncoder.encode(urlToTrack,
							"UTF-8"));
				}
				if (prodDetId != 16) {
					destinationUrl = destinationUrlBuilder.toString()
							.replaceAll("prodDetId=16",
									"prodDetId=" + prodDetId);
				} else {
					destinationUrl = destinationUrlBuilder.toString();
				}
			} else {
				destinationUrl = urlToTrack;
			}
			strBuilder.append(destinationUrl);

			return strBuilder.toString();
		} catch (MalformedURLException mue) {
			throw new CitysearchException("HelperUtil", "getTrackingUrl", mue);
		} catch (UnsupportedEncodingException excep) {
			throw new CitysearchException("HelperUtil", "getTrackingUrl", excep);
		}
	}

	public static String parseRadius(String radiusStr) {
		try {
			Float r = Float.parseFloat(radiusStr);
			int radius = Math.round(r);
			radius = (radius < 1 || radius > CommonConstants.DEFAULT_RADIUS) ? CommonConstants.DEFAULT_RADIUS
					: radius;
			return String.valueOf(radius);
		} catch (NumberFormatException nfe) {
			log.error("Unable to parse radius to Float: " + radiusStr);
			// DO not throw an exception here.
		}
		return String.valueOf(CommonConstants.DEFAULT_RADIUS);
	}

	/**
	 * Parses a plain 3101231234 into (310) 123-1234
	 */
	public static String parsePhone(String phoneStr) {
		String areaCode = phoneStr.substring(0, 3);
		String prefix = phoneStr.substring(3, 6);
		String number = phoneStr.substring(6, 10);

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append('(');
		strBuilder.append(areaCode);
		strBuilder.append(") ");
		strBuilder.append(prefix);
		strBuilder.append('-');
		strBuilder.append(number);

		return strBuilder.toString();
	}
}