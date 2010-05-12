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

import org.apache.log4j.Logger;

import com.citysearch.adaptor.HttpConnection;
import com.citysearch.exception.CitySearchException;
import com.citysearch.helper.request.RequestHelper;
import com.citysearch.helper.response.SearchResponseHelper;
import com.citysearch.shared.CommonConstants;

public class SearchQueryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Logger log = Logger.getLogger(getClass());
    private static final String apiType = "search";
    private static final String latitude = "latitude";
    private static final String longitude = "longitude";

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
     * @throws CitySearchException
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws CitySearchException {
        HttpSession session = req.getSession(true);
        Map<String, String[]> paramMap = req.getParameterMap();
        res.setContentType(CommonConstants.RES_CONTENT_TYPE);
        RequestHelper reqHelper = new RequestHelper(paramMap);
        HttpURLConnection connection = null;
        try {
            String queryString = reqHelper.getQueryString(apiType);
            log.info(queryString);
            connection = HttpConnection.getConnection(queryString);
            InputStream input = null;
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
            log.error(excep);
        } finally {
            HttpConnection.closeConnection(connection);
        }
    }

    /**
     * Reads the response from the stream. Gets the latitude and longitude from the response xml and
     * sets them in session
     * 
     * @param input
     * @param session
     * @throws CitySearchException
     */
    private void processResponse(InputStream input, HttpSession session) throws CitySearchException {
        SearchResponseHelper responseHelper = new SearchResponseHelper();
        String[] latLon = responseHelper.parseXML(input);
        session.setAttribute(latitude, latLon[0]);
        session.setAttribute(longitude, latLon[1]);
    }

}
