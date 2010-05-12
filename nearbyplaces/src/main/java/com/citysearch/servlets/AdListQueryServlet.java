package com.citysearch.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.citysearch.adaptor.HttpConnection;
import com.citysearch.exception.CitySearchException;
import com.citysearch.helper.PropertiesLoader;
import com.citysearch.helper.request.RequestHelper;
import com.citysearch.helper.response.PfpResponseHelper;
import com.citysearch.shared.CommonConstants;
import com.citysearch.value.AdListBean;

public class AdListQueryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Logger log;// = Logger.getLogger(getClass());

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String prefix = getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");
        if (file != null) {
            PropertyConfigurator.configure(prefix + file);
        }
        log = Logger.getLogger(AdListQueryServlet.class);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
            IOException {
        final String apiType = "pfp";
        final String errMsgParam = "errMsgParam";
        final String errPagePath = "errPagePath";
        final String searchPagePath = "searchPagePath";
        Map<String, String[]> paramMap = req.getParameterMap();
        RequestHelper reqHelper = new RequestHelper(paramMap);
        HttpSession session = req.getSession(true);
        Properties apiProperties = getAPIProperties();
        boolean error = reqHelper.validateRequest();
        RequestDispatcher dispatcher;
        if (error) {
            session.setAttribute(apiProperties.getProperty(errMsgParam), error);
            dispatchRequest(req, res, apiProperties.getProperty(errPagePath));
        } else {
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
                log.error(excep);
            } finally {
                HttpConnection.closeConnection(connection);
            }

        }
    }

    /**
     * Reads the api properties file. If file is not found, an exception will be thrown
     * 
     * @return
     * @throws CitySearchException
     */
    private Properties getAPIProperties() throws CitySearchException {
        Properties apiProperties = PropertiesLoader.apiProperties;
        Properties errProperties = PropertiesLoader.getErrorProperties();
        if (apiProperties == null) {
            String error = "apiproperties";
            String errMsg = errProperties.getProperty(error);
            log.error(errMsg);
            throw new CitySearchException(errMsg);
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
     * @throws CitySearchException
     * @throws ServletException
     */
    private void processResponse(HttpServletRequest req, HttpServletResponse res,
            HttpURLConnection connection, Properties apiProperties) throws CitySearchException,
            ServletException {
        String redirectURL;
        final String redirectURLParam = "RedirectURL";

        res.setContentType(CommonConstants.RES_CONTENT_TYPE);
        redirectURL = (String) req.getParameter(redirectURLParam);
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
            log.error(excep);
            throw new CitySearchException();
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
     * @throws CitySearchException
     * @throws ServletException
     */
    private void processSuccessResponse(String redirectURL, HttpServletRequest req,
            HttpServletResponse res, HttpURLConnection connection, Properties apiProperties)
            throws CitySearchException, ServletException {
        final String resultListParam = "ResultList";
        final String jspFwdPathParam = "jspFwdPath";
        final String latitude = "latitude";
        final String longitude = "longitude";
        final String callBackURLParam = "callbackURL";
        InputStream input = null;
        HttpSession session = req.getSession();
        PfpResponseHelper responseHelper = new PfpResponseHelper();
        try {
            input = connection.getInputStream();
            if (input != null) {
                String sourceLat = (String) session.getAttribute(latitude);
                String sourceLon = (String) session.getAttribute(longitude);

                String callbackURL = req.getParameter(callBackURLParam);
                ArrayList<AdListBean> adList = responseHelper.parseXML(input, sourceLat, sourceLon,
                        callbackURL);
                if (adList.size() > 0) {
                    req.setAttribute(resultListParam, adList);
                    dispatchRequest(req, res, apiProperties.getProperty(jspFwdPathParam));
                } else {
                    res.sendRedirect(redirectURL);
                }
            }
        } catch (IOException excep) {
            log.error(excep);
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
        String defaultRedirectURLParam = "defaultRedirectURL";
        if (StringUtils.isBlank(redirectURL)) {
            redirectURL = apiProperties.getProperty(defaultRedirectURLParam);
        }
        return redirectURL;
    }

    /**
     * Forwards the request to other resources
     */
    private void dispatchRequest(HttpServletRequest req, HttpServletResponse res,
            String resourcePath) throws ServletException {
        final String dispatchException = "Exception while request dispatching";
        try {
            RequestDispatcher dispatcher;
            dispatcher = getServletContext().getRequestDispatcher(resourcePath);
            dispatcher.forward(req, res);
        } catch (IOException ioe) {
            log.error(dispatchException, ioe);
        }
    }

}
