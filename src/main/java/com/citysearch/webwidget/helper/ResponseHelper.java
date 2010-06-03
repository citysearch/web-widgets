package com.citysearch.webwidget.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.citysearch.webwidget.bean.AdListBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

/**
 * Helper class for processing response of Search and Profile APIs
 * @author Aspert
 *
 */
public abstract class ResponseHelper {

    private Logger log = Logger.getLogger(getClass());
    private static final String ioExcepMsg = "streamread.error";
    private static final String jdomExcepMsg = "jdom.excep.msg";
    private static final String imagesPropertiesFile = "images.properties";
    private static final String adTag = "ad";
    private static final String locationTag = "location";
    private static final int displaySize = 3;
    private static final String commaString = ",";
    private static final String spaceString = " ";
    private static final String busNameMaxLengthProp = "name.length";
    private static final String taglineMaxLengthProp = "tagline.length";
    private static final int busNameMaxLength = 30;
    private static final int tagLineMaxLength = 30;
    private static final double kmToMile = 0.622;
    private static final int radius = 6371;
    private static final int totalRating = 5;
    private static final int emptyStar = 0;
    private static final int halfStar = 1;
    private static final int fullStar = 2;
    private static final String imageError = "image.properties.error";
    private static Properties imageProperties;
    private static final String apiTypeError = "invalid.apitype";
    protected static final int extendedRadius = 25;

    /**
     * Reads from input stream, constructs and returns a jdom document
     * 
     * @param input
     * @return
     * @throws IOException
     * @throws IOException
     * @throws CitysearchException
     */
    public Document getDocumentfromStream(InputStream input) throws IOException,
            CitysearchException {
        Document document = null;
        try {
            if (input != null) {
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(input);
            }
        } catch (JDOMException excep) {
            log.error(PropertiesLoader.getErrorProperties().getProperty(jdomExcepMsg), excep);
        } catch (IOException ioExcep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(ioExcepMsg);
            log.error(errMsg, ioExcep);
            throw new CitysearchException(this.getClass().getName(), "getDocumentfromStream", errMsg);
        } finally {
            input.close();
        }
        return document;
    }

