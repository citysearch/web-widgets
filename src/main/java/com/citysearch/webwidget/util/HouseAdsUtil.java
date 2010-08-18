package com.citysearch.webwidget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.citysearch.webwidget.bean.HouseAd;
import com.citysearch.webwidget.exception.CitysearchException;

public class HouseAdsUtil {
	public static List<HouseAd> getHouseAds(String path, String dartTrackUrl)
			throws CitysearchException {

		List<HouseAd> houseAds = null;
		InputStream inputStream = null;

		inputStream = HouseAdsUtil.class.getClassLoader()
				.getResourceAsStream("/HouseAdsConfig.xml");
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
				throw new CitysearchException("HouseAdsHelper", "getHouseAds",
						ioe);
			}
		}
		houseAds = buildHouseAds(document, path, dartTrackUrl);

		return houseAds;
	}

	private static List<HouseAd> buildHouseAds(Document document, String path,
			String dartTrackUrl) {
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
					String imageURL = elm.getChildText("imageURL");
					if (StringUtils.isNotBlank(imageURL)) {
						if (!imageURL.startsWith("http")) {
							StringBuilder strb = new StringBuilder(path);
							strb.append(imageURL);
							imageURL = strb.toString();
						}
					}

					HouseAd ad = new HouseAd();
					ad.setTitle(title);
					ad.setTagLine(tagLine);
					if (dartTrackUrl != null) {
						StringBuilder destUrl = new StringBuilder(dartTrackUrl);
						destUrl.append(url);
						ad.setDestinationUrl(destUrl.toString());
						ad.setDisplayUrl(url);
					} else {
						ad.setDestinationUrl(url);
						ad.setDisplayUrl(url);
					}

					ad.setImageURL(imageURL);

					hads.add(ad);
				}
			}
		}
		return hads;
	}
}
