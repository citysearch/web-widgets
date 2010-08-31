package com.citysearch.webwidget.facade.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.citysearch.webwidget.api.bean.PFPAd;
import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.PropertiesLoader;
import com.citysearch.webwidget.util.Utils;

public class NearbyPlacesHelper {
    public static List<NearbyPlace> addDefaultImages(List<NearbyPlace> nearByPlaces,
            String contextPath) throws CitysearchException {
        if (nearByPlaces != null && !nearByPlaces.isEmpty()) {
            List<String> imageList = Utils.getImages(contextPath);
            if (imageList != null && !imageList.isEmpty()) {
                ArrayList<Integer> indexList = new ArrayList<Integer>(3);
                Random randomizer = new Random();
                for (int i = 0; i < nearByPlaces.size(); i++) {
                    NearbyPlace nbp = nearByPlaces.get(i);
                    String imageUrl = nbp.getAdImageURL();
                    if (StringUtils.isBlank(imageUrl)) {
                        int index = 0;
                        do {
                            index = randomizer.nextInt(imageList.size());
                        } while (indexList.contains(index));
                        indexList.add(index);
                        imageUrl = imageList.get(index);
                        nbp.setAdImageURL(imageUrl);
                    }
                    nearByPlaces.set(i, nbp);
                }
            }
        }
        return nearByPlaces;
    }

    public static NearbyPlace toBackfill(RequestBean request, PFPAd ad) throws CitysearchException {
        NearbyPlace nbp = new NearbyPlace();
        String adUnitIdentifier = request.getAdUnitIdentifier();

        String category = ad.getCategory();
        StringBuilder tagLengthProp = new StringBuilder(adUnitIdentifier);
        tagLengthProp.append(".backfill.");
        tagLengthProp.append(CommonConstants.TAGLINE_LENGTH);
        category = Utils.getAbbreviatedString(category, tagLengthProp.toString());
        nbp.setCategory(category);

        nbp.setAdImageURL(ad.getImageUrl());

        String description = ad.getDescription();
        StringBuilder descLengthProp = new StringBuilder(adUnitIdentifier);
        descLengthProp.append(".");
        descLengthProp.append(CommonConstants.DESCRIPTION_LENGTH);
        description = Utils.getAbbreviatedString(description, descLengthProp.toString());
        nbp.setDescription(description);

        nbp.setOffers(ad.getOffers());
        nbp.setAdDisplayURL(ad.getAdDisplayUrl());
        nbp.setAdDestinationUrl(ad.getAdDestinationUrl());
        nbp.setListingId(ad.getListingId());
        nbp.setPhone(ad.getPhone());

        String adDisplayTrackingUrl = Utils.getTrackingUrl(nbp.getAdDisplayURL(),
                nbp.getAdDestinationUrl(), null, request.getDartClickTrackUrl(),
                nbp.getListingId(), nbp.getPhone(), request.getPublisher(),
                request.getAdUnitName(), request.getAdUnitSize());

        nbp.setAdDisplayTrackingURL(adDisplayTrackingUrl);

        StringBuilder displayUrlLengthProp = new StringBuilder(adUnitIdentifier);
        displayUrlLengthProp.append(".");
        displayUrlLengthProp.append(CommonConstants.DISPLAY_URL_LENGTH);
        String adDisplayUrl = nbp.getAdDisplayURL();
        adDisplayUrl = Utils.getTruncatedString(adDisplayUrl, displayUrlLengthProp.toString());
        nbp.setAdDisplayURL(adDisplayUrl);

        return nbp;
    }

