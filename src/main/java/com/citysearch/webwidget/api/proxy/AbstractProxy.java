package com.citysearch.webwidget.api.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.HttpConnection;
import com.citysearch.webwidget.util.PropertiesLoader;

public class AbstractProxy {
	private static Logger log = Logger.getLogger(AbstractProxy.class);
	
	
	protected String constructQueryParam(String name, String value)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();
		if (StringUtils.isNotBlank(value)) {
			apiQueryString.append(name);
			apiQueryString.append("=");
			try {
				value = URLEncoder.encode(value, "UTF-8");
				apiQueryString.append(value);
			} catch (UnsupportedEncodingException excep) {
				throw new CitysearchException("AbstractProxy",
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
	protected Document buildFromStream(InputStream input) throws IOException,
			CitysearchException {
		Document document = null;
		try {
			if (input != null) {
				SAXBuilder builder = new SAXBuilder();
				document = builder.build(input);
			}
		} catch (JDOMException jde) {
			throw new CitysearchException("AbstractProxy", "buildFromStream",
					jde);
		} catch (IOException ioe) {
			throw new CitysearchException("AbstractProxy", "buildFromStream",
					ioe);
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
	protected Document getAPIResponse(String url, Map<String, String> headers)
			throws CitysearchException, InvalidHttpResponseException {
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
			throw new CitysearchException("AbstractProxy", "getAPIResponse",
					ioe);
		} finally {
			if (connection != null) {
				HttpConnection.closeConnection(connection);
			}
		}
		return xmlDocument;
	}

	protected String getQueryString(RequestBean request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();

		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, request.getWhat()));

		if (!StringUtils.isBlank(request.getLatitude())
				&& !StringUtils.isBlank(request.getLongitude())) {
			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.LATITUDE, request.getLatitude()));

			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.LONGITUDE, request.getLongitude()));

			String radius = (StringUtils.isBlank(request.getRadius())) ? String
					.valueOf(CommonConstants.DEFAULT_RADIUS) : request
					.getRadius();
			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.RADIUS, radius));
		} else {
			apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
			apiQueryString.append(HelperUtil.constructQueryParam(
					APIFieldNameConstants.WHERE, request.getWhere()));
		}
		return apiQueryString.toString();
	}

	protected String getLatLonQueryString(RequestBean request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();

		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, request.getWhat()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.LATITUDE, request.getLatitude()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.LONGITUDE, request.getLongitude()));

		String radius = (StringUtils.isBlank(request.getRadius())) ? String
				.valueOf(CommonConstants.DEFAULT_RADIUS) : request.getRadius();
		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.RADIUS, radius));

		return apiQueryString.toString();
	}

	protected String getWhereQueryString(RequestBean request)
			throws CitysearchException {
		StringBuilder apiQueryString = new StringBuilder();

		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.API_KEY, apiKey));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.PUBLISHER, request.getPublisher()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHAT, request.getWhat()));

		apiQueryString.append(CommonConstants.SYMBOL_AMPERSAND);
		apiQueryString.append(HelperUtil.constructQueryParam(
				APIFieldNameConstants.WHERE, request.getWhere()));

		return apiQueryString.toString();
	}
}
