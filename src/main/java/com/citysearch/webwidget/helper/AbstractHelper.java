package com.citysearch.webwidget.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.HttpConnection;

public class AbstractHelper {
	/**
	 * Helper method to build a string in name=value format. Used in building
	 * http query string.
	 * 
	 * @param name
	 * @param value
	 * @throws CitysearchException
	 */
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
				throw new CitysearchException(this.getClass().getName(),
						"constructQueryParam", excep.getMessage());
			}
		}
		return apiQueryString.toString();
	}

	private Document buildFromStream(InputStream input) throws IOException,
			CitysearchException {
		Document document = null;
		try {
			if (input != null) {
				SAXBuilder builder = new SAXBuilder();
				document = builder.build(input);
			}
		} catch (JDOMException jde) {
			throw new CitysearchException(this.getClass().getName(),
					"buildFromStream", jde.getMessage());
		} catch (IOException ioe) {
			throw new CitysearchException(this.getClass().getName(),
					"buildFromStream", ioe.getMessage());
		} finally {
			input.close();
		}
		return document;
	}

	protected Document getAPIResponse(String url) throws CitysearchException {
		HttpURLConnection connection = null;
		Document xmlDocument = null;
		try {
			connection = HttpConnection.getConnection(url);
			InputStream iStream = connection.getInputStream();
			xmlDocument = buildFromStream(iStream);
		} catch (IOException ioe) {
			throw new CitysearchException(this.getClass().getName(),
					"getResponse", ioe.getMessage());
		} finally {
			if (connection != null) {
				HttpConnection.closeConnection(connection);
			}
		}
		return xmlDocument;
	}
}
