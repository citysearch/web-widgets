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
import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.HttpConnection;

public class CitySearchServlet extends HttpServlet {
    private final Logger LOGGER = Logger.getLogger(getClass());

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String queryString = request.getQueryString();
        String adUnitName = request.getParameter("widget");
        if (adUnitName != null) {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(request.getScheme());
            strBuilder.append("://");
            strBuilder.append(request.getServerName());
            strBuilder.append(":");
            strBuilder.append(request.getServerPort());
            strBuilder.append(request.getContextPath());
            strBuilder.append("/actions");
            if (adUnitName.equalsIgnoreCase("nearby")) {
                strBuilder.append("/nearbyplaces");
            } else if (adUnitName.equalsIgnoreCase("review")) {
                strBuilder.append("/review");
            }
            strBuilder.append("?");
            strBuilder.append(queryString);

            response.setContentType("text/plain");// Because we don't to render

            HttpURLConnection connection = null;
            InputStream iStream = null;
            try {
                connection = HttpConnection.getConnection(strBuilder.toString());
                iStream = connection.getInputStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(iStream, writer);
                String htmlString = writer.toString();
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
                if (iStream != null)
                {
                    iStream.close();
                }
            }
        }
    }
}