    public static NearbyPlace toNearbyPlace(RequestBean request, PFPAd ad)
            throws CitysearchException {
        NearbyPlace nearbyPlace = new NearbyPlace();

        String adUnitIdentifier = request.getAdUnitIdentifier();

        StringBuilder nameLengthProp = new StringBuilder(adUnitIdentifier);
        nameLengthProp.append(".");
        nameLengthProp.append(CommonConstants.NAME_LENGTH);

        String name = ad.getName();
        name = Utils.getAbbreviatedString(name, nameLengthProp.toString());
        nearbyPlace.setName(name);

        String location = Utils.getLocationString(ad.getCity(), ad.getState());
        nearbyPlace.setLocation(location);

        String rating = ad.getRating();
        List<Integer> ratingList = Utils.getRatingsList(rating);
        double ratings = Utils.getRatingValue(rating);
        nearbyPlace.setRating(ratingList);
        nearbyPlace.setRatings(ratings);

        String reviewCount = ad.getReviewCount();
        int userReviewCount = Utils.toInteger(reviewCount);
        nearbyPlace.setReviewCount(userReviewCount);

        // Calculate distance only if lat&lon passed in request
        if (!StringUtils.isBlank(request.getLatitude())
                && !StringUtils.isBlank(request.getLongitude())
                && !StringUtils.isBlank(ad.getLatitude())
                && !StringUtils.isBlank(ad.getLongitude())) {
            BigDecimal sourceLat = new BigDecimal(request.getLatitude());
            BigDecimal sourceLon = new BigDecimal(request.getLongitude());
            BigDecimal destLat = new BigDecimal(ad.getLatitude());
            BigDecimal destLon = new BigDecimal(ad.getLongitude());
            double distance = Utils.getDistance(sourceLat, sourceLon, destLat, destLon);
            Properties appProperties = PropertiesLoader.getApplicationProperties();
            String propValue = appProperties.getProperty(CommonConstants.DISTANCE_DISPLAY_CUTOFF);
            if (!StringUtils.isBlank(propValue) && StringUtils.isNumeric(propValue)) {
                double nPropValue = Double.valueOf(propValue);
                distance = (distance > nPropValue) ? -1 : distance;
            }
            nearbyPlace.setDistance(distance);
        } else {
            nearbyPlace.setDistance(-1);
        }

        StringBuilder tagLengthProp = new StringBuilder(adUnitIdentifier);
        tagLengthProp.append(".");
        tagLengthProp.append(CommonConstants.TAGLINE_LENGTH);
        String category = ad.getCategory();
        category = Utils.getAbbreviatedString(category, tagLengthProp.toString());
        nearbyPlace.setCategory(category);

        nearbyPlace.setListingId(ad.getListingId());
        nearbyPlace.setAdDisplayURL(ad.getAdDisplayUrl());
        nearbyPlace.setAdImageURL(ad.getImageUrl());
        nearbyPlace.setPhone(ad.getPhone());
        nearbyPlace.setOffers(ad.getOffers());

        StringBuilder descLengthProp = new StringBuilder(adUnitIdentifier);
        descLengthProp.append(".");
        descLengthProp.append(CommonConstants.DESCRIPTION_LENGTH);
        String description = ad.getDescription();
        description = Utils.getAbbreviatedString(description, descLengthProp.toString());
        nearbyPlace.setDescription(description);

        nearbyPlace.setStreet(ad.getStreet());
        nearbyPlace.setCity(ad.getCity());
        nearbyPlace.setState(ad.getState());
        nearbyPlace.setPostalCode(ad.getPostalCode());
        nearbyPlace.setAdDestinationUrl(ad.getAdDestinationUrl());

        nearbyPlace.setCallBackFunction(request.getCallBackFunction());
        nearbyPlace.setCallBackUrl(request.getCallBackUrl());

        String adDisplayTrackingUrl = Utils.getTrackingUrl(nearbyPlace.getAdDisplayURL(),
                nearbyPlace.getAdDestinationUrl(), request.getCallBackUrl(),
                request.getDartClickTrackUrl(), nearbyPlace.getListingId(), nearbyPlace.getPhone(),
                request.getPublisher(), request.getAdUnitName(), request.getAdUnitSize());
        nearbyPlace.setAdDisplayTrackingURL(adDisplayTrackingUrl);

        String callBackFn = Utils.getCallBackFunctionString(request.getCallBackFunction(),
                nearbyPlace.getListingId(), nearbyPlace.getPhone());
        nearbyPlace.setCallBackFunction(callBackFn);

        StringBuilder displayUrlLengthProp = new StringBuilder(adUnitIdentifier);
        displayUrlLengthProp.append(".");
        displayUrlLengthProp.append(CommonConstants.DISPLAY_URL_LENGTH);
        String adDisplayUrl = nearbyPlace.getAdDisplayURL();
        adDisplayUrl = Utils.getTruncatedString(adDisplayUrl, displayUrlLengthProp.toString());
        nearbyPlace.setAdDisplayURL(adDisplayUrl);
        return nearbyPlace;
    }
}
