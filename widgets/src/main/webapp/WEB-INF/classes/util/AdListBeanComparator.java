package util;

import java.util.*;
import util.AdListBean;

public class AdListBeanComparator implements Comparator<AdListBean>{
	
	public AdListBeanComparator(){
	}

	public int compare(AdListBean beanOne,AdListBean beanTwo){
		double beanOneDistance;
		double beanTwoDistance;
		double beanOneRating;
		double beanTwoRating;
		int beanOneReviewCount;
		int beanTwoReviewCount;
		int result;

		/*beanOneDistance = Double.parseDouble(beanOne.getDistance());
		beanTwoDistance = Double.parseDouble(beanTwo.getDistance());
		result = Double.compare(beanOneDistance, beanTwoDistance);
		if(result == 0){*/
			beanOneRating = beanOne.getRatings();
			beanTwoRating = beanTwo.getRatings();
			result = Double.compare(beanTwoRating, beanOneRating);
			if(result == 0){
				beanOneReviewCount = beanOne.getReviewCount();
				beanTwoReviewCount = beanTwo.getReviewCount();
				result = compare(beanTwoReviewCount,beanOneReviewCount);
			}
		//}
		return result;
	}

	private int compare(int one, int two){
		int result;
		if(one == two){
			result = 0;
		} else if (one > two){
			result = 1;
		} else {
			result = -1;
		}
		return result;
	}

}