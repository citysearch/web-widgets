package com.citysearch.helper.response;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.helper.LogHelper;

public class SearchResponseHelper extends ResponseHelper{

	private Logger log;
	
	public SearchResponseHelper(){
		log = LogHelper.getLogger(this.getClass().getName());
	}
	
	public String[] parseXML(InputStream input) throws IOException{
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
		}
	   return latLonValues;
	}
}
