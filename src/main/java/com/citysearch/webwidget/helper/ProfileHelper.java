package com.citysearch.webwidget.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.Address;
import com.citysearch.webwidget.bean.ProfileRequest;
import com.citysearch.webwidget.bean.Review;
import com.citysearch.webwidget.bean.ReviewRequest;
import com.citysearch.webwidget.bean.ReviewResponse;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;

public class ProfileHelper extends AbstractHelper {

    private final static String property_profile_url = "profile.url";
    private static final String client_IP_err_msg = "clientip.errmsg";
    private static final String listingid_err_msg = "listingid.errmsg";
    protected static final String location = "location";
    private static final String street = "street";
    private static final String city = "city";
    private static final String state = "state";
    private static final String postalCode = "postal_code";
    private static final String address = "address";
    private static final String contactInfo = "contact_info";
    private static final String phone = "display_phone";
    private static final String urls = "urls";
    private static final String profileURL = "profile_url";
    private static final String sendToFriendURL = "send_to_friend_url";
    private static final String images = "images";
    private static final String image = "image";
    private static final String imageURL = "image_url";
    private static List<String> imageList;
    private static final String imagePropertiesFile = "review.image.properties";

    private ProfileRequest request;

    public ProfileHelper(ProfileRequest request) {
        this.request = request;
    }

    public ProfileHelper(ReviewRequest request, Review review) {
        this.request = new ProfileRequest();
        this.request.setApi_key(request.getApi_key());
        this.request.setPublisher(request.getPublisher());
        // Hardcoded for now as it is not required for Reviews API
        this.request.setClientIP("122.123.124.125");
        this.request.setListingId(review.getListing_id());
    }

    /**
     * Validates the request. If any of the parameters are missing, throws Citysearch Exception
     * 
     * @throws CitysearchException
     */
    public void validateRequest() throws CitysearchException {
        List<String> errors = new ArrayList<String>();
        Properties errorProperties = PropertiesLoader.getErrorProperties();
        if (StringUtils.isBlank(request.getApi_key())) {
            errors.add(errorProperties.getProperty(CommonConstants.API_KEY_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getPublisher())) {
            errors.add(errorProperties.getProperty(CommonConstants.PUBLISHER_ERROR_CODE));
        }
        if (StringUtils.isBlank(request.getListingId())) {
            errors.add(errorProperties.getProperty(listingid_err_msg));
        }
        if (StringUtils.isBlank(request.getClientIP())) {
            errors.add(errorProperties.getProperty(client_IP_err_msg));
        }
        if (!errors.isEmpty()) {
            throw new CitysearchException(this.getClass().getName(), "validateRequest",
                    "Invalid parameters.", errors);
        }
    }

    /**
     * Constructs the Profile API query string with all the supplied parameters
     * 
     * @return
     * @throws CitysearchException
     */
    private String getQueryString() throws CitysearchException {
        // Reflection Probably???
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(constructQueryParam(APIFieldNameConstants.API_KEY, request.getApi_key()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.LISTING_ID,
                request.getListingId()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.INFOUSA_ID,
                request.getInfoUSAId()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.PHONE, request.getPhone()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.ALL_RESULTS,
                String.valueOf(request.isAllResults())));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.CUSTOMER_ONLY,
                String.valueOf(request.isCustomerOnly())));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.REVIEW_COUNT,
                request.getReviewCount()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.CLIENT_IP,
                request.getClientIP()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.CALLBACK, request.getCallback()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.PLACEMENT,
                request.getPlacement()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.FORMAT, request.getFormat()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(constructQueryParam(APIFieldNameConstants.NO_LOG,
                String.valueOf(request.getNolog())));
        return strBuilder.toString();
    }

    /**
     * Connects to the Profile API and processes the response sent by API for Reviews Response
     * 
     * @return
     * @throws CitysearchException
     */
    public ReviewResponse getProfileForReviews(Review review) throws CitysearchException {
        validateRequest();
        Properties properties = PropertiesLoader.getAPIProperties();
        String urlString = properties.getProperty(property_profile_url) + getQueryString();
        Document responseDocument = getAPIResponse(urlString);
        Review reviewObj = parseProfileForReviews(responseDocument, review);
        return new ReviewResponse(reviewObj);
    }

    /**
     * Parses xml response and returns the Reviews object
     * 
     * @param doc
     * @param review
     * @return
     * @throws CitysearchException
     */
    public Review parseProfileForReviews(Document doc, Review review) throws CitysearchException {
        if (doc != null && doc.hasRootElement()) {
            try{
                Element locationElem = doc.getRootElement().getChild(location);
                if (locationElem != null) {
                    review.setAddress(getAddress(locationElem.getChild(address)));
                    review.setPhone(getPhone(locationElem.getChild(contactInfo)));
                    Element url = locationElem.getChild(urls);
                    if (url != null) {
                        review.setProfile_url(url.getChildText(profileURL));
                        review.setSend_to_friend_url(url.getChildText(sendToFriendURL));
                    }
                    review.setImage_url(getImage(locationElem.getChild(images)));
                }
            }catch(Exception excep){
                throw new CitysearchException(this.getClass().getName(),
                        "parseProfileForReviews", excep.getMessage());
            }
        }
        return review;
    }

    /**
     * Parses the address element received in response
     * 
     * @param addressElem
     * @return
     * @throws CitysearchException 
     */
    private Address getAddress(Element addressElem) throws CitysearchException {
        Address address = null;
        if (addressElem != null) {
            try{
                address = new Address();
                address.setStreet(addressElem.getChildText(street));
                address.setCity(addressElem.getChildText(city));
                address.setState(addressElem.getChildText(state));
                address.setPostal_code(addressElem.getChildText(postalCode));
            }catch(Exception excep){
                throw new CitysearchException(this.getClass().getName(),
                        "getAddress", excep.getMessage());
            }
        }
        return address;
    }

    /**
     * Gets the phone number from contact info element
     * 
     * @param contactInfo
     * @return
     */
    private String getPhone(Element contactInfo) {
        if (contactInfo != null) {
            return contactInfo.getChildText(phone);
        }
        return null;
    }

    /**
     * Gets the image url from xml. If no image url is found, returns the stock image related to the
     * business category
     * 
     * @param images
     * @return
     * @throws CitysearchException
     */
    private String getImage(Element images) throws CitysearchException {
        String imageurl = null;
        try{
            if (images != null) {
                List<Element> imageList = images.getChildren(image);
                int size = imageList.size();
                for (int index = 0; index < size; index++) {
                    Element image = imageList.get(index);
                    if (image != null) {
                        imageurl = image.getChildText(imageURL);
                        if (StringUtils.isNotBlank(imageurl)) {
                            break;
                        }
                    }
                }
            }
            /*
             * if (StringUtils.isBlank(imageurl)) { if (imageList == null) { imageList =
             * getImageList(imagePropertiesFile); } if (imageList != null) { int listSize =
             * imageList.size(); int index = new Random().nextInt(listSize); imageurl =
             * imageList.get(index); } }
             */
        }catch(Exception excep){
            throw new CitysearchException(this.getClass().getName(),
                    "getImage", excep.getMessage());
        }
        return imageURL;
    }
}
