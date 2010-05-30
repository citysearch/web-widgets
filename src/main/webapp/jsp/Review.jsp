<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Recent Reviews Near You</title>
	<link type="text/css" href="static/css/Review.css"  rel="stylesheet"/>
</head>
<body>
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
		        	<s:iterator value="review.rating" id="rating">
		        		<s:if test="%{2}">
						    <img src="static/img/Star.png" />
						</s:if>
						<s:elseif test="%{1}">
						    <img src="static/img/HalfStar.png" />
						</s:elseif>
						<s:else>
						    <img src="static/img/EmptyStar.png" />
						</s:else>
		        	</s:iterator>
		        </div>
		        <div id="cs_title">
		        	<a href='<s:property value="review.profileUrl"/>' target="_blank"><s:property value="review.businessName"/></a>
		        </div>
		        <div id="cs_address">
		        	<s:property value="review.address.street"/><br />
		        	<s:property value="review.address.state"/>&nbsp;<s:property value="review.address.postalCode"/>
		        </div>
		    </div>
		    <div id="cs_time"><s:property value="review.reviewAuthor"/> said <s:property value="review.timeSinceReviewString" /> ago:</div>
		    <div id="cs_reviews">
		        <div id="cs_longTitle">
		        	<a href='<s:property value="review.reviewUrl"/>' target="_blank">
		        		<s:property value="review.reviewTitle"/>
		        	</a>
		        </div>
		        <div id="cs_description"><s:property value="review.reviewText"/></div>
		        <div id="cs_description">
		        	<br/>
		        	<strong>Pros:</strong>&nbsp;<s:property value="review.pros"/><br/> 
		            <strong>Cons:</strong>&nbsp;<s:property value="review.cons"/> 
		        </div>
		    </div>
		    <div id="cs_share">
		    	<a href='<s:property value="review.sendToFriendUrl"/>' target="_blank">Share this review</a>
		    </div>
		</div>
	</div>
</body>
</html>