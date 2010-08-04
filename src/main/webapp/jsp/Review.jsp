<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{review != null}">
    <!-- Main Page -->
    <div class="ctsrch_tallContainer">
        <div class="ctsrch_header">
            <div class="ctsrch_headerText">
                Recent Reviews Near You
            </div>
        </div>
        <div class="ctsrch_container">
            <div class="ctsrch_listing clearfix">
                <div class="ctsrch_leftSide">
                    <div class="ctsrch_bizPhoto">
                        <img src='<s:property value="review.imageUrl"/>' />
                    </div>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <a href='<s:property value="review.profileTrackingUrl"/>' ><s:property value="review.shortBusinessName"/></a>
                    </div>
                    <div class="ctsrch_starContainer clearfix">
                        <div class="ctsrch_stars">
                            <s:iterator value="review.rating" status="stat"><span class='<s:if test="%{review.rating[#stat.index] == 2}">full</s:if><s:elseif test="%{review.rating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        </div>
                    </div>
                    <div class="ctsrch_cityFont">
                        <s:property value="review.address.street"/>,<br/>
                        <s:property value="review.address.city"/>, <s:property value="review.address.state"/>&nbsp;<s:property value="review.address.postalCode"/>
                    </div>
                </div>
            </div>

            <div class="ctsrch_review_full">
               <div class="ctsrch_review_time">
                   <s:property value="review.reviewAuthor"/> on <s:property value="review.reviewDate" /> said:
               </div>
                <div class="ctsrch_review_longTitle">
                    <a href='<s:property value="review.reviewTrackingUrl"/>' >
                        <s:property value="review.shortTitle"/>
                    </a>
                </div>
                <div class="ctsrch_review_description">
                    <s:property value="review.shortReviewText"/>
                    <s:if test="%{review.shortReviewText != review.reviewText}">
                        <span class="ctsrch_review_readmore">
                           <a  href="<s:property value="review.reviewTrackingUrl"/>">read more</a>
                        </span>
                    </s:if>
                </div>
                <s:if test="%{review.shortPros != ''}">
                    <div class="ctsrch_review_proscons">
                        <span>Pros:</span><s:property value="review.shortPros"/>
                    </div>
                </s:if>
                <s:if test="%{review.shortCons != ''}">
                    <div class="ctsrch_review_proscons">
                        <span>Cons:</span><s:property value="review.shortCons"/>
                    </div>
                </s:if>
            </div>
            <div class="ctsrch_review_share">
                <a href='<s:property value="review.sendToFriendTrackingUrl"/>' >Share this review</a>
            </div>
        </div>
    </div>
</s:if>
<s:else>
    <!-- House Ads -->
    <div class="ctsrch_boxContainer">
        <div class="ctsrch_header" style="">
            <div class="ctsrch_headerText">More to Try</div>
        </div>
        <div class="ctsrch_container">
            <s:iterator value="houseAds" status="hadStatus">
                <div class="ctsrch_leftSide"></div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <a href='<s:property value="destinationUrl" />'><s:property value="title" /></a>
                    </div>
                    <div class="review_ctsrch_descFont">
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
    </div>
</s:else>