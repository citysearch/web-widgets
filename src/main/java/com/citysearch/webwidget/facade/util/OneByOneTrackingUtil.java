package com.citysearch.webwidget.facade.util;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.PropertiesLoader;

public class OneByOneTrackingUtil {
	private static Properties trackingProperties;

	private static String getPropertyKey(String adunitName, String adunitSize,
			Integer pfpResultsSize, Integer pfpBackfillSize,
			Integer searchResultsSize, Integer houseAdsSize) {
		StringBuilder strBuilder = new StringBuilder();
		if (!StringUtils.isBlank(adunitName)) {
			strBuilder.append(adunitName.toUpperCase());
			strBuilder.append(".");
		}
		if (!StringUtils.isBlank(adunitSize)) {
			strBuilder.append(adunitSize);
			if (pfpResultsSize != null && pfpBackfillSize != null
					&& searchResultsSize != null && houseAdsSize != null) {
				strBuilder.append(".");
			}
		}
		if (pfpResultsSize != null) {
			strBuilder.append(String.valueOf(pfpResultsSize.intValue()));
			strBuilder.append("P-");
		}
		if (pfpBackfillSize != null) {
			strBuilder.append(String.valueOf(pfpBackfillSize.intValue()));
			strBuilder.append("B-");
		}
		if (searchResultsSize != null) {
			strBuilder.append(String.valueOf(searchResultsSize.intValue()));
			strBuilder.append("S-");
		}
		if (houseAdsSize != null) {
			strBuilder.append(String.valueOf(houseAdsSize.intValue()));
			strBuilder.append("H");
		}
		return strBuilder.toString();
	}

	private static String getTrackingUrl(String key) throws CitysearchException {
		if (trackingProperties == null) {
			trackingProperties = PropertiesLoader.getTrackingProperties();
		}
		if (trackingProperties != null && trackingProperties.containsKey(key)) {
			return trackingProperties.getProperty(key);
		}
		return null;
	}

	public static String get1x1TrackingUrl(String adunitName,
			String adunitSize, Integer pfpResultsSize, Integer pfpBackfillSize,
			Integer searchResultsSize, Integer houseAdsSize)
			throws CitysearchException {
		String key = getPropertyKey(adunitName, adunitSize, pfpResultsSize,
				pfpBackfillSize, searchResultsSize, houseAdsSize);
		return getTrackingUrl(key);
	}
}
