package com.citysearch.webwidget.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;
/**
 * This class contains the functionality related to Http connection like getting the connecton and
 * closing the connection
 * 
 * @author Aspert Benjamin
 * 
 */
public class HttpConnection {
    private static Logger log = Logger.getLogger(HttpConnection.class);
    private static final String reqMethod = "GET";
    private static final int resWaitTime = 10000;
    private static final String error = "connection.error";

    /**
     * Gets the connection object for the given url Exception thrown if there is a connection
     * failure
     * 
     * @param urlString
     * @param headers
     *            Map for HTTP Headers
     * @return
     * @throws CitysearchException
     */
    public static HttpURLConnection getConnection(String urlString, Map<String, String> headers)
            throws CitysearchException {
        HttpURLConnection connection = null;
        String errorMsg = PropertiesLoader.getErrorProperties().getProperty(error);
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            // Set HTTP headers if passed.
            if (headers != null && !headers.isEmpty()) {
                for (String key : headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }
            connection.setRequestMethod(reqMethod);
            connection.setDoOutput(true);
            connection.setReadTimeout(resWaitTime);
            connection.connect();
            if (connection == null) {
                throw new CitysearchException("HttpConnection", "getConnection", errorMsg);
            }
        } catch (IOException e) {
            throw new CitysearchException("HttpConnection", "getConnection", e);
        }
        return connection;
    }

    /**
     * Closes the connection object
     * 
     * @param connection
     */
    public static void closeConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
