package com.citysearch.webwidget.api.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;

import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.exception.InvalidHttpResponseException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HttpConnection;
import com.citysearch.webwidget.util.PropertiesLoader;
import com.citysearch.webwidget.util.Utils;

public class AbstractProxy {
	private static Logger log = Logger.getLogger(AbstractProxy.class);

	protected String constructQueryParam(String name, String value)
			throws CitysearchException {
		return Utils.constructQueryParam(name, value);
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
		return Utils.buildFromStream(input);
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

	protected String getQueryString(Map<String, String> parameters)
			throws CitysearchException {
		StringBuilder query = new StringBuilder();
		if (parameters != null && !parameters.isEmpty()) {
			int idx = 0;
			for (String key : parameters.keySet()) {
				if (idx > 0)
					query.append(CommonConstants.SYMBOL_AMPERSAND);
				query.append(constructQueryParam(key, parameters.get(key)));
				++idx;
			}
		}
		return query.toString();
	}

	protected String getQueryString(RequestBean request)
			throws CitysearchException {
		Map<String, String> parameters = new HashMap<String, String>();
		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		parameters.put(APIFieldNameConstants.API_KEY, apiKey);
		parameters.put(APIFieldNameConstants.PUBLISHER, request.getPublisher());
		parameters.put(APIFieldNameConstants.WHAT, request.getWhat());
		if (!StringUtils.isBlank(request.getLatitude())
				&& !StringUtils.isBlank(request.getLongitude())) {
			parameters.put(APIFieldNameConstants.LATITUDE, request
					.getLatitude());
			parameters.put(APIFieldNameConstants.LONGITUDE, request
					.getLongitude());
			String radius = (StringUtils.isBlank(request.getRadius())) ? String
					.valueOf(CommonConstants.DEFAULT_RADIUS) : request
					.getRadius();
			parameters.put(APIFieldNameConstants.RADIUS, radius);
		} else {
			parameters.put(APIFieldNameConstants.WHERE, request.getWhere());
		}
		parameters.put(APIFieldNameConstants.PLACEMENT, request
				.getPlacementString());
		return getQueryString(parameters);
	}

	protected String getWhereQueryString(RequestBean request)
			throws CitysearchException {
		Map<String, String> parameters = new HashMap<String, String>();
		Properties properties = PropertiesLoader.getAPIProperties();
		String apiKey = properties
				.getProperty(CommonConstants.API_KEY_PROPERTY);
		parameters.put(APIFieldNameConstants.API_KEY, apiKey);
		parameters.put(APIFieldNameConstants.PUBLISHER, request.getPublisher());
		parameters.put(APIFieldNameConstants.WHAT, request.getWhat());
		parameters.put(APIFieldNameConstants.WHERE, request.getWhere());
		parameters.put(APIFieldNameConstants.PLACEMENT, request
				.getPlacementString());
		return getQueryString(parameters);
	}
}
