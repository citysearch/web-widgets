package com.citysearch.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.citysearch.adaptor.HttpConnection;
import com.citysearch.exception.CitysearchException;
import com.citysearch.helper.CommonConstants;
import com.citysearch.helper.PropertiesLoader;
import com.citysearch.processors.RequestHelper;
import com.citysearch.processors.SearchResponseHelper;

public class SearchQueryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());
    private static final String apiType = "search";
    private static final String latLonError = "lat.lon.error";

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
            IOException {
        processRequest(req, res);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
            IOException {
        processRequest(req, res);
    }

    /**
     * Constructs the query string, queries Search api and returns latitude and longitude values
     * 
     * @param req
     * @param res
     * @throws CitysearchException
     * @throws IOException
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws CitysearchException, IOException {
        HttpSession session = req.getSession(true);
        Map<String, String[]> paramMap = req.getParameterMap();
        res.setContentType(CommonConstants.RES_CONTENT_TYPE);
        RequestHelper reqHelper = new RequestHelper(paramMap);
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            String queryString = reqHelper.getQueryString(apiType);
            log.info(queryString);
            connection = HttpConnection.getConnection(queryString);
            if (connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE) {
                input = connection.getErrorStream();
                if (input != null) {
                    SearchResponseHelper responseHelper = new SearchResponseHelper();
                    String errMsg = responseHelper.getStringFromStream(input);
                    log.error(errMsg);
                }
            } else {
                // read the result from the server
                input = connection.getInputStream();
                if (input != null) {
                    processResponse(input, session);
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
            HttpConnection.closeConnection(connection);
        }
    }

    /**
     * Reads the response from the stream. Gets the latitude and longitude from the response xml and
     * sets them in session
     * 
     * @param input
     * @param session
     * @throws CitysearchException
     */
    private void processResponse(InputStream input, HttpSession session) throws CitysearchException {
        SearchResponseHelper responseHelper = new SearchResponseHelper();
        String[] latLon = responseHelper.parseXML(input);
        if (StringUtils.isBlank(latLon[0]) || StringUtils.isBlank(latLon[0])) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(latLonError);
            throw new CitysearchException(errMsg);
        }
        session.setAttribute(CommonConstants.LATITUDE, latLon[0]);
        session.setAttribute(CommonConstants.LONGITUDE, latLon[1]);
    }

}
