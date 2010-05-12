package com.citysearch.adaptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.citysearch.exception.CitySearchException;
import com.citysearch.helper.PropertiesLoader;

public class HttpConnection {

    private static Logger log = Logger.getLogger(HttpConnection.class);
    private static final String reqMethod = "GET";
    private static final int resWaitTime = 10000;

    /**
     * Gets the connection object for the given url Exception thrown if there is a connection
     * failure
     * 
     * @param urlString
     * @return
     * @throws CitySearchException
     */
    public static HttpURLConnection getConnection(String urlString) throws CitySearchException {
        HttpURLConnection connection = null;
        final String error = "connectionError";
        String errorMsg = PropertiesLoader.getErrorProperties().getProperty(error);
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(reqMethod);
            connection.setDoOutput(true);
            connection.setReadTimeout(resWaitTime);
            connection.connect();
            if (connection == null) {
                log.error(errorMsg);
                throw new CitySearchException(errorMsg);
            }
        } catch (IOException e) {
            log.error(errorMsg, e);
            throw new CitySearchException(errorMsg);
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
