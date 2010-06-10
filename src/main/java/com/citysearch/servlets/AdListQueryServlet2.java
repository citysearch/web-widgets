package com.citysearch.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;

import com.citysearch.adaptor.HttpConnection;
import com.citysearch.exception.CitysearchException;
import com.citysearch.helper.CommonConstants;
import com.citysearch.helper.PropertiesLoader;
import com.citysearch.processors.PfpResponseHelper;
import com.citysearch.processors.RequestHelper;
import com.citysearch.processors.ResponseHelper;
import com.citysearch.processors.SearchResponseHelper;
import com.citysearch.value.AdListBean;

public class AdListQueryServlet2 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(getClass());
    private static final String searchPagePath = "searchpagepath";
    private static final String redirectURLParam = "RedirectURL";
    private static final String resultListParam = "ResultList";
    private static final String jspFwdPathParam = "jspfwdpath2";
    private static final String latLonError = "lat.lon.error";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
            IOException {

        Map<String, String[]> paramMap = req.getParameterMap();
        Properties apiProperties = getAPIProperties();
        RequestHelper reqHelper = new RequestHelper(paramMap);
        setDefaultURL(req, apiProperties);
        String sourceLatLon[] = new String[2];
        // If the request is not valid, user will be directed to an error page
        if (reqHelper.validateRequest()) {
            throw new CitysearchException();
        }
        // Calling Search API to get latitude and longitude
        if (!reqHelper.validateLatLon()) {
            querySearchAPI(req, res);
            sourceLatLon[0] = (String) req.getAttribute(CommonConstants.LATITUDE);
            sourceLatLon[1] = (String) req.getAttribute(CommonConstants.LONGITUDE);

        } else {
            sourceLatLon = reqHelper.getSourceLatLon();
        }
        if (StringUtils.isBlank(sourceLatLon[0]) || StringUtils.isBlank(sourceLatLon[1])) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(latLonError);
            log.error(errMsg);
        }
        executeRequest(reqHelper, CommonConstants.PFP_API_TYPE, req, res, apiProperties,
                sourceLatLon);
    }

    /**
     * Sets the default redirect url in a session If any error occurs,this will be the url the user
     * will be forwarded
     *
     * @param req
     * @param apiProperties
     */
    private void setDefaultURL(HttpServletRequest req, Properties apiProperties) {
        String defaultRedirectURL = apiProperties.getProperty(CommonConstants.REDIRECT_URL_PARAM);
        HttpSession session = req.getSession(true);
        session.setAttribute(CommonConstants.REDIRECT_URL_PARAM, defaultRedirectURL);
    }

    /**
     * Reads the api properties file. If file is not found, an exception will be thrown
     *
     * @return
     * @throws CitysearchException
     */
    private Properties getAPIProperties() throws CitysearchException {
        Properties apiProperties;
        Properties errorProperties = PropertiesLoader.getErrorProperties();
        try {
            apiProperties = PropertiesLoader.getAPIProperties();
        } catch (Exception e) {
            String errMsg = errorProperties.getProperty(CommonConstants.API_PROP_READ_ERROR);
            log.error(errMsg);
            throw new CitysearchException(errMsg);
        }
        return apiProperties;
    }

    /**
     * @throws IOException
     * @throws ServletException
     *
     */
    private void querySearchAPI(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(getAPIProperties().getProperty(
                searchPagePath));
        dispatcher.include(req, res);
    }

    /**
     * Calls the pfp api and processes response If pfp api does not return any result, then a call
     * to pfp api is again issued but, without geography parameters(where,lat/lon) If no results are
     * returned then search api is queried and results are returned
     *
     * @param reqHelper
     * @param apiType
     * @param req
     * @param res
     * @param apiProperties
     * @param sourceLatLon
     * @throws ServletException
     * @throws IOException
     */
    private void executeRequest(RequestHelper reqHelper, String apiType, HttpServletRequest req,
            HttpServletResponse res, Properties apiProperties, String[] sourceLatLon)
            throws ServletException, IOException {
        String url = reqHelper.getQueryString(apiType);
        boolean response = executeRequest(url, req, res, sourceLatLon);
        if (!response) {
            url = reqHelper.getQueryString(CommonConstants.PFP_WITHOUT_GEOGRAPHY);
            response = executeRequest(url, req, res, sourceLatLon);
            if (!response) {
                String queried = (String) req.getAttribute(CommonConstants.SEARCH_API_QUERIED);
                if (StringUtils.isBlank(queried)) {
                    querySearchAPI(req, res);
                }
                Document doc = (Document) req.getAttribute(CommonConstants.SEARCHRESPONSE);
                req.removeAttribute(CommonConstants.SEARCH_API_QUERIED);
                req.removeAttribute(CommonConstants.SEARCHRESPONSE);
                response = processSuccessResponse(req, res, doc, sourceLatLon,
                        CommonConstants.SEARCH_API_TYPE);
                if (!response) {
                    String redirectURL = (String) req.getParameter(redirectURLParam);
                    redirectURL = getRedirectURL(redirectURL);
                    res.sendRedirect(redirectURL);
                }
            }
        }
    }

    /**
     * Connects to the url, gets and processes the response
     *
     * @param req
     * @param res
     * @param connection
     * @param apiProperties
     * @param sourceLatLon
     * @throws IOException
     * @throws ServletException
     */
    private boolean executeRequest(String url, HttpServletRequest req, HttpServletResponse res,
            String[] sourceLatLon) throws ServletException, IOException {
        HttpURLConnection connection = null;
        boolean response = false;
        try {
            log.info(url);
            connection = HttpConnection.getConnection(url);
            response = processResponse(req, res, connection, sourceLatLon);
        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + "executeRequest()";
            log.error(errMsg, excep);
            throw new CitysearchException();
        } finally {
            if (connection != null) {
                HttpConnection.closeConnection(connection);
            }
        }
        return response;
    }

    /**
     * Processes the response received from host.If any error is received, the user will be
     * forwarded to a default page.
     *
     * @param req
     * @param res
     * @param connection
     * @param apiProperties
     * @throws CitysearchException
     * @throws ServletException
     * @throws IOException
     */
    private boolean processResponse(HttpServletRequest req, HttpServletResponse res,
            HttpURLConnection connection, String[] sourceLatLon) throws CitysearchException,
            ServletException, IOException {
        res.setContentType(CommonConstants.RES_CONTENT_TYPE);
        boolean response = false;
        InputStream input = null;
        try {
            if (connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE) {
                String redirectURL = (String) req.getParameter(redirectURLParam);
                res.sendRedirect(getRedirectURL(redirectURL));
                response = true;
            } else {
                input = connection.getInputStream();
                Document doc = new PfpResponseHelper().getDocumentfromStream(input);
                response = processSuccessResponse(req, res, doc, sourceLatLon,
                        CommonConstants.PFP_API_TYPE);
            }
        } catch (IOException excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " processResponse()";
            log.error(errMsg, excep);
            throw new CitysearchException();
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return response;
    }

    /**
     * Parses the response xml and forward request to JSP
     *
     * @param redirectURL
     * @param req
     * @param res
     * @param connection
     * @param apiProperties
     * @throws CitysearchException
     * @throws ServletException
     * @throws IOException
     */
    private boolean processSuccessResponse(HttpServletRequest req, HttpServletResponse res,
            Document doc, String[] sourceLatLon, String apiType) throws CitysearchException,
            ServletException, IOException {
        boolean response = false;
        try {
            ResponseHelper responseHelper = null;
            if (apiType.equalsIgnoreCase(CommonConstants.PFP_API_TYPE)) {
                responseHelper = new PfpResponseHelper();
            } else if (apiType.equalsIgnoreCase(CommonConstants.SEARCH_API_TYPE)) {
                responseHelper = new SearchResponseHelper();
            }
            ArrayList<AdListBean> adList = responseHelper.parseXML(doc, sourceLatLon[0],
                    sourceLatLon[1], apiType, req.getContextPath());
            if (adList.size() > 0) {
                req.setAttribute(resultListParam, adList);
                dispatchRequest(req, res, getAPIProperties().getProperty(jspFwdPathParam));
                response = true;
            }

        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " processSuccessResponse()";
            log.error(errMsg, excep);
            throw new CitysearchException();
        }
        return response;
    }

    /**
     * Gets the default redirect url
     *
     * @param redirectURL
     * @param apiProperties
     * @return
     * @throws CitysearchException
     */
    private String getRedirectURL(String redirectURL) throws CitysearchException {
        if (StringUtils.isBlank(redirectURL)) {
            redirectURL = getAPIProperties().getProperty(CommonConstants.REDIRECT_URL_PARAM);
        }
        return redirectURL;
    }

    /**
     * Forwards the request to other resources
     */
    private void dispatchRequest(HttpServletRequest req, HttpServletResponse res,
            String resourcePath) throws ServletException {
        try {
            RequestDispatcher dispatcher;
            dispatcher = getServletContext().getRequestDispatcher(resourcePath);
            dispatcher.forward(req, res);
        } catch (IOException ioe) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " dispatchRequest()";
            log.error(errMsg, ioe);
            throw new CitysearchException();
        }
    }

}
