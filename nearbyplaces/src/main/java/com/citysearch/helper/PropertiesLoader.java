package com.citysearch.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.exception.CitySearchException;

public class PropertiesLoader {
    private static Logger log = Logger.getLogger(PropertiesLoader.class);
    public static final Properties apiProperties = getProperties("/api.properties");
    public static final Properties imageProperties = getProperties("/images.properties");
    private static final Properties errorProperties = getProperties("/error.properties");

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
            String errMsg = "Error Loading Properties File";
            log.error(errMsg, ioexcep);
        }
        return properties;
    }

    public static Properties getErrorProperties() throws CitySearchException {
        if (errorProperties == null) {
            String msg = "Error initializing the error.properties file";
            throw new CitySearchException(msg);
        }
        return errorProperties;
    }
}
