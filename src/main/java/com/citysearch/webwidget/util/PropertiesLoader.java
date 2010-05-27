package com.citysearch.webwidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;

public class PropertiesLoader {
	private static Logger log = Logger.getLogger(PropertiesLoader.class);
    private static final String apiPropertiesFile = "/api.properties";
    private static final String errorPropertiesFile = "/error.properties";
    private static Properties errorProperties;
    private static Properties apiProperties;
    private final static String errorPropMsg = "Error initializing the properties file";
    private final static String ioExcepMsg = "IOException while reading properties file";

    public static Properties getProperties(String fileName) {
        InputStream inputStream;
        Properties properties;
        inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName);
        properties = new Properties();

        // load the inputStream using the Properties
        try {
            if (inputStream != null)
                properties.load(inputStream);
        } catch (IOException ioexcep) {
            log.error(ioExcepMsg, ioexcep);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioexcep) {
                log.error(ioExcepMsg, ioexcep);
            }
        }
        return properties;
    }

    public static Properties getErrorProperties() throws CitysearchException {
        try {
            if (errorProperties == null) {
                errorProperties = getProperties(errorPropertiesFile);
            }
        } catch (Exception e) {
            log.error(errorPropMsg);
            throw new CitysearchException(errorPropMsg);
        }
        return errorProperties;
    }

    public static Properties getAPIProperties() throws CitysearchException {
        try {
            if (apiProperties == null) {
                apiProperties = getProperties(apiPropertiesFile);
            }
        } catch (Exception e) {
            log.error(errorPropMsg); 
            throw new CitysearchException(errorPropMsg);
        }
        return apiProperties;
    }
}
