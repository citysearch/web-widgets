package com.citysearch.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.citysearch.adaptor.HttpConnection;
import com.citysearch.helper.LogHelper;
import com.citysearch.helper.request.RequestHelper;
import com.citysearch.helper.response.SearchResponseHelper;
import com.citysearch.shared.CommonConstants;

public class SearchQueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected Logger log = LogHelper.getLogger(getClass().getName());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {
		processRequest(req,res);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
    							throws ServletException, IOException {
		processRequest(req,res);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException{
		HttpSession session;
		InputStream input = null;
		HttpURLConnection connection = null;
		String queryParam;
		String errMsg;
		RequestHelper reqHelper;
		String apiType = "search";
		String queryString = null;
		Map paramMap;
		
		session = req.getSession(true);
		paramMap = req.getParameterMap();
		res.setContentType(CommonConstants.RES_CONTENT_TYPE);
		reqHelper = new RequestHelper(paramMap);
		log.info(queryString);
		try{
			
			queryString = reqHelper.getQueryString(apiType);
			connection = HttpConnection.getConnection(queryString);
			
			if(connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE){
				input = connection.getErrorStream();
				if(input != null){
					SearchResponseHelper responseHelper = new SearchResponseHelper();
					errMsg = responseHelper.getStringFromStream(input);
					log.error(errMsg);
				}
			
			}else{
				
				//read the result from the server
				input = connection.getInputStream();
				if(input != null){
					processResponse(input,session);
					
				}
			}
	        
	    } catch (IOException excep) {
	      log.error(excep);
		} finally{
			HttpConnection.closeConnection(connection);
		}
	}

	private void processResponse(InputStream input, HttpSession session) {
		SearchResponseHelper responseHelper;
		String[] latLon;
		final String latitude = "latitude";
		final String longitude = "longitude";
		final String errMsg = "Error parsing response";
		
		responseHelper = new SearchResponseHelper();
		try {
			latLon = responseHelper.parseXML(input);
			session.setAttribute(latitude, latLon[0]);
			session.setAttribute(longitude, latLon[1]);
		} catch (IOException excep) {
			log.error(errMsg,excep);
		}
		
	}
	
	
 }