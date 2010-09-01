<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<s:if test='%{dealsResponse != null && dealsResponse.grouponDeal != null}'>
    <!-- Groupon Deals -->
    <div class='ctsrch_boxContainer ctsrch_groupon'>
        <div class="ctsrch_header">
            <div class="ctsrch_headerText">Deals Nearby</div>
            <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
            <img src='<s:property value="oneByOneTrackingUrlForOriginal"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
        </div>
        <div class="ctsrch_container">
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
                                <s:property value="name" />
                            </s:if>
                            <s:else>
                                <s:property value="name" />
                            </s:else>
                        </div>
                        <s:if test="%{isValidLocation == true}">
                            <div class="ctsrch_cityFont">
                                <s:property value="location" />
                            </div>
                        </s:if>
                    </div>
                </s:iterator>
                <!-- Backfill -->
                <s:iterator value="dealsResponse.backfill" status="placesStatus">
                    <div class="ctsrch_listing">
                        <div class="ctsrch_mainLink" >
                            <s:property value="category" />
                        </div>
                        <div class="ctsrch_tagLine" >
                            <s:property value="description" />
                        </div>
                        <div class="ctsrch_displayUrl" >
                            <s:property value="adDisplayURL" />
                        </div>
                        <s:if test='%{offers != null && !"".equals(offers)}'>
                             <div class="ctsrch_offersFont" >
                                 <s:property value="offers" />
                             </div>
                        </s:if>
                    </div>
                </s:iterator>
                <!-- Search -->
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
                                <s:property value="name" />
                            </s:if>
                            <s:else>
                                <s:property value="name" />
                            </s:else>
                        </div>
                        <s:if test="%{isValidLocation == true}">
                            <div class="ctsrch_cityFont">
                                <s:property value="location" />
                            </div>
                        </s:if>
                    </div>
                </s:iterator>
                <!-- House Ads -->
                <s:iterator value="dealsResponse.houseAds" status="hadStatus">
                    <div class="ctsrch_listing">
                        <div class="ctsrch_mainLink">
                            <s:property value="title" />
                        </div>
                        <div class="ctsrch_tagLine">
                            <s:property value="tagLine" />
                        </div>
                        <div class="ctsrch_displayUrl" >
                            <s:property value="displayUrl" />
                        </div>
                    </div>
                </s:iterator>
            </div>
        </div>
    </div>
</s:if>
<s:else>
    <!-- CS Offer -->
    <div class='ctsrch_boxContainer ctsrch_offers_box'>
        <div class="ctsrch_header">
            <div class="ctsrch_headerText">Deals Nearby</div>
            <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
            <img src='<s:property value="oneByOneTrackingUrlForOriginal"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
        </div>
        <div class="ctsrch_container">
            <div class="ctsrch_listing">
                <div class="ctsrch_mainLink">
                    <a href='<s:property value="dealsResponse.citySearchOffer.profileTrackingUrl" />' ><s:property value="dealsResponse.citySearchOffer.listingName" /></a>
                </div>
                <div class="ctsrch_leftSide">
                    <div class="ctsrch_bizPhoto">
                        <a href='<s:property value="dealsResponse.citySearchOffer.profileTrackingUrl" />' >
                            <img src='<s:property value="dealsResponse.citySearchOffer.imageUrl" />'/>
                        </a>
                    </div>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_starContainer clearfix">
                        <div class="ctsrch_stars">
                            <s:iterator value="dealsResponse.citySearchOffer.listingRating" status="stat"><span class='<s:if test="%{dealsResponse.citySearchOffer.listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{dealsResponse.citySearchOffer.listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        </div>
                        <div class="ctsrch_reviewFont">
                            <s:property value="dealsResponse.citySearchOffer.reviewCount" /> Reviews
                        </div>
                    </div>
                    <div class="ctsrch_phone">
                        <s:property value="dealsResponse.citySearchOffer.phone" />
                    </div>
                    <div class="ctsrch_cityFont">
                        <s:property value="dealsResponse.citySearchOffer.street" />
                    </div>
                    <div class="ctsrch_cityFont">
                        <s:property value="dealsResponse.citySearchOffer.location" /> <s:property value="dealsResponse.citySearchOffer.zip" />
                    </div>
                </div>
            </div>
            <div class="ctsrch_offers_details">
                <div class="ctsrch_offers_title">
                    <s:property value="dealsResponse.citySearchOffer.offerDescription" />
                </div>
            </div>
            <div class="ctsrch_offers_print">
                <a href='<s:property value="dealsResponse.citySearchOffer.couponUrl" />' >
                    Get Offer
                </a>
            </div>
            <div class="ctsrch_footer clearfix">
                <div class="ctsrch_sponserText">Ads by CityGrid</div>

            </div>
        </div>
    </div>
</s:else>