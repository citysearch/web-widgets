package com.citysearch.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesLoader {
	private static Logger log;
	
	public PropertiesLoader(){
		log = LogHelper.getLogger(this.getClass().getName());
	}
	
	public static Properties getProperties(String fileName){
		InputStream inputStream;
		Properties properties;
		inputStream = PropertiesLoader.class.getClassLoader()  
						.getResourceAsStream(fileName);  
		properties = new Properties();  

		// load the inputStream using the Properties  
		try {
			if(inputStream != null)
				properties.load(inputStream);
		} catch (IOException ioexcep) {
			String errMsg = "Error Loading Properties File";
			log.error(errMsg, ioexcep);
		} 
		return properties;
	}
	
	
}
