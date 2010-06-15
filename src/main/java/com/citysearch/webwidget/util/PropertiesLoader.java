package com.citysearch.webwidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;

/**
 * This class loads properties from property files.
 * 
 * @author Aspert Benjamin
 * 
 */
public class PropertiesLoader {
    private static Logger log = Logger.getLogger(PropertiesLoader.class);
    private static final String API_PROPERTIES_FILE = "/api.properties";
    private static final String ERROR_PROPERTIES_FILE = "/error.properties";
    private static final String APPLICATION_PROPERTIES_FILE = "/application.properties";
    private static Properties errorProperties;
    private static Properties apiProperties;
    private static Properties applicationProperties;

    /**
     * Takes the file name as input and reads the properties from the file. Returns the Properties
     * object that contains parameters as key,value pairs
     * 
     * @param fileName
     * @return Properties
     */
    public static Properties getProperties(String fileName) throws CitysearchException {
        InputStream inputStream;
        Properties properties;
        inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName);
        properties = new Properties();

        // load the inputStream using the Properties
        try {
            if (inputStream != null)
                properties.load(inputStream);
        } catch (IOException ioexcep) {
            throw new CitysearchException("PropertiesLoader", "getProperties", ioexcep);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioexcep) {
                throw new CitysearchException("PropertiesLoader", "getProperties", ioexcep);
            }
        }
        return properties;
    }

    /**
     * Read the error.properties file. If that file is missing, throws CitysearchException
     * 
     * @return Properties
     * @throws CitysearchException
     */
    public static Properties getErrorProperties() throws CitysearchException {
        if (errorProperties == null) {
            errorProperties = getProperties(ERROR_PROPERTIES_FILE);
        }
        return errorProperties;
    }

    /**
     * Read the api.properties file. If that file is missing, throws CitysearchException
     * 
     * @return Properties
     * @throws CitysearchException
     */
    public static Properties getAPIProperties() throws CitysearchException {
        if (apiProperties == null) {
            apiProperties = getProperties(API_PROPERTIES_FILE);
        }
        return apiProperties;
    }

    public static Properties getApplicationProperties() throws CitysearchException {
        if (applicationProperties == null) {
            applicationProperties = getProperties(APPLICATION_PROPERTIES_FILE);
        }
        return applicationProperties;
    }
}
