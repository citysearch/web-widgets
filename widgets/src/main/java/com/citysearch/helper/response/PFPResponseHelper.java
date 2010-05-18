package com.citysearch.helper.response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.helper.AdListBeanComparator;
import com.citysearch.helper.LogHelper;
import com.citysearch.helper.PropertiesLoader;
import com.citysearch.value.AdListBean;

public class PFPResponseHelper extends ResponseHelper{

	private Logger log;
	private final static String threeEllipses = " . . . ";
	public PFPResponseHelper(){
		log = LogHelper.getLogger(this.getClass().getName());
	}
	
	/** This method reads the values from the result xml, stores each of the businesses returned in a list,
    * does necessary manipulations and returns the list */
	public ArrayList parseXML(InputStream input,String sLat,String sLon,String imagePropertiesFile) throws IOException{
	   Document doc = null;
		Element rootElement;
		List resultSet = null;
		Element ad;
		ArrayList adList = new ArrayList();
		AdListBean adListBean;
		AdListBeanComparator beanComparator;
		int size;
		String adTag = "ad";
		
		try {
				doc = getDocumentfromStream(input);
			
			if(doc != null && doc.hasRootElement()){
			  rootElement = doc.getRootElement();
			  resultSet = rootElement.getChildren(adTag);
			  if(resultSet != null){
				size = resultSet.size();
				//Retrieving values from result xml
				for(int i = 0; i < size; i++){
					adListBean = new AdListBean();
					ad = (Element)resultSet.get(i); 
					adListBean	= processAdElement(ad,sLat,sLon);
					if (adListBean != null)
						adList.add(adListBean);
				}
			  }
			}
		} catch (IOException excep) {
			log.error(excep);
		}
		beanComparator = new AdListBeanComparator();
		Collections.sort(adList,beanComparator);
		adList = getDisplayList(adList,imagePropertiesFile);
		return adList;
	 }
	
