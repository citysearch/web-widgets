package com.citysearch.processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.citysearch.exception.CitysearchException;
import com.citysearch.helper.PropertiesLoader;

public class ResponseHelper {

    private Logger log = Logger.getLogger(getClass());
    private static final String ioExcepMsg = "streamread.error";
    private static final String jdomExcepMsg = "jdom.excep.msg";

    /**
     * Reads from input stream, constructs and returns a jdom document
     * 
     * @param input
     * @return
     * @throws IOException
     * @throws IOException
     * @throws CitysearchException
     */
    public Document getDocumentfromStream(InputStream input) throws IOException,
            CitysearchException {
        Document document = null;
        try {
            if (input != null) {
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(input);
            }
        } catch (JDOMException excep) {
            log.error(PropertiesLoader.getErrorProperties().getProperty(jdomExcepMsg), excep);
        } catch (IOException ioExcep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(ioExcepMsg);
            log.error(errMsg, ioExcep);
            throw new CitysearchException(errMsg);
        } finally {
            input.close();
        }
        return document;
    }

    /**
     * Converts InputStream to String and returns the String
     * 
     * @throws CitysearchException
     */
    public String getStringFromStream(InputStream input) throws IOException, CitysearchException {
        StringBuilder sb = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException ioe) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(ioExcepMsg);
            log.error(errMsg, ioe);
            throw new CitysearchException(errMsg);
        } finally {
            input.close();
        }
        return sb.toString();
    }

}
