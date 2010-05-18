package com.citysearch.processors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.exception.CitysearchException;
import com.citysearch.helper.CommonConstants;
import com.citysearch.helper.PropertiesLoader;

public class SearchResponseHelper extends ResponseHelper {

    private Logger log = Logger.getLogger(getClass());
    private static final String regionTag = "region";

    /**
     * Parse the response xml received from search api and returns latitude and longitude If proper
     * response is not returned by api, the user will be directed to a default page
     * 
     * @param input
     * @return
     * @throws IOException
     * @throws CitysearchException
     */
    public String[] parseXML(InputStream input) throws CitysearchException {
        String[] latLonValues = new String[2];
        try {
            Document doc = getDocumentfromStream(input);
            if (doc != null && doc.hasRootElement()) {
                Element rootElement = doc.getRootElement();
                // Getting Source Latitude and Longitude
                Element region = rootElement.getChild(regionTag);
                if (region != null) {
                    String sLat = region.getChildText(CommonConstants.LATITUDE);
                    String sLon = region.getChildText(CommonConstants.LONGITUDE);
                    if (sLat != null && sLon != null) {
                        latLonValues[0] = sLat;
                        latLonValues[1] = sLon;
                    }
                }
            }
        } catch (IOException excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " parseXML()";
            log.error(errMsg, excep);
            throw new CitysearchException();
        }
        return latLonValues;
    }
}
