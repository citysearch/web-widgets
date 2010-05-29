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
		    <div id="cs_img"><img src="static/img/img.jpg" /></div>
		    <div id="cs_ratings">
		        <div id="cs_stars">
		        	<img src="static/img/star.jpg" />
		        </div>
		        <div id="cs_title">
		        	<a href="#"><s:property value="review.businessName"/></a>
		        </div>
		        <div id="cs_address">
		        	<s:property value="review.address.street"/><br />
		        	<s:property value="review.address.state"/>&nbsp;<s:property value="review.address.postalCode"/>
		        </div>
		    </div>
		    <div id="cs_time">Novac said 5 hours ago:</div>
		    <div id="cs_reviews">
		        <div id="cs_longTitle"><s:property value="review.reviewTitle"/></div>
		        <div id="cs_description"><s:property value="review.reviewText"/></div>
		    </div>
		    <div id="cs_share">
		    	<a href='<s:property value="review.sendToFriendUrl"/>'>Share this review</a>
		    </div>
		</div>
	</div>
</body>
</html>