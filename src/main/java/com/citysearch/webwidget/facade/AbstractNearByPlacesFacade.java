package com.citysearch.webwidget.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.citysearch.webwidget.bean.NearbyPlace;
import com.citysearch.webwidget.exception.CitysearchException;
import com.citysearch.webwidget.util.HelperUtil;

public abstract class AbstractNearByPlacesFacade {
	protected String contextPath;

	protected AbstractNearByPlacesFacade(String contextPath) {
		this.contextPath = contextPath;
	}

	protected List<NearbyPlace> addDefaultImages(List<NearbyPlace> nearByPlaces)
			throws CitysearchException {
		if (nearByPlaces != null && !nearByPlaces.isEmpty()) {
			List<String> imageList = HelperUtil.getImages(contextPath);
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

}
