import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.net.*;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import org.apache.log4j.Logger;

import util.AdListBean;
import util.AdListConstants;
import util.AdListBeanComparator;
import util.CommonConstants;

public class AdListQueryServlet extends AdListServlet {

	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		log = Logger.getLogger(AdListQueryServlet.class);
	} 
   

	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {

	 
	InputStream input = null;
	HttpURLConnection connection = null;
	//String reqQueryString ;
	ArrayList adList;
	RequestDispatcher dispatcher;
	HttpSession session;
	String callbackURL;
	String what;
	String where;
	String clientIP;
	String publisher;
	String offers;
	String sourceLat;
	String sourceLon;
	String queryParam;
	String tags;
	String sort;
	String rpp;
	boolean error = false;
	String errMsg = null;

	StringBuffer apiQueryString = new StringBuffer(AdListConstants.PFP_API);
	//what=sushi&where=los_angeles,ca&client_ip=63.251.207.35&publisher=test&offers=true
	
	//reqQueryString = req.getQueryString();

	what = (String) req.getParameter(AdListConstants.WHAT);
	where = (String) req.getParameter(AdListConstants.WHERE);
	clientIP = (String) req.getParameter(AdListConstants.PFP_CLIENT_IP);
	publisher = (String) req.getParameter(AdListConstants.PUBLISHER);
	offers = (String) req.getParameter(AdListConstants.PFP_OFFERS);
	sourceLat = (String) req.getParameter(AdListConstants.LATITUDE);
	sourceLon = (String) req.getParameter(AdListConstants.LONGITUDE);
	tags = (String) req.getParameter(AdListConstants.TAGS);
	/*sort = (String) req.getParameter(AdListConstants.SORT);
	rpp = (String) req.getParameter(AdListConstants.RPP); */

	
	
	session = req.getSession(true);
	//Calling error page if any of the required parameters are missing
	if(what == null || what.length() == 0){
		error = true;
		errMsg = AdListConstants.WHAT_ERR_MSG;
	}

	if(where == null || where.length() == 0){
		error = true;
		errMsg = AdListConstants.WHERE_ERR_MSG;
	}

	if(publisher == null || publisher.length() == 0){
		error = true;
		errMsg = AdListConstants.PUBLISHER_ERR_MSG;
	}

	if(error){
		session.setAttribute(AdListConstants.ERR_MSG, errMsg);
		dispatchRequest(req,res,AdListConstants.ERROR_PAGE_PATH);
	}else{
	
	//Calling Search API to get latitude and longitude
	if(sourceLat == null || sourceLon == null){
		dispatcher = req.getRequestDispatcher(AdListConstants.SEARCH_FWD_PATH);
		dispatcher.include(req, res);
		sourceLat = (String) session.getAttribute(AdListConstants.LATITUDE);
		sourceLon = (String) session.getAttribute(AdListConstants.LONGITUDE);
	}

	//Constructing Query Params
	queryParam = constructQueryParam(AdListConstants.WHAT,what);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(AdListConstants.WHERE,where);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(AdListConstants.PUBLISHER,publisher);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(AdListConstants.PFP_CLIENT_IP,clientIP);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(AdListConstants.PFP_OFFERS,offers);
	apiQueryString.append(queryParam);
	queryParam = constructQueryParam(AdListConstants.TAGS,tags);
	apiQueryString.append(queryParam);
	//Adding sort param and RPP to get results sorted on distance
	queryParam = constructSortAndRPPQueryParam();
	apiQueryString.append(queryParam);

	callbackURL = req.getParameter(AdListConstants.PFP_CALL_BACK_URL);
	req.setAttribute(AdListConstants.PFP_CALL_BACK_URL,callbackURL);
	
	res.setContentType(AdListConstants.RES_CONTENT_TYPE);
	
	//apiQueryString.append(reqQueryString);
	try{
		connection = callAPI(apiQueryString.toString());
	
		if(connection.getResponseCode() != AdListConstants.RES_SUCCESS_CODE){
			input = connection.getErrorStream();
			if(input != null){
				errMsg = getStringFromStream(input);
				dispatchRequest(req,res,AdListConstants.ERROR_PAGE_PATH);
			}
		
		}else{

			//read the result from the server
			input = connection.getInputStream();
			if(input != null){

				adList = parseXML(input,sourceLat,sourceLon);
				req.setAttribute (AdListConstants.RESULT_LIST, adList );
				dispatchRequest(req,res,AdListConstants.JSP_FWD_PATH);
			}
		}
        
	} catch (IOException e) {
		log.error(AdListConstants.STREAM_READ_ERROR,e);
	} catch (ServletException e) {
		log.error(AdListConstants.SERVLET_EXCEP,e);
	} 

	}
 }


  /* This method reads the values from the result xml, stores each of the businesses returned in a list,
     does necessary manipulations and returns the list */
  private ArrayList parseXML(InputStream input,String sLat,String sLon) throws IOException{
    Document doc = null;
	SAXBuilder builder;
	Element rootElement;
	List resultSet = null;
	Element ad;
		
	String name;
	StringBuffer city;
	String rating ;
	String category;
	String dLat;
	String dLon;
	double ratings = 0.0;
	double destLat = 0.0;
	double destLon = 0.0;
	double sourceLat = 0.0;
	double sourceLon = 0.0;
	double distance = 0.0;
	int userReviewCount;
	String listingURL;
	String listingId;

	ArrayList adList = new ArrayList();
	ArrayList adListLongDist = new ArrayList();
	AdListBean adListBean;
	AdListBeanComparator beanComparator;
	int[] ratingList;
	int size;
	String dist;
	String reviewCount;
	int resultSize = 3;
	if(input != null){
		try{

		  builder = new SAXBuilder();
		  doc = builder.build(input);
		  		  
		  if(doc != null && doc.hasRootElement()){
			  
			  rootElement = doc.getRootElement();
			
			  resultSet = rootElement.getChildren(AdListConstants.AD_TAG);
			  if(resultSet != null){
				size = resultSet.size();
								
				//Retrieving values from result xml
				for(int i = 0; i < size; i++){
				 	adListBean = new AdListBean();
					ad = (Element)resultSet.get(i); 
					if(ad != null){
						name = ad.getChildText(AdListConstants.NAME_TAG);
						city = new StringBuffer();
						city.append( ad.getChildText(AdListConstants.CITY_TAG));
						city.append(AdListConstants.COMMA);
						city.append(ad.getChildText(AdListConstants.STATE_TAG));
						rating = ad.getChildText(AdListConstants.REVIEW_RATING_TAG);
						reviewCount = ad.getChildText(AdListConstants.REVIEWS_TAG);
						dist = ad.getChildText(AdListConstants.DISTANCE_TAG);
						listingId = ad.getChildText(AdListConstants.LISTING_ID_TAG);
						category = ad.getChildText(AdListConstants.TAGLINE_TAG);
						dLat = ad.getChildText(AdListConstants.LATITUDE);
						dLon = ad.getChildText(AdListConstants.LONGITUDE);
						listingURL = ad.getChildText(AdListConstants.AD_DISPLAY_URL_TAG);
						

						//Calculating Distance
						if(sLat != null && sLon != null && dLat != null && dLon != null) {
							sourceLat = Double.valueOf(sLat);
							sourceLon = Double.valueOf(sLon);
							destLat = Double.valueOf(dLat);
							destLon = Double.valueOf(dLon);
							distance = getDistance(sourceLat,sourceLon,destLat,destLon);
						}
						
						//Get the rating list for display
						ratingList = getRatingsList(rating);
						
						//Get the rating as double value
						ratings = getRatingValue(rating);
						
						//Setting userReviewCount to 0
						userReviewCount = getUserReviewCount(reviewCount);

						//Truncate Business Name
						name = getBusinessName(name);
						
						//Truncate taglib text
						category = getTagLine(category);
						
						
						/*dist = Double.toString(distance);
						dist = dist.substring(0,3);*/
						
						//Adding to AdListBean
						adListBean.setName(name);
						adListBean.setLocation(city.toString());
						adListBean.setRating(ratingList);
						adListBean.setReviewCount(userReviewCount);
						adListBean.setDistance(distance);
						adListBean.setListingId(listingId);
						adListBean.setCategory(category);
						adListBean.setRatings(ratings);
						adListBean.setAdDisplayURL(listingURL);
						
						if(distance < AdListConstants.DEFAULT_RADIUS) {
							adList.add(adListBean);
						}else if(distance > AdListConstants.DEFAULT_RADIUS &&
								 distance < AdListConstants.EXTENDED_RADIUS){
							adListLongDist.add(adListBean);
						}
						
					}

				}
				beanComparator = new AdListBeanComparator();
				if(adList.size() < resultSize){
					int listSize = adListLongDist.size();
					for(int j = 0;j < listSize;j++){
						adList.add(adListLongDist.get(j));
					}
				}
				Collections.sort(adList,beanComparator);
	  
		  }

		}
		
		}catch(JDOMException jde){
			log.error(AdListConstants.JDOM_ERROR,jde);
		}catch(IOException ioe){
			log.error(AdListConstants.STREAM_READ_ERROR,ioe);
		}
	}

	return adList;
   }



	/**
	 * This method takes the source latitude, logitude and destination latitude, longitude to calculate 
	 * the distance between two points 
	 */
	private double getDistance(double sourceLat,double sourceLon,double destLat,double destLon){

		double distance = 0.0;
		double kmToMile = 0.622;
		int radius = 6371; // Radius of the earth in km
		double diffOfLat = Math.toRadians(destLat-sourceLat);  
		double diffOfLon = Math.toRadians(destLon-sourceLon); 
		double sourceLatRad = Math.toRadians(sourceLat);
		double destLatRad = Math.toRadians(destLat);

		double calcResult = Math.sin(diffOfLat/2) * Math.sin(diffOfLat/2) +
					Math.cos(sourceLatRad) * Math.cos(destLatRad) * 
					Math.sin(diffOfLon/2) * Math.sin(diffOfLon/2); 
		
		calcResult = 2 * Math.atan2(Math.sqrt(calcResult), Math.sqrt(1-calcResult)); 
		distance = radius * calcResult; // Distance in km
		//Converting from kms to Miles
		distance = distance * kmToMile;
		//Rounding to one decimal place
		distance = Math.floor(distance * 10) / 10.0;
		return distance;
	}
	
	/**
	 * Calculate the ratings value and determines the rating stars to be displayed 
	 * Returns what type of star to be displayed in an array
	 * @param rating
	 * @return
	 */
	private int[] getRatingsList(String rating){
		double ratings = 0.0;
		int[] ratingList;
		int totalRating = 5;
		int count;
		
		ratingList = new int[totalRating];
		if(rating != null && rating.length() > 0){
			ratings = (Double.valueOf(rating))/2;
			int userRating = (int)ratings;
			count = 0;
			for(;count < (userRating-1); count++){
				ratingList[count] = CommonConstants.FULL_STAR;
			}
			
			if(ratings%1 != 0)
				ratingList[count++] = CommonConstants.HALF_STAR;
			
			for(; count < totalRating; count++){
				ratingList[count] = CommonConstants.EMPTY_STAR;
			}

		} else{
			for(count = 0; count < totalRating; count++){
				ratingList[count] = CommonConstants.EMPTY_STAR;
			}
		}
		return ratingList;
	}
	
	/**
	 * Calculates the rating value and returns it back
	 * @param rating
	 * @return
	 */
	private double getRatingValue(String rating){
		double ratings = 0.0;
		if(rating != null && rating.length() > 0){
			ratings = (Double.valueOf(rating))/2;
		}
		return ratings;
		
	}
	
	/** Truncates the business name to maximum length and 
	 * if truncated add three ellipses at the end
	 * @param name
	 * @return name
	 */
	private String getBusinessName(String name){
		if(name != null && name.length() > CommonConstants.MAX_LENGTH){
			name = name.substring(0,CommonConstants.MAX_LENGTH) + CommonConstants.THREE_ELLIPSES;
		}
		return name;
	}
	
	/** Truncates the tag line to maximum length and 
	 * if truncated add three ellipses at the end
	 * @param name
	 * @return tag line
	 */
	private String getTagLine(String tagLine){
		if(tagLine != null && tagLine.length() > CommonConstants.MAX_LENGTH){
			tagLine = tagLine.substring(0,CommonConstants.MAX_LENGTH) + CommonConstants.THREE_ELLIPSES;
		}
		return tagLine;
	}
	
	/**
	 * If no review count is given, returns a default value of 0
	 * @param reviewCount
	 * @return
	 */
	private int getUserReviewCount(String reviewCount){
		int userReviewCount;
		if(reviewCount == null || reviewCount.length() == 0){
			userReviewCount = 0;
		}else{
			userReviewCount = Integer.parseInt(reviewCount);
		}
		return userReviewCount;
	}
    

 }