    /**
     * Converts InputStream to String and returns the String
     * 
     * @throws CitysearchException
     */
    public String getStringFromStream(InputStream input) throws IOException, CitysearchException {
        StringBuilder sb = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException ioe) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(ioExcepMsg);
            log.error(errMsg, ioe);
            throw new CitysearchException(this.getClass().getName(), "getStringFromStream", errMsg);
        } finally {
            input.close();
        }
        return sb.toString();
    }

    /**
     * This method reads the values from the result xml, stores each of the businesses returned in a
     * list, does necessary manipulations and returns the list
     * 
     * @throws CitysearchException
     */
    public ArrayList<AdListBean> parseXML(Document doc, String sLat, String sLon, String apiType,
            String contextPath) throws CitysearchException {
        ArrayList<AdListBean> adList = new ArrayList<AdListBean>();
        try {
            if (doc != null && doc.hasRootElement()) {
                Element rootElement = doc.getRootElement();
                String childElem = getApiChildElementName(apiType);
                List<Element> resultSet = rootElement.getChildren(childElem);
                if (resultSet != null) {
                    int size = resultSet.size();
                    HashMap<String, String> resultMap;
                    // Retrieving values from result xml
                    for (int i = 0; i < size; i++) {
                        AdListBean adListBean = new AdListBean();
                        Element ad = (Element) resultSet.get(i);
                        resultMap = processElement(ad);
                        adListBean = processMap(resultMap, sLat, sLon);
                        if (adListBean != null)
                            adList.add(adListBean);
                    }
                }
            }
        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                    CommonConstants.ERROR_METHOD_PARAM)
                    + " parseXML()";
            log.error(errMsg, excep);
            throw new CitysearchException(this.getClass().getName(), "parseXML", excep.getMessage());
        }
        Collections.sort(adList);
        adList = getDisplayList(adList, contextPath);
        return adList;
    }

    /**
     * Map contains the element values parsed from result xml .Does the necessary processing to
     * values and adds to AdListBean object
     * 
     * @param resultMap
     * @param sLat
     * @param sLon
     * @return
     * @throws CitysearchException
     */
    private AdListBean processMap(HashMap<String, String> resultMap, String sLat, String sLon)
            throws CitysearchException {
        AdListBean adListBean = null;
        if (resultMap != null) {
            // Calculating Distance
            double distance = 0.0;
            String dLat = resultMap.get(CommonConstants.DLAT);
            String dLon = resultMap.get(CommonConstants.DLON);
            String rating = resultMap.get(CommonConstants.RATING);
            String reviewCount = resultMap.get(CommonConstants.REVIEWCOUNT);
            String listingId = resultMap.get(CommonConstants.LISTING_ID);
            String category = resultMap.get(CommonConstants.CATEGORY);
            String name = resultMap.get(CommonConstants.NAME);
            String phone = resultMap.get(CommonConstants.PHONE);
            if (StringUtils.isNotBlank(sLat) && StringUtils.isNotBlank(sLon)
                    && StringUtils.isNotBlank(dLat) && StringUtils.isNotBlank(dLon)) {
                BigDecimal sourceLat = new BigDecimal(sLat);
                BigDecimal sourceLon = new BigDecimal(sLon);
                BigDecimal destLat = new BigDecimal(dLat);
                BigDecimal destLon = new BigDecimal(dLon);
                distance = getDistance(sourceLat, sourceLon, destLat, destLon);
            }

            int[] ratingList = getRatingsList(rating);
            double ratings = getRatingValue(rating);
            int userReviewCount = getUserReviewCount(reviewCount);
            name = getBusinessName(name);
            category = getTagLine(category);
            String location = getLocation(resultMap.get(CommonConstants.CITY),
                    resultMap.get(CommonConstants.STATE));

            // Adding to AdListBean
            if (distance < extendedRadius) {
                adListBean = new AdListBean();
                adListBean.setName(name);
                adListBean.setLocation(location);
                adListBean.setRating(ratingList);
                adListBean.setReviewCount(userReviewCount);
                adListBean.setDistance(distance);
                adListBean.setListingId(StringUtils.trim(listingId));
                adListBean.setCategory(category);
                adListBean.setRatings(ratings);
                adListBean.setAdDisplayURL(resultMap.get(CommonConstants.DISPLAY_URL));
                adListBean.setAdImageURL(resultMap.get(CommonConstants.IMAGE_URL));
                adListBean.setPhone(StringUtils.trim(phone));
            }
        }
        return adListBean;
    }

    /**
     * Reads the apiType and returns the name of the child element to be processed
     * 
     * @param apiType
     * @return
     * @throws CitysearchException
     */
    private String getApiChildElementName(String apiType) throws CitysearchException {
        String childName;
        if (apiType.equalsIgnoreCase(CommonConstants.PFP_API_TYPE)) {
            childName = adTag;
        } else if (apiType.equalsIgnoreCase(CommonConstants.SEARCH_API_TYPE)) {
            childName = locationTag;
        } else {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(apiTypeError);
            log.error(errMsg);
            throw new CitysearchException(this.getClass().getName(), "getApiChildElementName", errMsg);
        }
        return childName;
    }

    protected abstract HashMap<String, String> processElement(Element element);

    /**
     * Constructs the String as city,state and returns it. If city is not present then only state is
     * returned and vice-versa
     * 
     * @param city
     * @param state
     * @return
     */
    protected String getLocation(String city, String state) {
        StringBuffer location = new StringBuffer();
        if (StringUtils.isNotBlank(city))
            location.append(city.trim());
        if (StringUtils.isNotBlank(state)) {
            if (location.length() > 0) {
                location.append(commaString);
                location.append(spaceString);
            }
            location.append(state.trim());
        }
        return location.toString();
    }

    /**
     * This method takes the source latitude, longitude and destination latitude, longitude to
     * calculate the distance between two points
     */
    protected double getDistance(BigDecimal sourceLat, BigDecimal sourceLon, BigDecimal destLat,
            BigDecimal destLon) {

        double distance = 0.0;
        double diffOfLat = Math.toRadians(destLat.doubleValue() - sourceLat.doubleValue());
        double diffOfLon = Math.toRadians(destLon.doubleValue() - sourceLon.doubleValue());
        double sourceLatRad = Math.toRadians(sourceLat.doubleValue());
        double destLatRad = Math.toRadians(destLat.doubleValue());

        double calcResult = Math.sin(diffOfLat / 2) * Math.sin(diffOfLat / 2)
                + Math.cos(sourceLatRad) * Math.cos(destLatRad) * Math.sin(diffOfLon / 2)
                * Math.sin(diffOfLon / 2);

        calcResult = 2 * Math.atan2(Math.sqrt(calcResult), Math.sqrt(1 - calcResult));
        distance = radius * calcResult;
        // Converting from kms to Miles
        distance = distance * kmToMile;
        // Rounding to one decimal place
        distance = Math.floor(distance * 10) / 10.0;
        return distance;
    }

    /**
     * Calculate the ratings value and determines the rating stars to be displayed Returns what type
     * of star to be displayed in an array E.g.for 3.5 rating the array will have values {2,2,2,1,0}
     * where 2 represents full star, 1 half star and 0 empty star
     * 
     * @param rating
     * @return
     */
    protected int[] getRatingsList(String rating) {
        int[] ratingList = new int[totalRating];
        int count = 0;
        if (StringUtils.isNotBlank(rating)) {
            double ratings = (Double.parseDouble(rating)) / 2;
            int userRating = (int) ratings;
            while (count < userRating) {
                ratingList[count++] = fullStar;
            }

            if (ratings % 1 != 0)
                ratingList[count++] = halfStar;

            while (count < totalRating) {
                ratingList[count++] = emptyStar;
            }

        } else {
            for (count = 0; count < totalRating; count++) {
                ratingList[count] = emptyStar;
            }
        }
        return ratingList;
    }

    /**
     * Calculates the rating value and returns it back
     * 
     * @param rating
     * @return
     */
    protected double getRatingValue(String rating) {
        double ratings = 0.0;
        if (StringUtils.isNotBlank(rating)) {
            ratings = (Double.parseDouble(rating)) / 2;
            ratings = Math.floor(ratings * 10) / 10.0;
        }
        return ratings;

    }

    /**
     * Truncates the business name to maximum length and if truncated add three ellipses at the end
     * Reads the length from the property file.
     * 
     * @param name
     * @return name
     * @throws CitysearchException
     */
    protected String getBusinessName(String name) throws CitysearchException {
        String value = PropertiesLoader.getAPIProperties().getProperty(busNameMaxLengthProp);
        int length = busNameMaxLength;
        if (StringUtils.isNotBlank(value)) {
            try {
                length = Integer.parseInt(value);
            } catch (Exception excep) {
                String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                        CommonConstants.ERROR_METHOD_PARAM)
                        + "getBusinessName()";
                log.error(errMsg, excep);
                throw new CitysearchException(this.getClass().getName(), "getBusinessName", excep.getMessage());
            }
        }
        name = StringUtils.abbreviate(name, length);
        return StringUtils.trimToEmpty(name);
    }

    /**
     * Truncates the tag line to maximum length and if truncated add three ellipses at the end
     * 
     * @param name
     * @return tag line
     * @throws CitysearchException
     */
    protected String getTagLine(String tagLine) throws CitysearchException {
        String value = PropertiesLoader.getAPIProperties().getProperty(taglineMaxLengthProp);
        int length = tagLineMaxLength;
        if (StringUtils.isNotBlank(value)) {
            try {
                length = Integer.parseInt(value);
            } catch (Exception excep) {
                String errMsg = PropertiesLoader.getErrorProperties().getProperty(
                        CommonConstants.ERROR_METHOD_PARAM)
                        + "getTagLine()";
                log.error(errMsg, excep);
                throw new CitysearchException(this.getClass().getName(), "getTagLine", excep.getMessage());
            }
        }
        tagLine = StringUtils.abbreviate(tagLine, length);
        return StringUtils.trimToEmpty(tagLine);
    }

    /**
     * If no review count is given, returns a default value of 0
     * 
     * @param reviewCount
     * @return
     */
    protected int getUserReviewCount(String reviewCount) {
        int userReviewCount = 0;
        if (StringUtils.isNotBlank(reviewCount)) {
            userReviewCount = Integer.parseInt(reviewCount);
        }
        return userReviewCount;
    }

    /**
     * Getting the images from the properties file, adding and returning in a ArrayList
     * 
     * @param imagePropertiesFile
     * @return
     * @throws CitysearchException
     */
    private ArrayList<String> getImageList(String contextPath) throws CitysearchException {
        ArrayList<String> imageList = new ArrayList<String>();
        try {
            if (imageProperties == null) {
                imageProperties = PropertiesLoader.getProperties(imagesPropertiesFile);
            }
            Enumeration<Object> enumerator = imageProperties.keys();
            while (enumerator.hasMoreElements()) {
                String key = (String) enumerator.nextElement();
                String value = imageProperties.getProperty(key);
                imageList.add(contextPath + "/" + value);
            }

        } catch (Exception excep) {
            String errMsg = PropertiesLoader.getErrorProperties().getProperty(imageError);
            log.error(errMsg);
        }

        return imageList;
    }

    /**
     * Returns list with only three objects if the size is greater than 3 Otherwise, returns the
     * list as is
     * 
     * @param adList
     * @param imagePropertiesFile
     * @return
     * @throws CitysearchException
     */
    protected ArrayList<AdListBean> getDisplayList(ArrayList<AdListBean> adList, String contextPath)
            throws CitysearchException {
        ArrayList<AdListBean> displayList = new ArrayList<AdListBean>(3);
        if (adList.size() > 3) {
            for (int i = 0; i < displaySize; i++) {
                displayList.add(adList.get(i));
            }
        } else {
            displayList = adList;
        }
        displayList = addDefaultImages(displayList, contextPath);
        return displayList;
    }

    /**
     * Add default images to the final list Read the images from the list in a random order
     * 
     * @param adList
     * @param imagePropertiesFile
     * @return
     * @throws CitysearchException
     */
    private ArrayList<AdListBean> addDefaultImages(ArrayList<AdListBean> adList, String contextPath)
            throws CitysearchException {
        AdListBean adListBean;
        ArrayList<String> imageList;
        Random random;
        ArrayList<Integer> indexList = new ArrayList<Integer>(3);
        int imageListSize = 0;
        String imageUrl = "";

        imageList = getImageList(contextPath);
        random = new Random();
        int size = adList.size();

        for (int i = 0; i < size; i++) {
            adListBean = adList.get(i);
            imageUrl = adListBean.getAdImageURL();
            if (StringUtils.isBlank(imageUrl)) {
                int index = 0;
                imageListSize = imageList.size();
                if (imageListSize > 0) {
                    do {
                        index = random.nextInt(imageListSize);
                    } while (indexList.contains(index));
                    indexList.add(index);
                    imageUrl = imageList.get(index);
                    adListBean.setAdImageURL(imageUrl);
                }
            }
            adList.set(i, adListBean);
        }

        return adList;
    }

}
