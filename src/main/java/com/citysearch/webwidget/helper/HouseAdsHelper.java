package com.citysearch.webwidget.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.HelperUtil;

public class HouseAdsHelper {
    private static List<HouseAd> houseAds;

    public static List<HouseAd> getHouseAds() throws CitysearchException {
        InputStream inputStream = null;
        if (houseAds == null) {
            inputStream = HouseAdsHelper.class.getClassLoader().getResourceAsStream(
                    "/HouseAdsConfig.xml");
            Document document;
            try {
                document = HelperUtil.buildFromStream(inputStream);
            } catch (IOException ioe) {
                throw new CitysearchException("HouseAdsHelper", "getHouseAds", ioe);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ioe) {
                    throw new CitysearchException("HouseAdsHelper", "getHouseAds", ioe);
                }
            }
            houseAds = buildHouseAds(document);
        }
        return houseAds;
    }

    private static List<HouseAd> buildHouseAds(Document document) {
        List<HouseAd> hads = null;
        if (document != null && document.hasRootElement()) {
            Element rootElement = document.getRootElement();
            List<Element> children = rootElement.getChildren("ad");
            if (children != null && !children.isEmpty()) {
                hads = new ArrayList<HouseAd>();
                for (Element elm : children) {
                    String title = elm.getChildText("title");
                    String tagLine = elm.getChildText("tagLine");
                    String url = elm.getChildText("url");

                    HouseAd ad = new HouseAd();
                    ad.setTitle(title);
                    ad.setTagLine(tagLine);
                    ad.setDestinationUrl(url);

                    hads.add(ad);
                }
            }
        }
        return hads;
    }
}
