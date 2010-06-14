<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

citygrid.common.loadWidget(
<div class="ctsrch_wideContainer">
    <div class="ctsrch_sponserText">
        Ads by CityGrid
    </div>
    <div class="ctsrch_container">
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
);