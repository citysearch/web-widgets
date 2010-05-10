package com.citysearch.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.citysearch.adaptor.HttpConnection;
import com.citysearch.helper.LogHelper;
import com.citysearch.helper.request.RequestHelper;
import com.citysearch.helper.response.PFPResponseHelper;
import com.citysearch.shared.CommonConstants;

public class AdListQueryServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	protected Logger log; 
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {
	 
	HttpURLConnection connection = null;
	RequestDispatcher dispatcher;
	HttpSession session;
	String errMsg = null;
	String queryString;
	Map paramMap;
	RequestHelper reqHelper;
	String apiType = "pfp";
	final String errMsgParam = "ERRORMSG";
	final String errPagePath = "/jsp/Error.jsp";
	final String searchPagePath = "SearchQueryServlet";
	String file;
	String log4jInitFile = "log4j-init-file";
	
	paramMap = req.getParameterMap();
	reqHelper = new RequestHelper(paramMap);
	session = req.getSession(true);
	file = getServletConfig().getInitParameter(log4jInitFile);
	initLogger(file);
	
	errMsg = reqHelper.validateRequest();
	if(errMsg != null){
		session.setAttribute(errMsgParam, errMsg);
		dispatchRequest(req,res,errPagePath);
	}else{
		//Calling Search API to get latitude and longitude
		if(! reqHelper.validateLatLon()){
			dispatcher = req.getRequestDispatcher(searchPagePath);
			dispatcher.include(req, res);
		}
		try{
			setRequest(req);
			queryString = reqHelper.getQueryString(apiType);
			connection = HttpConnection.getConnection(queryString);
			processResponse(req,res,connection);
		} catch (Exception excep) {
			log.error(excep);
		} finally{
			HttpConnection.closeConnection(connection);
		}

	}
 }
	
	private void initLogger(String fileName){
		log	= LogHelper.getLogger(fileName,this.getClass().getName());
	}
	
	private void setRequest(HttpServletRequest req){
		String callbackURL;
		String cssfile;
		String callbackFunction;
		final String callBackURLParam = "callbackURL";
		final String cssFileParam = "cssfile";
		final String callBackFunctionParam = "callbackfunction";
		
		callbackFunction = (String) req.getParameter(callBackURLParam);
		callbackURL = req.getParameter(cssFileParam); 
		cssfile = (String) req.getParameter(callBackFunctionParam);
		
		//Adding callback url and css file as request attributes 
		req.setAttribute(callBackURLParam,callbackURL);
		req.setAttribute(cssFileParam,cssfile);
		req.setAttribute(callBackFunctionParam,callbackFunction);
	}
	
	
	private void processResponse(HttpServletRequest req,HttpServletResponse res,HttpURLConnection connection) throws ServletException{
		String errMsg;
		String redirectURL;
		InputStream input = null;
		final String redirectURLParam = "RedirectURL";
				
		res.setContentType(CommonConstants.RES_CONTENT_TYPE);
		redirectURL = (String) req.getParameter(redirectURLParam);
		redirectURL = getRedirectURL(redirectURL);
		try {
			if(connection.getResponseCode() != CommonConstants.RES_SUCCESS_CODE){
				input = connection.getErrorStream();
				if(input != null)
					res.sendRedirect(redirectURL);
			}else{
				processSuccessResponse(redirectURL,req,res,connection);
				
			}
		} catch (IOException excep) {
			log.error(excep);
		}
		
	}
	
	private void processSuccessResponse(String redirectURL,HttpServletRequest req,HttpServletResponse res,HttpURLConnection connection) throws ServletException{
		final String resultListParam = "ResultList";
		final String jspFwdPath = "/jsp/AdListResult.jsp";
		final String latitude = "latitude";
		final String longitude = "longitude";
		InputStream input = null;
		String imagePropertiesFile;
		ArrayList adList;
		String imagePropertiesParam = "propertiesFile";
		HttpSession session = req.getSession();
		String sourceLat;
		String sourceLon;
		PFPResponseHelper responseHelper = new PFPResponseHelper();
		
		try {
			input = connection.getInputStream();
			if(input != null){
				imagePropertiesFile = getServletConfig().getInitParameter(imagePropertiesParam);
				sourceLat = (String) session.getAttribute(latitude);
				sourceLon = (String) session.getAttribute(longitude);
				adList = responseHelper.parseXML(input,sourceLat,sourceLon,imagePropertiesFile);
				if(adList.size() > 0){
				   req.setAttribute (resultListParam, adList );
				   dispatchRequest(req,res,jspFwdPath);
				}else{
					res.sendRedirect(redirectURL);
				}
			}
		} catch (IOException excep) {
			log.error(excep);
		}
	}
	
	private String getRedirectURL(String redirectURL){
		String defaultRedirectURL = "http://www.citysearch.com";
		if(redirectURL == null || redirectURL.length() == 0){
			redirectURL = defaultRedirectURL;
		}
		return redirectURL;
	}
	
	/**
	*  Forwards the request to other resources 
	*/
	private void dispatchRequest(HttpServletRequest req, HttpServletResponse res,String resourcePath) throws ServletException{
		final String dispatchException = "Exception while request dispatching";
		try{
			RequestDispatcher dispatcher;
			dispatcher = getServletContext().getRequestDispatcher(resourcePath);
			dispatcher.forward(req, res);
		}catch(IOException ioe){
			log.error(dispatchException,ioe);
		}
	}

 }