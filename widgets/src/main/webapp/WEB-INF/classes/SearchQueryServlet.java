import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.net.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import org.apache.log4j.Logger;

import util.SearchConstants;

public class SearchQueryServlet extends AdListServlet {

	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		log = Logger.getLogger(AdListQueryServlet.class);
	} 
  
	public void doPost(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {

	 
	HttpSession session;
	InputStream input = null;
	HttpURLConnection connection = null;
	StringBuffer apiQueryString = new StringBuffer(SearchConstants.SEARCH_API);
	String what;
	String where;
	String publisher;
	String queryParam;
	String tags;
	String sort;
	String rpp;
	String errMsg;
	
	what = (String) req.getParameter(SearchConstants.WHAT);
	where = (String) req.getParameter(SearchConstants.WHERE);
	publisher = (String) req.getParameter(SearchConstants.PUBLISHER);
	tags = (String) req.getParameter(SearchConstants.TAGS);
	sort = (String) req.getParameter(SearchConstants.SORT);
	rpp = (String) req.getParameter(SearchConstants.RPP);

	queryParam = constructQueryParam(SearchConstants.WHAT,what);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(SearchConstants.WHERE,where);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(SearchConstants.PUBLISHER,publisher);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(SearchConstants.API_KEY,SearchConstants.API_KEY_VAL);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(SearchConstants.TAGS,tags);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(SearchConstants.SORT,sort);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(SearchConstants.RPP,rpp);
	apiQueryString.append(queryParam);


	session = req.getSession(true);
	res.setContentType(SearchConstants.RES_CONTENT_TYPE);
	
	log.info(apiQueryString.toString());
	try{
		
		connection = callAPI(apiQueryString.toString());
		
		if(connection.getResponseCode() != SearchConstants.RES_SUCCESS_CODE){
			input = connection.getErrorStream();
			if(input != null){
				errMsg = getStringFromStream(input);
				log.error(errMsg);
				//dispatchRequest(req,res,AdListConstants.ERROR_PAGE_PATH);
			}
		
		}else{

			//read the result from the server
			input = connection.getInputStream();
			if(input != null){
				parseXML(input,session);
				
			}
		}
        
    } catch (IOException e) {
      if(connection == null){
			log.error(SearchConstants.CONN_FAILURE,e);
		}else{
			log.error(SearchConstants.STREAM_READ_ERROR,e);
		}
	} 
  
 }

	
	
	 /**
	   * Reads the InputStream, constructs the result document, reads the source latitude and longitude 
	   * from the result xml and sets in a session object
	   */
	  private void parseXML(InputStream input,HttpSession session) throws IOException{
	    Document doc = null;
		SAXBuilder builder;
		Element rootElement;
		Element region;
		
		String sLat;
		String sLon;
		
		if(input != null){
			try{
			  builder = new SAXBuilder();
			  doc = builder.build(input);
			  		  
			  if(doc != null && doc.hasRootElement()){
	
				  
				  rootElement = doc.getRootElement();
				
				  //Getting Source Latitude and Longitude
				  region = rootElement.getChild(SearchConstants.REGION);
				  if(region != null){
	
						sLat = region.getChildText(SearchConstants.LATITUDE);
						sLon = region.getChildText(SearchConstants.LONGITUDE);
						if(sLat != null && sLon != null){
							session.setAttribute(SearchConstants.LATITUDE,sLat);
							session.setAttribute(SearchConstants.LONGITUDE,sLon);
						}
				  }
			}
	
			}catch(JDOMException jde){
				log.error(SearchConstants.JDOM_ERROR,jde);
			}catch(IOException ioe){
				log.error(SearchConstants.STREAM_READ_ERROR,ioe);
			}finally{
			
			}
		
		}
	   }

	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {
		
		doPost(req,res);
	}
  
	

 }