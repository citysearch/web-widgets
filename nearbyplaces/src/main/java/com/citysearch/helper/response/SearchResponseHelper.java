package com.citysearch.helper.response;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.exception.CitySearchException;

public class SearchResponseHelper extends ResponseHelper{

	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * Parse the response xml received from search api and returns latitude and longitude
	 * If proper response is not returned by api, the user will be directed to a default page
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws CitySearchException
	 */
	public String[] parseXML(InputStream input) throws CitySearchException{
	  	Document doc = null;
		Element rootElement;
		Element region;
		String latitude = "latitude";
		String longitude = "longitude";
		String regionTag = "region";
		String[] latLonValues = new String[2];
		String sLat;
		String sLon;
		try{
		   doc = getDocumentfromStream(input);
		   if(doc != null && doc.hasRootElement()){
			  rootElement = doc.getRootElement();
			  //Getting Source Latitude and Longitude
			  region = rootElement.getChild(regionTag);
			  if(region != null){
				sLat = region.getChildText(latitude);
				sLon = region.getChildText(longitude);
				if(sLat != null && sLon != null){
					latLonValues[0] = sLat;
					latLonValues[1] = sLon;
				}
			  }
		  }
		}catch(IOException excep){
			log.error(excep);
			throw new CitySearchException();
		}
	   return latLonValues;
	}
}
