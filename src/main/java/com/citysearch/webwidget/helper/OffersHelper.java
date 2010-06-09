package com.citysearch.webwidget.helper;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.OffersRequest;
import com.citysearch.webwidget.bean.SearchRequest;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.APIFieldNameConstants;
import com.citysearch.webwidget.util.CommonConstants;
import com.citysearch.webwidget.util.HelperUtil;
import com.citysearch.webwidget.util.PropertiesLoader;

public class OffersHelper {

    private final static String PROPERTY_OFFERS_URL = "offers.url";
    private Logger log = Logger.getLogger(getClass());
    private String rootPath;

    public OffersHelper(String rootPath) {
        this.rootPath = rootPath;
    }

    private String getQueryString(OffersRequest request) throws CitysearchException {

        StringBuilder strBuilder = new StringBuilder();

        Properties properties = PropertiesLoader.getAPIProperties();
        String apiKey = properties.getProperty(CommonConstants.API_KEY_PROPERTY);

        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.API_KEY, apiKey));

        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PUBLISHER,
                request.getPublisher()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHAT,
                request.getWhat()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.WHERE,
                request.getWhere()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.TAG,
                request.getTag()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LATITUDE,
                request.getLatitude()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.LONGITUDE,
                request.getLongitude()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.PAGE,
                request.getPage()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RPP,
                request.getRpp()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.EXPIRES_BEFORE,
                request.getExpires_before()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CUSTOMER_HASBUDGET,
                String.valueOf(request.isCustomer_hasbudget())));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.RADIUS,
                request.getRadius()));
        strBuilder.append(CommonConstants.SYMBOL_AMPERSAND);
        strBuilder.append(HelperUtil.constructQueryParam(APIFieldNameConstants.CALLBACK,
                request.getCallback()));

        return strBuilder.toString();
    }

}
