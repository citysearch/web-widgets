<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{offer != null}">
    <!-- Offers -->
    <div class="ctsrch_wideContainer ctsrch_offers">
        <div class="ctsrch_container clearfix">
            <div class="ctsrch_offers_details">
                <div class="ctsrch_offers_title">
                    <s:property value="offer.offerTitle" />
                </div>
                <div class="ctsrch_offers_desc">
                    <s:property value="offer.offerDescription" />
                </div>
                <div class="ctsrch_offers_print">
                    <a href='<s:property value="offer.couponUrl" />' >
                        Get Offer
                    </a>
                </div>
            </div>
            <div class="ctsrch_listing">
                <div class="ctsrch_leftSide">
                    <div class="ctsrch_bizPhoto">
                        <a href='<s:property value="offer.profileTrackingUrl" />' >
                            <img src='<s:property value="offer.imageUrl" />'/>
                        </a>
                    </div>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <a href='<s:property value="offer.profileTrackingUrl" />' ><s:property value="offer.listingName" /></a>
                    </div>
                    <div class="ctsrch_starContainer clearfix">
                        <div class="ctsrch_stars">
                            <s:iterator value="offer.listingRating" status="stat"><span class='<s:if test="%{offer.listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{offer.listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        </div>
                        <div class="ctsrch_reviewFont">
                            <s:property value="offer.reviewCount" /> Reviews
                        </div>
                    </div>
                    <div class="ctsrch_phone">
                        <s:property value="offer.phone" />
                    </div>
                    <div class="ctsrch_cityFont">
                        <s:property value="offer.location" /> <s:property value="offer.zip" />
                    </div>
                </div>
            </div>
        </div>
        <div class="ctsrch_header clearfix">
            <div class="ctsrch_sponserText">Ads by CityGrid</div>
            <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
        </div>
    </div>
</s:if>
<s:elseif test="%{!houseAds.isEmpty()}">
    <!-- House Ads -->
    <div class="ctsrch_wideContainer">
        <div class="ctsrch_header clearfix">
            <div class="ctsrch_sponserText">Ads by CityGrid</div>
            <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
            <s:if test="%{offer != null}">
                <div class="ctsrch_headerText">Special Offers Nearby</div>
            </s:if>
        </div>
        <div class="ctsrch_container clearfix">
            <s:iterator value="houseAds" status="hadStatus">
                <div class="ctsrch_listing">
                    <div class="ctsrch_leftSide"></div>
                    <div class="ctsrch_rightSide">
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
                </div>
            </s:iterator>
        </div>
    </div>
</s:elseif>