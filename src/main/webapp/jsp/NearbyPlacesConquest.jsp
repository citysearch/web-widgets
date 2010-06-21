<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div class="ctsrch_wideContainer">
    <div class="ctsrch_sponserText">
        Ads by CityGrid
    </div>
    <div class="ctsrch_container">
        <s:iterator value="nearbyPlaces" status="placesStatus">
            <div class="ctsrch_listing">
                <div class="ctsrch_leftSide">
                    <div class="ctsrch_bigStar">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a href='<s:property value="callBackFunction" />'><img src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="listingUrl" />' ><img src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:else>
                    </div>
                    <div class="ctsrch_milesFont ctsrch_reviewFont">
                        <s:property value="distance" /> mi away
                    </div>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="listingUrl" />' ><s:property value="name" /></a>
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
            </div>
        </s:iterator>
        <s:iterator value="backfill" status="placesStatus">
            <div class="ctsrch_listing">
                <div class="ctsrch_leftSide">
                    <a target="_blank" href='<s:property value="adDestinationUrl" />'><img src='<s:property value="adImageURL"/>' border="0"/></a>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink_bf" >
                        <a target="_blank" href='<s:property value="adDestinationUrl" />'><s:property value="category" /></a>
                    </div>
                    <div class="ctsrch_descFont" >
                        <s:property value="description" />
                    </div>
                    <div class="ctsrch_subLink_bf" >
                        <a target="_blank" href='http://<s:property value="adDisplayURL" />'><s:property value="adDisplayURL" /></a>
                    </div>
                    <s:if test='%{offers != null && !"".equals(offers)}'>
                     <div class="ctsrch_offersFont" >
                         <s:property value="offers" />
                     </div>
                    </s:if>
                </div>
            </div>
        </s:iterator>
        <s:iterator value="houseAds" status="hadStatus">
            <div class="ctsrch_listing">
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
            </div>
        </s:iterator>
    </div>
</div>