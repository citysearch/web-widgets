<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Matching Offers</title>
        <link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
    </head>
    <body>
        <div class="ctsrch_boxContainer">
            <div class="ctsrch_container">
                <s:iterator value="offers" status="stat">
                    <div class="cs_offer">
                        <div class="cs_offer_text">
                            <s:property value="offerTitle" />
                        </div>
                        <div class="cs_offer_getoffer">
                            <a target="_blank" href='<s:property value="couponUrl" />' >Get offer</a>
                        </div>
                    </div>
                    <div class="ctsrch_leftSide">
                          <div class="ctsrch_bizPhoto">
                              <a href='<s:property value="profileTrackingUrl" />' >
                                <img width="47px" height="47px" src='<s:property value="imageUrl" />'/>
                            </a>
                          </div>
                      </div>
                      <div class="ctsrch_rightSide">
                          <div class="cs_offer_listing">
                            <a href='<s:property value="profileTrackingUrl" />' ><s:property value="listingName" /></a>
                        </div>
                             <div class="ctsrch_starContainer clearfix">
                            <div class="ctsrch_stars">
                                   <s:iterator value="listingRating" status="stat"><span class='<s:if test="%{listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                               </div>
                            <div class="ctsrch_reviewFont">
                                <s:property value="reviewCount" /> &nbsp; Reviews
                            </div>
                        </div>
                          <div class="ctsrch_cityFont">
                             <s:property value="location" />
                         </div>
                      </div>
                    <div class="cs_offer_msg">
                           <s:property value="offerDescription" />
                       </div>
                      <s:if test="%{profile != null && profile.review != null}">
                          <div class="cs_offer_reviews">
                            <div class="cs_offer_review_title">
                                <a href='<s:property value="profile.review.reviewTrackingUrl"/>'>
                                    <s:property value="profile.review.shortTitle"/>
                                </a>
                            </div>
                            <div class="cs_offer_review_desc"><s:property value="profile.review.shortReviewText"/></div>
                            <s:if test='%{profile.sendToFriendTrackingUrl != null && !"".equals(profile.sendToFriendTrackingUrl)}'>
                                <div class="cs_offer_review_share">
                                    <a href='<s:property value="profile.sendToFriendTrackingUrl"/>' >Share this review</a>
                                </div>
                            </s:if>
                        </div>
                    </s:if>
                      <s:if test="%{#stat.index < (offers.size()-1)}">
                         <div class="ctsrch_lineMargin"></div>
                    </s:if>
                 </s:iterator>
                 <s:iterator value="houseAds" status="hadStatus">
                    <div class="ctsrch_leftSide" style="height: 67px;"></div>
                    <div class="ctsrch_rightSide" style="height: 67px;">
                        <div class="ctsrch_mainLink">
                            <a href='<s:property value="destinationUrl" />'><s:property value="title" /></a>
                        </div>
                        <div class="ctsrch_descFont">
                            <s:property value="tagLine" />
                        </div>
                        <div class="ctsrch_displayUrl" >
                            <a href='<s:property value="destinationUrl" />'>www.citysearch.com</a>
                        </div>
                    </div>
                    <s:if test="%{#hadStatus.index < (houseAds.size() - 1)}">
                          <div class="ctsrch_lineMargin"> </div>
                      </s:if>
                </s:iterator>
            </div>
            <div class="ctsrch_logoFont_right">Ads by Citysearch</div>
        </div>
    </body>
</html>