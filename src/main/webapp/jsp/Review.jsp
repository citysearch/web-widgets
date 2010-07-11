<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{review != null}">
    <div id="cs_mainContainer" class="review_cs_mainContainer">
        <div class="review_cs_header">
            Recent Reviews Near You
        </div>
        <div class="review_cs_content">
            <div class="review_cs_img">
                <img src='<s:property value="review.imageUrl"/>' />
            </div>
            <div class="review_cs_ratings">
                <div class="ctsrch_stars">
                    <s:iterator value="review.rating" status="stat"><span class='<s:if test="%{review.rating[#stat.index] == 2}">full</s:if><s:elseif test="%{review.rating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                </div>
                <div class="review_cs_title">
                    <a href='<s:property value="review.profileTrackingUrl"/>' ><s:property value="review.shortBusinessName"/></a>
                </div>
                <div class="review_cs_address">
                    <s:property value="review.address.street"/><br />
                    <s:property value="review.address.state"/>&nbsp;<s:property value="review.address.postalCode"/>
                </div>
            </div>
            <div class="review_cs_time"><s:property value="review.reviewAuthor"/> on <s:property value="review.reviewDate" /> said:</div>
            <div class="review_cs_reviews" style="float:left;">
                <div class="review_cs_longTitle">
                    <a href='<s:property value="review.reviewTrackingUrl"/>' >
                        <s:property value="review.shortTitle"/>
                    </a>
                </div>
                <div class="review_cs_description">
                    <s:property value="review.shortReviewText"/>
                    <s:if test="%{review.shortReviewText != review.reviewText}">
                        <a href="javascript:citygrid.review.showDetail()">read more</a>
                    </s:if>
                </div>
                <div class="review_cs_description">
                    <div class="spacer"></div>
                    <strong>Pros:</strong>&nbsp;<s:property value="review.shortPros"/><br/>
                    <strong>Cons:</strong>&nbsp;<s:property value="review.shortCons"/>
                </div>
                <div class="review_cs_share">
                    <a href='<s:property value="review.sendToFriendTrackingUrl"/>' >Share this review</a>
                </div>
            </div>
        </div>
    </div>
    <div id="cs_mainContainer_detail" class="review_cs_mainContainer_detail" style="display: none;">
        <div class="review_cs_header_detail">Review Detail</div>
        <div class="review_cs_content_detail">
            <div class="review_cs_ratings_detail">
                <div class="review_cs_close_detail">
                    <a href="javascript:citygrid.review.hideDetail()">Close Window</a>
                </div>
                <div class="ctsrch_stars">
                    <s:iterator value="review.rating" status="stat"><span class='<s:if test="%{review.rating[#stat.index] == 2}">full</s:if><s:elseif test="%{review.rating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                </div>
            </div>
            <div class="review_cs_title_detail">
                <a href='<s:property value="review.profileTrackingUrl"/>' ><s:property value="review.shortBusinessName"/></a>
            </div>
            <div class="review_cs_address_detail">
                <s:property value="review.address.street"/><br />
                <s:property value="review.address.state"/>&nbsp;<s:property value="review.address.postalCode"/>
            </div>
            <div class="review_cs_time_detail"><s:property value="review.reviewAuthor"/> on <s:property value="review.reviewDate" /> said:</div>
            <div class="review_cs_reviews_detail">
                <div class="review_cs_longTitle_detail">
                    <a href='<s:property value="review.reviewTrackingUrl"/>' >
                        <s:property value="review.shortTitle"/>
                    </a>
                </div>
                <div class="review_cs_description_detail"><s:property value="review.reviewText"/></div>
                <div class="review_cs_share_detail">
                    <a href='<s:property value="review.sendToFriendTrackingUrl"/>' >Share this review</a>
                </div>
            </div>
        </div>
    </div>
</s:if>
<s:else>
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
                    <div class="ctsrch_subLink_bf" >
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