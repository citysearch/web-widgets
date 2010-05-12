package com.citysearch.helper.response;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.apache.commons.lang.StringUtils;

import com.citysearch.exception.CitySearchException;
import com.citysearch.helper.PropertiesLoader;
import com.citysearch.value.AdListBean;

public class PfpResponseHelper extends ResponseHelper{

	private Logger log = Logger.getLogger(getClass());
	private static final String nameTag = "name";
	private static final String cityTag = "city";
	private static final String stateTag = "state";
	private static final String latitudeTag = "latitude";
	private static final String longitudeTag = "longitude";
	private static final String reviewRatingTag = "overall_review_rating";
	private static final String reviewsTag = "reviews";
	private static final String listingIdTag = "listingId";
	private static final String taglineTag = "tagline";
	private static final String adDisplayURLTag = "ad_display_url";
	private static final String adImageURLTag = "ad_image_url";
	private static final String phoneTag = "phone";
	private static final int extendedRadius = 25;
	private static final int displaySize = 3;
	private static final String commaString = ",";
	private static final String spaceString = " ";
	private static final int busNameMaxLength = 21;
	private static final int tagLineMaxLength = 26;
	private static final String listingId = "?listing_id=";
	private static final double kmToMile = 0.622;
	private static final int radius = 6371; 
	
		
	/** This method reads the values from the result xml, stores each of the businesses returned in a list,
    * does necessary manipulations and returns the list 
	 * @throws CitySearchException */
	public ArrayList<AdListBean> parseXML(InputStream input,String sLat,String sLon,String callbackURL)
																		throws CitySearchException{
		ArrayList<AdListBean> adList = new ArrayList<AdListBean>();
		try {
			Document doc = getDocumentfromStream(input);
			if(doc != null && doc.hasRootElement()){
				Element rootElement = doc.getRootElement();
				String adTag = "ad";
				List<Element> resultSet =  rootElement.getChildren(adTag);
				if(resultSet != null){
					int size = resultSet.size();
					//Retrieving values from result xml
					for(int i = 0; i < size; i++){
						AdListBean adListBean = new AdListBean();
						Element ad = (Element)resultSet.get(i); 
						adListBean	= processAdElement(ad,sLat,sLon);
						if (adListBean != null)
							adList.add(adListBean);
					}
			  }
			}
		} catch (IOException excep) {
			log.error(excep);
			throw new CitySearchException();
		}
		Collections.sort(adList);
		adList = getDisplayList(adList,callbackURL);
		return adList;
	 }
	
	 /**
	    * Reads from the Ad element and constructs bean element 
	    * @param sLat 
	    * @return AdListBean
	    */
	   private AdListBean processAdElement(Element ad, String sLat,String sLon){
		    		
			AdListBean adListBean = null;
			if(ad != null){
				 String name = ad.getChildText(nameTag);
				if(StringUtils.isNotBlank(name)){
					String city = ( ad.getChildText(cityTag));
					String state = (ad.getChildText(stateTag));
					String rating = ad.getChildText(reviewRatingTag);
					String reviewCount = ad.getChildText(reviewsTag);
					String listingId = ad.getChildText(listingIdTag);
					String category = ad.getChildText(taglineTag);
					String dLat = ad.getChildText(latitudeTag);
					String dLon = ad.getChildText(longitudeTag);
					String phone = ad.getChildText(phoneTag);
							
					//Calculating Distance
					double distance =  0.0;
					if(StringUtils.isNotBlank(sLat) && StringUtils.isNotBlank(sLon)
							    && StringUtils.isNotBlank(dLat) && StringUtils.isNotBlank(dLon)) {
						BigDecimal sourceLat = new BigDecimal(sLat);
						BigDecimal sourceLon = new BigDecimal(sLon);
						BigDecimal destLat = new BigDecimal(dLat);
						BigDecimal destLon = new BigDecimal(dLon);
						distance  = getDistance(sourceLat,sourceLon,destLat,destLon);
		            }
					
					int[] ratingList = getRatingsList(rating);
					double ratings = getRatingValue(rating);
					int userReviewCount = getUserReviewCount(reviewCount);
					name = getBusinessName(name);
					category = getTagLine(category);
					String location = getLocation(city,state);	
											
					//Adding to AdListBean
					if(distance < extendedRadius) {
						adListBean = new AdListBean();
						adListBean.setName(name);
						adListBean.setLocation(location);
						adListBean.setRating(ratingList);
						adListBean.setReviewCount(userReviewCount);
						adListBean.setDistance(distance);
						adListBean.setListingId(StringUtils.trim(listingId.trim()));
						adListBean.setCategory(category);
						adListBean.setRatings(ratings);
						adListBean.setAdDisplayURL(ad.getChildText(adDisplayURLTag));
						adListBean.setAdImageURL(ad.getChildText(adImageURLTag));
						adListBean.setPhone(StringUtils.trim(phone));
					}
				}
			}
			return adListBean;
	   }

	  	/**
	  	 * Constructs the String as city,state and returns it.
	  	 * If city is not present then only state is returned and vice-versa
	  	 * @param city
	  	 * @param state
	  	 * @return
	  	 */
		private String getLocation(String city, String state) {
			StringBuffer location = new StringBuffer();
			if(StringUtils.isNotBlank(city))
				location.append(city.trim());
			if(StringUtils.isNotBlank(state)){
				if(location.length() > 0){
					location.append(commaString);
					location.append(spaceString);
				}
				location.append(state.trim());
			}
			return location.toString();
		}	


