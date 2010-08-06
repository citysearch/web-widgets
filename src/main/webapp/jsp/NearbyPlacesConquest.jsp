<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div class="ctsrch_wideContainer">
    <div class="ctsrch_header clearfix">
        <div class="ctsrch_sponserText">Ads by CityGrid</div>
        <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
        <img src='<s:property value="oneByOneTrackingUrlForOriginal"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
    </div>
    <div class="ctsrch_container clearfix">
        <s:iterator value="nearbyPlaces" status="placesStatus">
            <div class="ctsrch_listing">
                <div class="ctsrch_leftSide">
                    <div class="ctsrch_bizPhoto">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a href='<s:property value="callBackFunction" />'><img width="47px" height="47px" src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="adDisplayTrackingURL" />' ><img width="47px" height="47px" src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:else>
                    </div>
                    <s:if test="%{distance != -1}">
                        <div class="ctsrch_milesFont">
                            <s:property value="distance" /> mi away
                        </div>
                    </s:if>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="adDisplayTrackingURL" />' ><s:property value="name" /></a>
                        </s:else>
                    </div>
                    <s:if test="%{ratings > 2.5}">
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
                    <div class="ctsrch_bizPhoto">
                        <a href='<s:property value="adDisplayTrackingURL" />'><img width="47px" height="47px" src='<s:property value="adImageURL"/>' border="0"/></a>
                    </div>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink" >
                        <a href='<s:property value="adDisplayTrackingURL" />'><s:property value="category" /></a>
                    </div>
                    <div class="ctsrch_descFont_bf" >
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
            </div>
        </s:iterator>
        <s:iterator value="searchResults" status="placesStatus">
            <div class="ctsrch_listing">
                <div class="ctsrch_leftSide">
                    <div class="ctsrch_bizPhoto">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a href='<s:property value="callBackFunction" />'><img width="47px" height="47px" src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="adDisplayTrackingURL" />' ><img width="47px" height="47px" src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:else>
                    </div>
                    <div class="ctsrch_milesFont">
                        <s:property value="distance" /> mi away
                    </div>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="adDisplayTrackingURL" />' ><s:property value="name" /></a>
                        </s:else>
                    </div>
                    <s:if test="%{ratings > 2.5}">
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
        <s:iterator value="houseAds" status="hadStatus">
            <div class="ctsrch_listing">
                <div class="ctsrch_leftSide">
                    <!--
                    <a href='<s:property value="destinationUrl" />'><img src='<s:property value="imageURL"/>' border="0"/></a>
                    -->
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <a href='<s:property value="destinationUrl" />'><s:property value="title" /></a>
                    </div>
                    <div class="ctsrch_descFont">
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