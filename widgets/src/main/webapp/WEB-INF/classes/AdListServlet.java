

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import util.AdListConstants;
import util.CommonConstants;

/**
 * Servlet implementation class AdListServlet
 * This class contains common functionality for PFP and 
 * Search APIs
 */
public class AdListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected Logger log;
	public void init(ServletConfig config) throws ServletException{
		super.init(config);

		String prefix =  getServletContext().getRealPath("/");
		String file = getInitParameter(CommonConstants.LOG4J_INIT_FILE);
		if(file != null) {
			PropertyConfigurator.configure(prefix+file);
		}
		log = Logger.getLogger(AdListServlet.class);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

		
	/* Coverts InputStream to String and returns the String
	*/
	protected String getStringFromStream(InputStream input)
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
			input.close();
		}catch(IOException ioe){
			log.error(CommonConstants.STREAM_READ_ERROR,ioe);
		}
		return sb.toString();
	}
	
	/* Calls the PFP API and returns the connection object 
	*/
	protected HttpURLConnection callAPI(String apiQueryString){
		HttpURLConnection connection = null;
		URL url;
		try{
			url = new URL(apiQueryString);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(CommonConstants.HTTP_REQ_GET);
			connection.setDoOutput(true);
			connection.setReadTimeout(CommonConstants.RES_WAIT_TIME);
			connection.connect();
		}catch (IOException e) {
			log.error(CommonConstants.CONN_FAILURE,e);
		}
		return connection;
	}
	
	/* Forwards the request to other resources 
	
	*/
	protected void dispatchRequest(HttpServletRequest req, HttpServletResponse res,String resourcePath) throws ServletException{
		try{
			RequestDispatcher dispatcher;
			dispatcher = getServletContext().getRequestDispatcher(resourcePath);
			dispatcher.forward(req, res);
		}catch(IOException ioe){
			log.error(AdListConstants.DISPATCH_IO_EXCEPTION,ioe);
		}
	}
	
	/* Takes the name and value parameters,constructs a sting in the format "&name=value"
	and returns the string */
	protected String constructQueryParam(String name,String value){

		StringBuffer apiQueryString = new StringBuffer();
		if(value != null){
		apiQueryString.append(CommonConstants.AMPERSAND);
		apiQueryString.append(name);
		apiQueryString.append(CommonConstants.EQUALS);
		apiQueryString.append(value);
		}
		return apiQueryString.toString();
	}
	
	protected String constructSortAndRPPQueryParam(){
		StringBuffer apiQueryString = new StringBuffer();
		String temp;
		temp = constructQueryParam(CommonConstants.SORT,CommonConstants.SORT_VAL);
		apiQueryString.append(temp);
		temp = constructQueryParam(CommonConstants.RPP,CommonConstants.RPP_VAL);
		apiQueryString.append(temp);
		return apiQueryString.toString();
	}
	
	


}
