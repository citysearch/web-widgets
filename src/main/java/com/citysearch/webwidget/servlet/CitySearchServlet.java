package com.citysearch.webwidget.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HttpConnection;

public class CitySearchServlet extends HttpServlet {
    private final Logger LOGGER = Logger.getLogger(getClass());

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String queryString = request.getQueryString();
        String adUnitName = request.getParameter("adUnitName");
        String adUnitSize = request.getParameter("adUnitSize");
        int displaySize = (adUnitSize != null && adUnitSize.equals(CommonConstants.CONQUEST_AD_SIZE)) ? CommonConstants.CONQUEST_DISPLAY_SIZE
                : CommonConstants.MANTLE_DISPLAY_SIZE;

        if (adUnitName != null) {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(request.getScheme());
            strBuilder.append("://");
            strBuilder.append(request.getServerName());
            strBuilder.append(":");
            strBuilder.append(request.getServerPort());
            strBuilder.append(request.getContextPath());
            strBuilder.append("/actions");
            if (adUnitName.equalsIgnoreCase(CommonConstants.AD_UNIT_NAME_NEARBY)) {
                strBuilder.append("/nearbyplaces");
            } else if (adUnitName.equalsIgnoreCase(CommonConstants.AD_UNIT_NAME_REVIEW)) {
                strBuilder.append("/review");
            } else if (adUnitName.equalsIgnoreCase(CommonConstants.AD_UNIT_NAME_OFFERS)) {
                if (adUnitSize != null && adUnitSize.equals(CommonConstants.CONQUEST_AD_SIZE)) {
                    displaySize = 1;
                    strBuilder.append("/conquestOffers");
                } else {
                    displaySize = CommonConstants.MANTLE_DISPLAY_SIZE;
                    strBuilder.append("/offers");
                }
            }
            strBuilder.append("?");
            strBuilder.append(queryString);
            strBuilder.append("&displaySize=" + displaySize);

            response.setContentType("text/plain");// Because we don't to render

            HttpURLConnection connection = null;
            InputStream iStream = null;
            try {
                connection = HttpConnection.getConnection(strBuilder.toString(), null);
                iStream = connection.getInputStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(iStream, writer);
                String htmlString = writer.toString();
                htmlString = StringEscapeUtils.escapeHtml(htmlString);
                htmlString = htmlString.replaceAll("\\n", "");
                htmlString = "citygrid.common.loadWidget(\"" + htmlString + "\");";

                response.getWriter().write(htmlString);
            } catch (IOException ioe) {
                LOGGER.error(ioe.getMessage());
                throw ioe;
            } catch (CitysearchException cse) {
                LOGGER.error(cse.getMessage());
            } finally {
                if (connection != null) {
                    HttpConnection.closeConnection(connection);
                }
                if (iStream != null) {
                    iStream.close();
                }
            }
        }
    }
}
