package com.citysearch.helper.request;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.citysearch.helper.PropertiesLoader;
import com.citysearch.shared.CommonConstants;

public class RequestHelper {

	private String what;
	private String where;
	private String publisher;
	private String sourceLat;
	private String sourceLon;
	private String tags;
	private Map map;
	public RequestHelper(Map map){
		String values[];
		this.map = map;
		values = (String[]) map.get(CommonConstants.WHAT);
		what = values != null ? values[0] : null;
		values = (String[]) map.get(CommonConstants.WHERE);
		where = values != null ? values[0] : null;
		values = (String[]) map.get(CommonConstants.PUBLISHER_CODE);
		publisher = values != null ? values[0] : null;
		values = (String[]) map.get(CommonConstants.LAT_URL);
		sourceLat = values != null ? values[0] : null;
		values = (String[]) map.get(CommonConstants.LON_URL);
		sourceLon = values != null ? values[0] : null;
		values = (String[]) map.get(CommonConstants.TAGS);
		tags = values != null ? values[0] : null;
	}
	
	public String validateRequest(){
		final String whatErrMsg = "What are you searching for";
		final String whereErrMsg = "Geographical location";
		final String publisherErrMsg = "Publisher";
		String errMsg = null;
		if(what == null || what.length() == 0){
		    if(tags == null || tags.length() == 0){
		        errMsg = whatErrMsg;
		    }
		}
		if(where == null || where.length() == 0){
		    if(sourceLat == null || sourceLat.length() == 0
		            || sourceLon == null || sourceLon.length() == 0){
		        errMsg = whereErrMsg;
		    }
		}

		if(publisher == null || publisher.length() == 0){
			errMsg = publisherErrMsg;
		}
		return errMsg;
	}
	
	public boolean validateLatLon(){
		boolean latLonFlag = true;
		if(sourceLat == null || sourceLat.length() == 0
				|| sourceLon == null || sourceLon.length() == 0)
			latLonFlag = false;
		return latLonFlag;
	}
	
	public String getQueryString(String queryType){
		String pfpQuery = "pfp";
		String searchQuery = "search";
		String queryString = null;
		if(queryType.equalsIgnoreCase(pfpQuery)){
			queryString = getPFPQueryString();
		}else if(queryType.equalsIgnoreCase(searchQuery)){
			queryString = getSearchQueryString();
		}
		return queryString;
	}

	private String getPFPQueryString() {
		StringBuffer apiQueryString = new StringBuffer();
		String key;
		String value;
		String values[];
		String queryParam;
		String longURL = "long";
		String latURL = "lat";
		String lonQueryParam = "lon";
		String pfpURL = "http://pfp.citysearch.com/pfp?";
		String pfpLocationURL = "http://pfp.citysearch.com/pfp/location?";
		String urlString;
		String url;
		Set keySet = map.keySet();
		Iterator keyIterator = keySet.iterator();
		
		while(keyIterator.hasNext()){
			key = (String)keyIterator.next();
			values = (String[]) map.get(key);
			value = values[0];
			if(key.equals(longURL))
				key = lonQueryParam;
			queryParam = constructQueryParam(key,value);
			apiQueryString.append(queryParam);
		}
		if (keySet.contains(latURL) && keySet.contains(longURL)){
			url = pfpLocationURL;
		}else{
			url = pfpURL;
		}
		//Adding sort param and RPP to get results sorted on distance
		//queryParam = constructSortAndRPPQueryParam();
		//apiQueryString.append(queryParam);
		urlString = getURLString(url,apiQueryString.toString());
		return urlString;
	}

	private String getSearchQueryString() {
		String queryParam;
		String publisherKey = "publisher";
		String apiKey = "api_key";
		String rppKey = "rpp";
		String defaultAPIKey = "gunyay6vkqnvc2geyfedbdt3";
		String defaultRppVal = "1";
		String apiVal;
		String rppVal;
		StringBuffer apiQueryString = new StringBuffer();
		Properties properties;
		String propertiesFileName = "searchapi.properties";
		String values[];
		String searchURL = "http://api.citysearch.com/search/locations?";
		String urlString;
			
		properties = PropertiesLoader.getProperties(propertiesFileName);
				
		apiVal = properties.getProperty(apiKey);
		if (apiVal == null || apiVal.length() == 0)
			apiVal = defaultAPIKey;
		rppVal = properties.getProperty(rppKey);
		if (rppVal == null || rppVal.length() == 0)
			rppVal = defaultRppVal;
		
		queryParam = constructQueryParam(CommonConstants.WHAT,what);
		apiQueryString.append(queryParam);
		queryParam = constructQueryParam(CommonConstants.WHERE,where);
		apiQueryString.append(queryParam);
		queryParam = constructQueryParam(publisherKey,publisher);
		apiQueryString.append(queryParam);
		queryParam = constructQueryParam(CommonConstants.TAGS,tags);
		apiQueryString.append(queryParam);
		queryParam = constructQueryParam(apiKey,apiVal);
		apiQueryString.append(queryParam);
		queryParam = constructQueryParam(rppKey,rppVal);
		apiQueryString.append(queryParam);
		
		urlString = getURLString(searchURL,apiQueryString.toString());
		return urlString;
		
	}
	
	private String getURLString(String urlString,String queryString){
		String url;
		queryString = queryString.replaceAll(" ", "%20");
		url = urlString + queryString;
		return url;
	}
	
	 /**
	   * Constructs query string for sort and rpp parameters
	   * @return query string
	   */
	  /*protected String constructSortAndRPPQueryParam(){
			StringBuffer apiQueryString = new StringBuffer();
			String temp;
			temp = constructQueryParam(AdListConstants.SORT,AdListConstants.SORT_VAL);
			apiQueryString.append(temp);
			temp = constructQueryParam(AdListConstants.RPP,AdListConstants.RPP_VAL);
			apiQueryString.append(temp);
			return apiQueryString.toString();
		}*/
	  
	  /** Takes the name and value parameters,constructs a sting in the format "&name=value"
		 * and returns the string
		 */
		protected String constructQueryParam(String name,String value){
			String equals = "=";
			String ampersand = "&";
			StringBuffer apiQueryString = new StringBuffer();
			if(value != null){
			apiQueryString.append(ampersand);
			apiQueryString.append(name);
			apiQueryString.append(equals);
			apiQueryString.append(value);
			}
			return apiQueryString.toString();
		}
}
