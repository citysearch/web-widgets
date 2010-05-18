package com.citysearch.adaptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.citysearch.helper.LogHelper;

public class HttpConnection {
	
	private static Logger log;
		
	public HttpConnection(){
		log = LogHelper.getLogger(this.getClass().getName());
	}
	public static HttpURLConnection getConnection(String urlString){
		HttpURLConnection connection = null;
		URL url;
		final String reqMethod = "GET";
		final int resWaitTime = 10000;
		final String ioExcepMsg = "Error Connecting to the PFP Server";
		
		try{
			url = new URL(urlString);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(reqMethod);
			connection.setDoOutput(true);
			connection.setReadTimeout(resWaitTime);
			connection.connect();
		}catch (IOException e) {
			log.error(ioExcepMsg,e);
		}
		return connection;
	}

	public static void closeConnection(HttpURLConnection connection){
		if(connection != null){
			connection.disconnect();
		}
	}
	
}
