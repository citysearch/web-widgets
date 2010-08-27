<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Matching Offers</title>
        <link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
    </head>
    <body>


<div class="ctsrch_boxContainer ctsrch_groupon">
    <div class="ctsrch_header">
        <div class="ctsrch_headerText">Deals Nearby</div>
        <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
        <img src='<s:property value="oneByOneTrackingUrlForOriginal"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
    </div>
    <div class="ctsrch_container">
    	<s:if test='%{dealsResponse != null && dealsResponse.grouponDeal != null}'>
	        <!-- Groupon Deals -->
	        <div class="ctsrch_groupon_top">
	            <span class="ctsrch_groupon_value"><s:property value="dealsResponse.grouponDeal.price" /></span>
	            for
	            <span class="ctsrch_groupon_price"><s:property value="dealsResponse.grouponDeal.value" /></span>
	            value
	            <a class="ctsrch_groupon_viewIt" href="<s:property value="dealsResponse.grouponDeal.dealUrl" />"><img src="<s:property value="resourceRootPath"/>/static/img/groupon_viewit.png" width="72px" height="20px" /></a>
	        </div>
	        <div class="ctsrch_groupon_mid clearfix">
	            <div class="ctsrch_leftSide">
	                <div class="ctsrch_bizPhoto">
	                    <a href="<s:property value="dealsResponse.grouponDeal.dealUrl" />"><img width="74px" height="58px" src="<s:property value="dealsResponse.grouponDeal.mediumImageUrl" />" border="0"/></a>
	                </div>
	            </div>
	            <div class="ctsrch_rightSide">
	                <div class="ctsrch_groupon_deal">
	                    <a href="<s:property value="dealsResponse.grouponDeal.dealUrl" />" class="ctsrch_groupon_vendor"><s:property value="dealsResponse.grouponDeal.vendorName" /></a>
	                    : Today's Deal:
	                    <span class="ctsrch_groupon_detail">
	                        <s:property value="dealsResponse.grouponDeal.title" />
	                    </span>
	                </div>
	                <div class="ctsrch_groupon_expire">
	                    Expires <s:property value="dealsResponse.grouponDeal.enddate" />
	                </div>
	            </div>
	        </div>
        </s:if>
        <s:else>
        	<div class="ctsrch_container clearfix">
	            <div class="ctsrch_offers_details">
	                <div class="ctsrch_offers_title">
	                    <s:property value="dealsResponse.offer.offerTitle" />
	                </div>
	                <div class="ctsrch_offers_desc">
	                    <s:property value="dealsResponse.offer.offerDescription" />
	                </div>
	                <div class="ctsrch_offers_print">
	                    <a href='<s:property value="dealsResponse.offer.couponUrl" />' >
	                        Get Offer
	                    </a>
	                </div>
	            </div>
	            <div class="ctsrch_listing">
	                <div class="ctsrch_leftSide">
	                    <div class="ctsrch_bizPhoto">
	                        <a href='<s:property value="dealsResponse.offer.profileTrackingUrl" />' >
	                            <img src='<s:property value="dealsResponse.offer.imageUrl" />'/>
	                        </a>
	                    </div>
	                </div>
	                <div class="ctsrch_rightSide">
	                    <div class="ctsrch_mainLink">
	                        <a href='<s:property value="dealsResponse.offer.profileTrackingUrl" />' ><s:property value="dealsResponse.offer.listingName" /></a>
	                    </div>
	                    <div class="ctsrch_starContainer clearfix">
	                        <div class="ctsrch_stars">
	                            <s:iterator value="dealsResponse.offer.listingRating" status="stat"><span class='<s:if test="%{dealsResponse.offer.listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{dealsResponse.offer.listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
	                        </div>
	                        <div class="ctsrch_reviewFont">
	                            <s:property value="dealsResponse.offer.reviewCount" /> Reviews
	                        </div>
	                    </div>
	                    <div class="ctsrch_phone">
	                        <s:property value="dealsResponse.offer.phone" />
	                    </div>
	                    <div class="ctsrch_cityFont">
	                        <s:property value="dealsResponse.offer.location" /> <s:property value="dealsResponse.offer.zip" />
	                    </div>
	                </div>
	            </div>
        	</div>
        </s:else>
        <div class="ctsrch_groupon_btm">
            <!-- PFPx2 -->
            <s:iterator value="dealsResponse.places" status="placesStatus">
	            <div class="ctsrch_listing">
	                <div class="ctsrch_starContainer clearfix">
	                    <div class="ctsrch_stars">
	                        <s:iterator value="rating" status="stat"><span class='<s:if test="%{rating[#stat.index] == 2}">full</s:if><s:elseif test="%{rating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
	                    </div>
	                    <s:if test="%{reviewCount > 0}">
                            <div class="ctsrch_reviewFont">
                                <s:property value="reviewCount" /> Reviews
                            </div>
                        </s:if>
	                </div>
	                <div class="ctsrch_mainLink">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="adDisplayTrackingURL" />' ><s:property value="name" /></a>
                        </s:else>
                    </div>
	                <s:if test="%{isValidLocation == true}">
                        <div class="ctsrch_cityFont">
                            <s:property value="location" />
                        </div>
                    </s:if>
	            </div>
            </s:iterator>
            <s:iterator value="dealsResponse.backfill" status="placesStatus">
	            <div class="ctsrch_listing">
	                <div class="ctsrch_mainLink" >
                        <a href='<s:property value="adDisplayTrackingURL" />'><s:property value="category" /></a>
                    </div>
                    <div class="ctsrch_tagLine" >
                        <s:property value="description" />
                    </div>
                    <div class="ctsrch_displayUrl" >
                        <a href='<s:property value="adDisplayTrackingURL" />'><s:property value="adDisplayURL" /></a>
                    </div>
                    <s:if test='%{offers != null && !"".equals(offers)}'>
	                     <div class="ctsrch_offersFont" >
	                         <s:property value="offers" />
	                     </div>
                    </s:if>
	            </div>
            </s:iterator>
            <s:iterator value="dealsResponse.searchResults" status="placesStatus">
	            <div class="ctsrch_listing">
	                <div class="ctsrch_starContainer clearfix">
	                    <div class="ctsrch_stars">
	                        <s:iterator value="rating" status="stat"><span class='<s:if test="%{rating[#stat.index] == 2}">full</s:if><s:elseif test="%{rating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
	                    </div>
	                    <s:if test="%{reviewCount > 0}">
                            <div class="ctsrch_reviewFont">
                                <s:property value="reviewCount" /> Reviews
                            </div>
                        </s:if>
	                </div>
	                <div class="ctsrch_mainLink">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="adDisplayTrackingURL" />' ><s:property value="name" /></a>
                        </s:else>
                    </div>
	                <s:if test="%{isValidLocation == true}">
                        <div class="ctsrch_cityFont">
                            <s:property value="location" />
                        </div>
                    </s:if>
	            </div>
            </s:iterator>
            <s:iterator value="dealsResponse.houseAds" status="hadStatus">
	            <div class="ctsrch_listing">
	                <div class="ctsrch_mainLink">
                        <a href='<s:property value="destinationUrl" />'><s:property value="title" /></a>
                    </div>
                    <div class="ctsrch_tagLine">
                        <s:property value="tagLine" />
                    </div>
                    <div class="ctsrch_displayUrl" >
                        <a href='<s:property value="destinationUrl" />'><s:property value="displayUrl" /></a>
                    </div>
	            </div>
            </s:iterator>
        </div>
    </div>
</div>

</body>
</html>