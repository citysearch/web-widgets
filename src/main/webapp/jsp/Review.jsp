<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Recent Reviews Near You</title>
	<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/Review.css'  rel="stylesheet"/>
	<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
	<script language="JavaScript" type="text/javascript">
		function showDetail()
		{
			document.getElementById('cs_mainContainer').style.display='none';
			document.getElementById('cs_mainContainer_detail').style.display='block';
		}
 
		function hideDetail()
		{
			document.getElementById('cs_mainContainer').style.display='block';
			document.getElementById('cs_mainContainer_detail').style.display='none';
		}
	</script>
</head>
<body>
	<s:if test="%{review != null}">
		<div id="cs_mainContainer">
			<div id="cs_header">
			    Recent Reviews Near You
			</div>
			<div id="cs_content">
			    <div id="cs_img">
			    	<img src='<s:property value="review.imageUrl"/>' />
			    </div>
			    <div id="cs_ratings">
			        <div id="cs_stars">
			        	<s:iterator value="review.rating" status="stat"><span class='<s:if test="%{review.rating[#stat.index] == 2}">full</s:if><s:elseif test="%{review.rating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
			        </div>
			        <div id="cs_title">
			        	<a href='<s:property value="review.profileTrackingUrl"/>' ><s:property value="review.shortBusinessName"/></a>
			        </div>
			        <div id="cs_address">
			        	<s:property value="review.address.street"/><br />
			        	<s:property value="review.address.state"/>&nbsp;<s:property value="review.address.postalCode"/>
			        </div>
			    </div>
			    <div id="cs_time"><s:property value="review.reviewAuthor"/> on <s:property value="review.reviewDate" /> said:</div>
			    <div id="cs_reviews">
			        <div id="cs_longTitle">
			        	<a href='<s:property value="review.reviewTrackingUrl"/>' >
			        		<s:property value="review.shortTitle"/>
			        	</a>
			        </div>
			        <div id="cs_description">
			        	<s:property value="review.shortReviewText"/>
			        	<s:if test="%{review.shortReviewText != review.reviewText}">
			        		<a href="javascript:showDetail()">more</a>
			        	</s:if>
			        </div>
			        <div id="cs_description">
			        	<div class="spacer"></div>
		        		<strong>Pros:</strong>&nbsp;<s:property value="review.shortPros"/>
		        		<div class="seperator"></div>
		            	<strong>Cons:</strong>&nbsp;<s:property value="review.shortCons"/> 
			        </div>
			    </div>
			    <div id="cs_share">
			    	<a href='<s:property value="review.sendToFriendTrackingUrl"/>' >Share this review</a>
			    </div>
			</div>
		</div>
		<div id="cs_mainContainer_detail" style="display: none;">
			<div id="cs_header_detail">Review Detail</div>
			<div id="cs_content_detail">
				<div id="cs_ratings_detail">
					<div id="cs_close_detail">
			    		<a href="javascript:hideDetail()">Close Window</a>
			    	</div>
			        <div id="cs_stars_detail">
			        	<s:iterator value="review.rating" id="rating"><s:if test="%{2}"><img src='<s:property value="resourceRootPath"/>/static/img/Star.png' /></s:if><s:elseif test="%{1}"><img src='<s:property value="resourceRootPath"/>/static/img/HalfStar.png' /></s:elseif><s:else><img src='<s:property value="resourceRootPath"/>/static/img/EmptyStar.png' /></s:else></s:iterator>
			        </div>
			    </div>
			    <div id="cs_title_detail">
		        	<a href='<s:property value="review.profileTrackingUrl"/>' ><s:property value="review.shortBusinessName"/></a>
		        </div>
		        <div id="cs_address_detail">
		        	<s:property value="review.address.street"/><br />
		        	<s:property value="review.address.state"/>&nbsp;<s:property value="review.address.postalCode"/>
		        </div>
		        <div id="cs_time_detail"><s:property value="review.reviewAuthor"/> on <s:property value="review.reviewDate" /> said:</div>
		        <div id="cs_reviews_detail">
		        	<div id="cs_longTitle_detail">
			        	<a href='<s:property value="review.reviewTrackingUrl"/>' >
			        		<s:property value="review.shortTitle"/>
			        	</a>
		        	</div>
			        <div id="cs_description_detail"><s:property value="review.reviewText"/></div>
			        <div id="cs_share_detail">
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
		                <div class="ctsrch_descFont">
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
</body>
</html>