		/**
		 * This method takes the source latitude, longitude and destination latitude, longitude to calculate 
		 * the distance between two points 
		 */
		private double getDistance(BigDecimal sourceLat,BigDecimal sourceLon,BigDecimal destLat,BigDecimal destLon){

			double distance = 0.0;
			double diffOfLat = Math.toRadians(destLat.doubleValue()-sourceLat.doubleValue());  
			double diffOfLon = Math.toRadians(destLon.doubleValue()-sourceLon.doubleValue()); 
			double sourceLatRad = Math.toRadians(sourceLat.doubleValue());
			double destLatRad = Math.toRadians(destLat.doubleValue());

			double calcResult = Math.sin(diffOfLat/2) * Math.sin(diffOfLat/2) +
						Math.cos(sourceLatRad) * Math.cos(destLatRad) * 
						Math.sin(diffOfLon/2) * Math.sin(diffOfLon/2); 
			
			calcResult = 2 * Math.atan2(Math.sqrt(calcResult), Math.sqrt(1-calcResult)); 
			distance = radius * calcResult; 
			//Converting from kms to Miles
			distance = distance * kmToMile;
			//Rounding to one decimal place
			distance = Math.floor(distance * 10) / 10.0;
			return distance;
		}
		
		/**
		 * Calculate the ratings value and determines the rating stars to be displayed 
		 * Returns what type of star to be displayed in an array
		 * E.g.for 3.5 rating the array will have values {2,2,2,1,0} where
		 * 2 represents full star, 1 half star and 0 empty star
		 * @param rating
		 * @return
		 */
		private int[] getRatingsList(String rating){
			final int totalRating = 5;
			final int emptyStar = 0;
			final int halfStar = 1;
			final int fullStar = 2;
			
			int[] ratingList = new int[totalRating];
			int count = 0;
			if(StringUtils.isNotBlank(rating)){
				double ratings = (Double.parseDouble(rating))/2;
				int userRating = (int)ratings;
				while(count < userRating){
					ratingList[count++] = fullStar;
				}
				
				if(ratings%1 != 0)
					ratingList[count++] = halfStar;
				
				while( count < totalRating){
					ratingList[count++] = emptyStar;
				}

			} else{
				for(count = 0; count < totalRating; count++){
					ratingList[count] = emptyStar;
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
			if(StringUtils.isNotBlank(rating)){
				ratings = (Double.parseDouble(rating))/2;
				ratings = Math.floor(ratings * 10) / 10.0;
			}
			return ratings;
			
		}
		
		/** Truncates the business name to maximum length and 
		 * if truncated add three ellipses at the end
		 * @param name
		 * @return name
		 */
		private String getBusinessName(String name){
			name = StringUtils.abbreviate(name, busNameMaxLength);
			return name.trim();
		}
		
		/** Truncates the tag line to maximum length and 
		 * if truncated add three ellipses at the end
		 * @param name
		 * @return tag line
		 */
		private String getTagLine(String tagLine){
			tagLine = StringUtils.abbreviate(tagLine, tagLineMaxLength);
			return tagLine.trim();
		}
		
		/**
		 * If no review count is given, returns a default value of 0
		 * @param reviewCount
		 * @return
		 */
		private int getUserReviewCount(String reviewCount){
			int userReviewCount = 0;
			if(StringUtils.isNotBlank(reviewCount)){
				userReviewCount = Integer.parseInt(reviewCount);
			}
			return userReviewCount;
		}
	
		/**
		 * Getting the images from the properties file, adding and
		 * returning in a ArrayList
		 * @param imagePropertiesFile
		 * @return
		 */
		private ArrayList<String> getImageList(){
			ArrayList<String> imageList = new ArrayList<String>();
			Properties properties = PropertiesLoader.imageProperties;
			if(properties != null){
				Enumeration<Object> enumerator = properties.keys();
				while(enumerator.hasMoreElements()){
					String key = (String) enumerator.nextElement();
					String value = properties.getProperty(key);
					imageList.add(value);
				}
			}
			return imageList;
		}
		
		/**
		 * Returns list with only three objects if the size is greater than 3
		 * Otherwise, returns the list as is
		 * @param adList
		 * @param imagePropertiesFile
		 * @return
		 */
		private ArrayList<AdListBean> getDisplayList(ArrayList<AdListBean> adList,String callbackURL){
			
			ArrayList<AdListBean> displayList = new ArrayList<AdListBean>(3);
			if(adList.size() > 3){
				for(int i = 0; i < displaySize;i++){
					displayList.add(adList.get(i));
				}
			}else{
				displayList = adList;
			}
			displayList = addDefaultImagesAndCallBackURL(displayList,callbackURL);
			return displayList;
		}
		
		/**
		 * Add default images to the final list
		 * Read the images from the list in a random order
		 * @param adList
		 * @param imagePropertiesFile
		 * @return
		 */
		private ArrayList<AdListBean> addDefaultImagesAndCallBackURL(ArrayList<AdListBean> adList,String callbackURL){
			AdListBean adListBean;
			ArrayList<String> imageList;
			Random random;
		    ArrayList<Integer> indexList = new ArrayList<Integer>(3);
		    int imageListSize = 0;
		    String imageUrl;
		    
		    imageList = getImageList();
			random = new Random();
			int size = adList.size();
			
			for(int i = 0; i < size;i++){
				adListBean = adList.get(i);
				imageUrl = adListBean.getAdImageURL();
				if(StringUtils.isBlank(imageUrl)){
					int index = 0;
					imageListSize = imageList.size();
					do{
						index = random.nextInt(imageListSize);
					}while(indexList.contains(index));
					indexList.add(index);
					imageUrl = imageList.get(index);
					adListBean.setAdImageURL(imageUrl);
				}
				if(StringUtils.isNotBlank(callbackURL)){
					callbackURL = callbackURL + listingId + adListBean.getListingId();
					adListBean.setAdDisplayURL(callbackURL);
				}
				adList.set(i, adListBean);
			}
			
			return adList;
		}
}
