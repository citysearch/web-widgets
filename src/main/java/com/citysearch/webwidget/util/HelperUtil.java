package com.citysearch.webwidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;

public class HelperUtil {

	private static Logger log = Logger.getLogger(HelperUtil.class);

	private static final String imageError = "image.properties.error";
	private static final int totalRating = 5;
	private static final int emptyStar = 0;
	private static final int halfStar = 1;
	private static final int fullStar = 2;

	/**
	 * Helper method to build a string in name=value format. Used in building
	 * http query string.
	 * 
	 * @param name
	 * @param value
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
						"constructQueryParam", excep.getMessage());
			}
		}
		return apiQueryString.toString();
	}

	public static Document buildFromStream(InputStream input)
			throws IOException, CitysearchException {
		Document document = null;
		try {
			if (input != null) {
				SAXBuilder builder = new SAXBuilder();
				document = builder.build(input);
			}
		} catch (JDOMException jde) {
			throw new CitysearchException("HelperUtil", "buildFromStream", jde
					.getMessage());
		} catch (IOException ioe) {
			throw new CitysearchException("HelperUtil", "buildFromStream", ioe
					.getMessage());
		} finally {
			input.close();
		}
		return document;
	}

	public static Document getAPIResponse(String url)
			throws CitysearchException, InvalidHttpResponseException {
		HttpURLConnection connection = null;
		Document xmlDocument = null;
		try {
			connection = HttpConnection.getConnection(url);
			if (connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE) {
				throw new InvalidHttpResponseException(connection
						.getResponseCode(), "Invalid HTTP Status Code.");
			}
			InputStream iStream = connection.getInputStream();
			xmlDocument = buildFromStream(iStream);
		} catch (IOException ioe) {
			throw new CitysearchException("HelperUtil", "getAPIResponse", ioe
					.getMessage());
		} finally {
			if (connection != null) {
				HttpConnection.closeConnection(connection);
			}
		}
		return xmlDocument;
	}

	/**
	 * Parses the Review Element and creates value object
	 * 
	 * @param reviewElem
	 * @throws CitysearchException
	 */
	public static Date parseDate(String dateStr, SimpleDateFormat formatter)
			throws CitysearchException {
		Date date = null;
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException excep) {
			throw new CitysearchException("HelperUtil", "parseDate", excep
					.getMessage());
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
	public static int[] getRatingsList(String rating) {
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

	public static ArrayList<String> getImageList(String imageProprtiesFile) {
		ArrayList<String> imageList = new ArrayList<String>();
		Properties imageProperties = PropertiesLoader
				.getProperties(imageProprtiesFile);
		Enumeration<Object> enumerator = imageProperties.keys();
		while (enumerator.hasMoreElements()) {
			String key = (String) enumerator.nextElement();
			String value = imageProperties.getProperty(key);
			// imageList.add(contextPath + "/" + value);
			imageList.add(value);
		}
		return imageList;
	}
}
