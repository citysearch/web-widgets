package com.citysearch.webwidget.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.helper.RequestHelper;
import com.citysearch.webwidget.helper.SearchResponseHelper;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HttpConnection;
import com.citysearch.webwidget.util.PropertiesLoader;

public class SearchQueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(getClass());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			processRequest(req, res);
		} catch (CitysearchException cse) {
			throw new ServletException(cse);
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			processRequest(req, res);
		} catch (CitysearchException cse) {
			throw new ServletException(cse);
		}
	}

	/**
	 * Constructs the query string, queries Search api and returns latitude and
	 * longitude values
	 * 
	 * @param req
	 * @param res
	 * @throws CitysearchException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest req, HttpServletResponse res)
			throws CitysearchException, IOException {
		Map<String, String[]> paramMap = req.getParameterMap();
		res.setContentType(CommonConstants.RES_CONTENT_TYPE);
		RequestHelper reqHelper = new RequestHelper(paramMap);
		HttpURLConnection connection = null;
		InputStream input = null;
		try {
			String queryString = reqHelper
					.getQueryString(CommonConstants.SEARCH_API_TYPE);
			log.info(queryString);
			connection = HttpConnection.getConnection(queryString);
			if (connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE) {
				input = connection.getErrorStream();
				if (input != null) {
					SearchResponseHelper responseHelper = new SearchResponseHelper();
					String errMsg = responseHelper.getStringFromStream(input);
					log.error(errMsg);
					throw new CitysearchException(this.getClass().getName(),
							"processRequest", errMsg);
				}
			} else {
				// read the result from the server
				input = connection.getInputStream();
				if (input != null) {
					processResponse(input, req);
				}
			}

		} catch (IOException excep) {
			String errMsg = PropertiesLoader.getErrorProperties().getProperty(
					CommonConstants.ERROR_METHOD_PARAM)
					+ " processRequest()";
			log.error(errMsg, excep);
		} finally {
			if (input != null) {
				input.close();
			}
			if (connection != null) {
				HttpConnection.closeConnection(connection);
			}
		}
	}

	/**
	 * Reads the response from the stream. Gets the latitude and longitude from
	 * the response xml and sets them in session
	 * 
	 * @param input
	 * @param session
	 * @throws CitysearchException
	 * @throws IOException
	 */
	private void processResponse(InputStream input, HttpServletRequest req)
			throws CitysearchException, IOException {
		SearchResponseHelper responseHelper = new SearchResponseHelper();
		Document doc = responseHelper.getDocumentfromStream(input);
		String[] latLon = responseHelper.parseXML(doc);
		req.setAttribute(CommonConstants.LATITUDE, latLon[0]);
		req.setAttribute(CommonConstants.LONGITUDE, latLon[1]);
		req.setAttribute(CommonConstants.SEARCHRESPONSE, doc);
		req.setAttribute(CommonConstants.SEARCH_API_QUERIED,
				CommonConstants.SEARCH_API_QUERIED);
	}

}
