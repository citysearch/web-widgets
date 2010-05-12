package com.citysearch.helper;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogHelper {

    private static Logger log;

    public static Logger getLogger(String fileName, String className) {
        if (log == null) {
            if (fileName != null) {
                PropertyConfigurator.configure(fileName);
            }
        }
        log = getLogger(className);
        return log;
    }

    public static Logger getLogger(String className) {
        log = Logger.getLogger(className);
        return log;
    }
}
