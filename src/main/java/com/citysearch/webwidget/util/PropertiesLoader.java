package com.citysearch.webwidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.exception.CitysearchException;

/**
 * This class loads properties from property files.
 * 
 * @author Aspert
 * 
 */
public class PropertiesLoader {
    private static Logger log = Logger.getLogger(PropertiesLoader.class);
    private static final String API_PROPERTIES_FILE = "/api.properties";
    private static final String ERROR_PROPERTIES_FILE = "/error.properties";
    private static Properties errorProperties;
    private static Properties apiProperties;
    private final static String ERROR_PROP_MSG = "Error initializing the properties file";
    private final static String IO_EXCEP_MSG = "IOException while reading properties file";

    /**
     * Takes the file name as input and reads the properties from the file. Returns the Properties
     * object that contains parameters as key,value pairs
     * 
     * @param fileName
     * @return Properties
     */
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
            log.error(IO_EXCEP_MSG, ioexcep);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioexcep) {
                log.error(IO_EXCEP_MSG, ioexcep);
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
        try {
            if (errorProperties == null) {
                errorProperties = getProperties(ERROR_PROPERTIES_FILE);
            }
        } catch (Exception e) {
            log.error(ERROR_PROP_MSG);
            throw new CitysearchException("PropertiesLoader", "getErrorProperties", ERROR_PROP_MSG);
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
        try {
            if (apiProperties == null) {
                apiProperties = getProperties(API_PROPERTIES_FILE);
            }
        } catch (Exception e) {
            log.error(ERROR_PROP_MSG);
            throw new CitysearchException("PropertiesLoader", "getAPIProperties", ERROR_PROP_MSG);
        }
        return apiProperties;
    }
}
