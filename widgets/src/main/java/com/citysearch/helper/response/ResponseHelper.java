package com.citysearch.helper.response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.citysearch.helper.LogHelper;


public class ResponseHelper {

	private Logger log;
	private final String ioExcepMsg = "Exception while reading from stream";
	
	public ResponseHelper(){
		log = LogHelper.getLogger(this.getClass().getName());
	}
	
	/**
	 * Reads from input stream, constructs and returns a jdom document
	 * @param input
	 * @return
	 * @throws IOException 
	 * @throws IOException 
	 */
	public Document getDocumentfromStream(InputStream input) throws IOException {
		Document document = null;
		SAXBuilder builder;
		String jdomExcepMsg = "Error building jdom document";
		
		try{
			if(input != null){
				builder = new SAXBuilder();
				document = builder.build(input);
			}
		}catch(JDOMException excep){
			log.error(jdomExcepMsg, excep);
		}
		catch(IOException ioExcep){
			log.error(ioExcepMsg, ioExcep);
		}finally{
			input.close();
		}
		return document;
	}
	
	/** Converts InputStream to String and returns the String
	*/
	public String getStringFromStream(InputStream input) throws IOException 
	{
		BufferedReader reader;
		StringBuilder sb = null;
		try{
			reader = new BufferedReader(new InputStreamReader(input));
			sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}catch(IOException ioe){
			log.error(ioExcepMsg,ioe);
		}finally{
			input.close();
		}
		return sb.toString();
	}
	  
}
