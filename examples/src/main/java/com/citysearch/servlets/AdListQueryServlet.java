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

import com.citysearch.adaptor.HttpConnection;
import com.citysearch.exception.CitysearchException;
import com.citysearch.helper.CommonConstants;
import com.citysearch.helper.PropertiesLoader;
import com.citysearch.processors.PfpResponseHelper;
import com.citysearch.processors.RequestHelper;
import com.citysearch.value.AdListBean;

public class AdListQueryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger(getClass());
    private static final String apiType = "pfp";
    private static final String searchPagePath = "searchpagepath";
    private static final String redirectURLParam = "RedirectURL";
    private static final String resultListParam = "ResultList";
    private static final String jspFwdPathParam = "jspfwdpath";
    private static final String callBackURLParam = "callbackURL";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
            IOException {

        Map<String, String[]> paramMap = req.getParameterMap();
        Properties apiProperties = getAPIProperties();
        RequestHelper reqHelper = new RequestHelper(paramMap);
        setDefaultURL(req, apiProperties);
        // If the request is not valid, user will be directed to an error page
        if (reqHelper.validateRequest()) {
            throw new CitysearchException();
        }
        RequestDispatcher dispatcher;
        // Calling Search API to get latitude and longitude
        if (!reqHelper.validateLatLon()) {
            dispatcher = req.getRequestDispatcher(apiProperties.getProperty(searchPagePath));
            dispatcher.include(req, res);
        }
        HttpURLConnection connection = null;
        try {
            String queryString = reqHelper.getQueryString(apiType);
            log.info(queryString);
            connection = HttpConnection.getConnection(queryString);
            processResponse(req, res, connection, apiProperties);
        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + "doGet()";
            log.error(errMsg, excep);
            throw new CitysearchException();
        } finally {
            HttpConnection.closeConnection(connection);
        }

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
    private void processResponse(HttpServletRequest req, HttpServletResponse res,
            HttpURLConnection connection, Properties apiProperties) throws CitysearchException,
            ServletException, IOException {
        res.setContentType(CommonConstants.RES_CONTENT_TYPE);
        String redirectURL = (String) req.getParameter(redirectURLParam);
        redirectURL = getRedirectURL(redirectURL, apiProperties);
        InputStream input = null;
        try {
            if (connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE) {
                input = connection.getErrorStream();
                if (input != null)
                    res.sendRedirect(redirectURL);
            } else {
                processSuccessResponse(redirectURL, req, res, connection, apiProperties);
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
    private void processSuccessResponse(String redirectURL, HttpServletRequest req,
            HttpServletResponse res, HttpURLConnection connection, Properties apiProperties)
            throws CitysearchException, ServletException, IOException {
        InputStream input = null;
        HttpSession session = req.getSession();
        PfpResponseHelper responseHelper = new PfpResponseHelper();
        try {
            input = connection.getInputStream();
            if (input != null) {
                String sourceLat = (String) session.getAttribute(CommonConstants.LATITUDE);
                String sourceLon = (String) session.getAttribute(CommonConstants.LONGITUDE);
                String callbackURL = req.getParameter(callBackURLParam);
                ArrayList<AdListBean> adList = responseHelper.parseXML(input, sourceLat, sourceLon,
                        callbackURL, req.getContextPath());
                if (adList.size() > 0) {
                    String callBackFunction = req.getParameter(CommonConstants.CALL_BACK_FUNCTION_PARAM);
                    req.setAttribute(resultListParam, adList);
                    req.setAttribute(CommonConstants.CALL_BACK_FUNCTION_PARAM, callBackFunction);
                    dispatchRequest(req, res, apiProperties.getProperty(jspFwdPathParam));
                } else {
                    res.sendRedirect(redirectURL);
                }
            }
        } catch (IOException excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " processSuccessResponse()";
            log.error(errMsg, excep);
            throw new CitysearchException();
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * Gets the default redirect url
     * 
     * @param redirectURL
     * @param apiProperties
     * @return
     */
    private String getRedirectURL(String redirectURL, Properties apiProperties) {
        if (StringUtils.isBlank(redirectURL)) {
            redirectURL = apiProperties.getProperty(CommonConstants.REDIRECT_URL_PARAM);
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
