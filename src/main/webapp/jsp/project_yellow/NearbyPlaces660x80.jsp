<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<s:if test="nearbyPlaces.size > 0 || backfill.size > 0 || searchResults.size > 0 || houseAds.size > 0">
    <div class="ctsrch_wideContainer ctsrch_yelp">
        <div class="ctsrch_header clearfix">
            <div class="ctsrch_sponserText">Ads by CityGrid</div>
            <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
        </div>
        <div class="ctsrch_container clearfix">
            <!-- PFP -->
            <s:iterator value="nearbyPlaces" status="placesStatus">
                <div class="ctsrch_listing">
                    <div class="ctsrch_mainLink">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="adDisplayTrackingURL" />' ><s:property value="name" /></a>
                        </s:else>
                    </div>
                    <div class="ctsrch_tagLine">
                        <s:property value="category" />
                    </div>
                    <div class="ctsrch_displayUrl" >
                        <a href='<s:property value="adDisplayTrackingURL" />'><s:property value="adDisplayURL" /></a>
                    </div>
                </div>
            </s:iterator>

            <!-- PFP Backfill -->
            <s:iterator value="backfill" status="placesStatus">
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

            <!-- Search -->
            <s:iterator value="searchResults" status="placesStatus">
                <div class="ctsrch_listing">
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
                    <div class="ctsrch_tagLine">
                        <s:property value="category" />
                    </div>
                </div>
            </s:iterator>

            <!-- House Ads -->
            <s:iterator value="houseAds" status="hadStatus">
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
</s:if>
<s:else>
    <img src='<s:property value="oneByOneTrackingUrl"/>' style="float: left;" width="1px" height="1px" border="0" alt="" />
</s:else>