	 /**
	    * Reads from the Ad element and constructs bean element 
	    * @param sLat 
	    * @return AdListBean
	    */
	   private AdListBean processAdElement(Element ad, String sLat,String sLon){
		    String name;
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
			
			AdListBean adListBean = null;
			int[] ratingList;
			String city;
			String state;
			String location;
			String reviewCount;
			String phone;
			String adImageURL;
			
			final String nameTag = "name";
			final String cityTag = "city";
			final String stateTag = "state";
			final String latitudeTag = "latitude";
			final String longitudeTag = "longitude";
			final String reviewRatingTag = "overall_review_rating";
			final String reviewsTag = "reviews";
			final String distanceTag = "distance";
			final String listingIdTag = "listingId";
			final String taglineTag = "tagline";
			final String adDisplayURLTag = "ad_display_url";
			final String adImageURLTag = "ad_image_url";
			final String phoneTag = "phone";
			final int extendedRadius = 25;
			
			if(ad != null){
				name = ad.getChildText(nameTag);
				if(name != null && name.length() > 0){
					city = ( ad.getChildText(cityTag));
					state = (ad.getChildText(stateTag));
					rating = ad.getChildText(reviewRatingTag);
					reviewCount = ad.getChildText(reviewsTag);
					listingId = ad.getChildText(listingIdTag);
					category = ad.getChildText(taglineTag);
					dLat = ad.getChildText(latitudeTag);
					dLon = ad.getChildText(longitudeTag);
					listingURL = ad.getChildText(adDisplayURLTag);
					phone = ad.getChildText(phoneTag);
					adImageURL = ad.getChildText(adImageURLTag);
		
					//Calculating Distance
					if(sLat != null && sLat.length() > 0 && sLon != null && sLon.length() > 0
		               && dLat != null && dLat.length() > 0 && dLon != null && dLon.length() > 0) {
		                sourceLat = Double.valueOf(sLat);
		                sourceLon = Double.valueOf(sLon);
		                destLat = Double.valueOf(dLat);
		                destLon = Double.valueOf(dLon);
		                distance = getDistance(sourceLat,sourceLon,destLat,destLon);
		            }
					
					ratingList = getRatingsList(rating);
					ratings = getRatingValue(rating);
					userReviewCount = getUserReviewCount(reviewCount);
					name = getBusinessName(name);
					category = getTagLine(category);
					location = getLocation(city,state);	
											
					//Adding to AdListBean
					if(distance < extendedRadius) {
						adListBean = new AdListBean();
						adListBean.setName(name);
						adListBean.setLocation(location);
						adListBean.setRating(ratingList);
						adListBean.setReviewCount(userReviewCount);
						adListBean.setDistance(distance);
						adListBean.setListingId(listingId.trim());
						adListBean.setCategory(category);
						adListBean.setRatings(ratings);
						adListBean.setAdDisplayURL(listingURL);
						adListBean.setAdImageURL(adImageURL);
						adListBean.setPhone(phone.trim());
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
			final String commaString = ",";
			final String spaceString = " ";
			
			if(city != null && city.length() > 0)
				location.append(city.trim());
			if(state != null && state.length() > 0){
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
		 * @param rating
		 * @return
		 */
		private int[] getRatingsList(String rating){
			double ratings = 0.0;
			int[] ratingList;
			int totalRating = 5;
			int count;
			final int emptyStar = 0;
			final int halfStar = 1;
			final int fullStar = 2;
			
			ratingList = new int[totalRating];
			if(rating != null && rating.length() > 0){
				ratings = (Double.valueOf(rating))/2;
				int userRating = (int)ratings;
				count = 0;
				for(;count < userRating; count++){
					ratingList[count] = fullStar;
				}
				
				if(ratings%1 != 0)
					ratingList[count++] = halfStar;
				
				for(; count < totalRating; count++){
					ratingList[count] = emptyStar;
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
			if(rating != null && rating.length() > 0){
				ratings = (Double.valueOf(rating))/2;
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
			final int busNameMaxLength = 18;
			if(name != null && name.length() > busNameMaxLength){
				name = name.substring(0,busNameMaxLength) + threeEllipses;
			}
			return name.trim();
		}
		
		/** Truncates the tag line to maximum length and 
		 * if truncated add three ellipses at the end
		 * @param name
		 * @return tag line
		 */
		private String getTagLine(String tagLine){
			final int tagLineMaxLength = 26;
			if(tagLine != null && tagLine.length() > tagLineMaxLength){
				tagLine = tagLine.substring(0,tagLineMaxLength) + threeEllipses;
			}
			return tagLine.trim();
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
	
		private String getDefaultImage(){
			String defaultImgURL = null;
			ArrayList<String> imageList;
			Random random;
		    ArrayList<Integer> indexList;
		    int imageListSize = 0;
		    
		    imageList = new ArrayList();
		    random = new Random();
		    indexList = new ArrayList();
		    
			int index = 0;
			imageListSize = imageList.size();
			do{
				index = random.nextInt(imageListSize);
			}while(indexList.contains(index));
			indexList.add(index);
			defaultImgURL = (String) imageList.get(index);
			return defaultImgURL;
		}
		
		private ArrayList getImageList( String imagePropertiesFile){
			Properties properties;
			String key;
			String value;
			String values[];
			ArrayList imageList = new ArrayList();
			properties = PropertiesLoader.getProperties(imagePropertiesFile);
			Enumeration enumerator = properties.keys();
			while(enumerator.hasMoreElements()){
				key = (String) enumerator.nextElement();
				value = properties.getProperty(key);
				imageList.add(value);
			}
			return imageList;
		}
		
		/**
		 * Returns list with only three objects if the size is grater
		 * @param adList
		 * @param imagePropertiesFile
		 * @return
		 */
		private ArrayList getDisplayList(ArrayList adList, String imagePropertiesFile){
			final int displaySize = 3;
			ArrayList displayList = new ArrayList(3);
			if(adList.size() > 3){
				for(int i = 0; i < displaySize;i++){
					displayList.add(adList.get(i));
				}
			}else{
				displayList = adList;
			}
			displayList = addDefaultImages(displayList,imagePropertiesFile);
			return displayList;
		}
		
		/**
		 * Add default images to the final list
		 * @param adList
		 * @param imagePropertiesFile
		 * @return
		 */
		private ArrayList addDefaultImages(ArrayList adList, String imagePropertiesFile){
			AdListBean adListBean;
			ArrayList imageList;
			Random random;
		    ArrayList<Integer> indexList = new ArrayList(3);
		    int imageListSize = 0;
		    String imageUrl;
		    
		    imageList = getImageList(imagePropertiesFile);
			random = new Random();
			int size = adList.size();
			
			for(int i = 0; i < size;i++){
				adListBean = (AdListBean) adList.get(i);
				imageUrl = adListBean.getAdImageURL();
				if(imageUrl == null || imageUrl.length() ==0){
					int index = 0;
					imageListSize = imageList.size();
					do{
						index = random.nextInt(imageListSize);
					}while(indexList.contains(index));
					indexList.add(index);
					imageUrl = (String) imageList.get(index);
					adListBean.setAdImageURL(imageUrl);
				}
				adList.set(i, adListBean);
			}
			
			return adList;
		}
}
