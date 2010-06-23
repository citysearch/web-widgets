<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div class="ctsrch_boxContainer">
    <div class="ctsrch_header" style="">
        <div class="ctsrch_headerText">More to Try</div>
    </div>
    <div class="ctsrch_container">
        <s:iterator value="nearbyPlaces" status="placesStatus">
            <div class="ctsrch_leftSide">
                <div class="ctsrch_bigStar">
                    <s:if test="%{isValidCallbackFunction == true}">
                        <a href='<s:property value="callBackFunction" />'><img src='<s:property value="adImageURL" />' border="0"/></a>
                    </s:if>
                    <s:else>
                        <a href='<s:property value="adDisplayTrackingURL" />' ><img src='<s:property value="adImageURL" />' border="0"/></a>
                    </s:else>
                </div>
                <div class="ctsrch_milesFont ctsrch_reviewFont">
                    <s:property value="distance" /> mi away
                </div>
            </div>
            <div class="ctsrch_rightSide">
                <div class="ctsrch_mainLink" >
                    <s:if test="%{isValidCallbackFunction == true}">
                        <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                    </s:if>
                    <s:else>
                        <a href='<s:property value="adDisplayTrackingURL" />' ><s:property value="name" /></a>
                    </s:else>
                </div>
                <s:if test="%{ratings > 2.5}">
                    <div class="ctsrch_starContainer">
                        <div class="ctsrch_stars">
                            <s:iterator value="rating" status="stat"><span class='<s:if test="%{rating[#stat.index] == 2}">full</s:if><s:elseif test="%{rating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        </div>
                        <s:if test="%{reviewCount > 0}">
                            <div class="ctsrch_reviewFont">
                                <s:property value="reviewCount" /> Reviews
                            </div>
                        </s:if>
                    </div>
                </s:if>
                <s:if test="%{isValidLocation == true}">
                    <div class="ctsrch_cityFont">
                        <s:property value="location" />
                    </div>
                </s:if>
                <div class="ctsrch_subcategoryFont">
                    <s:property value="category" />
                </div>
            </div>
            <s:if test="%{profile != null && profile.review != null && nearbyPlaces.size() <= 2}">
            	<s:if test="%{nearbyPlaces.size() == 1}">
            		<div class="review_cs_reviews">
	                    <div class="review_cs_longTitle">
	                        <a href='<s:property value="profile.review.reviewTrackingUrl"/>' target="_blank">
	                            <s:property value="profile.review.shortTitle"/>
	                        </a>
	                    </div>
	                    <div class="review_cs_description"><s:property value="profile.review.shortReviewText"/></div>
	                    <div class="review_cs_description">
	                        <div class="spacer"></div>
	                        <strong>Pros:</strong>&nbsp;<s:property value="profile.review.shortPros"/>
	                        <div class="seperator"></div>
	                        <strong>Cons:</strong>&nbsp;<s:property value="profile.review.shortCons"/>
	                    </div>
	                </div>
	                <s:if test='%{profile.sendToFriendTrackingUrl != null && !"".equals(profile.sendToFriendTrackingUrl)}'>
		                <div id="review_cs_share">
		                    <a href='<s:property value="profile.sendToFriendTrackingUrl"/>' target="_blank">Share this review</a>
		                </div>
	                </s:if>
            	</s:if>
            	<s:elseif test="%{nearbyPlaces.size() == 2}">
            		<div class="review_cs_reviews_thin">
	                    <div class="review_cs_longTitle">
	                        <a href='<s:property value="profile.review.reviewTrackingUrl"/>' target="_blank">
	                            <s:property value="profile.review.shortTitle"/>
	                        </a>
	                    </div>
                    	<div class="review_cs_description">
	                        <s:property value="profile.review.smallReviewText"/>
	                    </div>
	                </div>
            	</s:elseif>
            </s:if>
            <s:if test="%{#placesStatus.index < (nearbyPlaces.size()-1)}">
                <div class="ctsrch_lineMargin"> </div>
            </s:if>
        </s:iterator>
        <s:if test="%{!backfill.isEmpty() && !nearbyPlaces.isEmpty()}">
              <div class="ctsrch_lineMargin"></div>
         </s:if>
        <s:iterator value="backfill" status="placesStatus">
            <div class="ctsrch_leftSide">
                <a target="_blank" href='<s:property value="adDisplayTrackingURL" />'><img src='<s:property value="adImageURL"/>' border="0"/></a>
            </div>
            <div class="ctsrch_rightSide">
                <div class="ctsrch_mainLink_bf" >
                    <a target="_blank" href='<s:property value="adDisplayTrackingURL" />'><s:property value="category" /></a>
                </div>
                <div class="ctsrch_descFont" >
                    <s:property value="description" />
                </div>
                <div class="ctsrch_subLink_bf" >
                    <a target="_blank" href='http://<s:property value="adDisplayTrackingURL" />'><s:property value="adDisplayURL" /></a>
                </div>
                <s:if test='%{offers != null && !"".equals(offers)}'>
                 <div class="ctsrch_offersFont" >
                     <s:property value="offers" />
                 </div>
                </s:if>
            </div>
            <s:if test="%{#placesStatus.index < (backfill.size() - 1)}">
                  <div class="ctsrch_lineMargin"> </div>
              </s:if>
        </s:iterator>
        <s:if test="%{(!backfill.isEmpty() || !nearbyPlaces.isEmpty()) && !houseAds.isEmpty()}">
              <div class="ctsrch_lineMargin"></div>
         </s:if>
        <s:iterator value="houseAds" status="hadStatus">
            <div class="ctsrch_leftSide">
                <!--
                <a target="_blank" href='<s:property value="destinationUrl" />'><img src='<s:property value="imageURL"/>' border="0"/></a>
                -->
            </div>
            <div class="ctsrch_rightSide">
                <div class="ctsrch_mainLink">
                    <a target="_blank" href='<s:property value="destinationUrl" />'><s:property value="title" /></a>
                </div>
                <div class="ctsrch_descFont">
                    <s:property value="tagLine" />
                </div>
                <div class="ctsrch_subLink_bf" >
                    <a target="_blank" href='<s:property value="destinationUrl" />'>www.citysearch.com</a>
                </div>
            </div>
            <s:if test="%{#hadStatus.index < (houseAds.size() - 1)}">
                  <div class="ctsrch_lineMargin"> </div>
              </s:if>
        </s:iterator>
    </div>
</div>