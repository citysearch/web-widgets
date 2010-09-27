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

public class CitySearchCSSServlet extends HttpServlet {
    private final Logger LOGGER = Logger.getLogger(getClass());

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");// Because we don't to render

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(request.getScheme());
        strBuilder.append("://");
        strBuilder.append(request.getServerName());
        strBuilder.append(":");
        strBuilder.append(request.getServerPort());
        strBuilder.append(request.getContextPath());
        strBuilder.append("/jsp/CitySearchCSS.jsp");

        HttpURLConnection connection = null;
        InputStream iStream = null;
        try {
            connection = HttpConnection.getConnection(strBuilder.toString(),
                    null);
            iStream = connection.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(iStream, writer);
            String htmlString = writer.toString();

            // JAWR does not return the absolute path.
            // Massage the string to return the absolute href back to the JS.
            // Convert <link rel="stylesheet" type="text/css" media="screen"
            // href="/web-widgets/N951924673/citysearch.css" /> to
            // http://server//web-widgets/N951924673/citysearch.css

            String href = "href=\"";
            int idx = htmlString.indexOf(href);
            String hrefValue = htmlString.substring(idx + href.length());
            idx = hrefValue.indexOf("\"");
            String relativePath = hrefValue.substring(0, idx);

            StringBuilder absolutePath = new StringBuilder();
            absolutePath.append(request.getScheme());
            absolutePath.append("://");
            absolutePath.append(request.getServerName());
            absolutePath.append(":");
            absolutePath.append(request.getServerPort());
            absolutePath.append(relativePath);

            response.getWriter().write("citygrid.common.styleInject(\""+absolutePath.toString()+"\");");
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
