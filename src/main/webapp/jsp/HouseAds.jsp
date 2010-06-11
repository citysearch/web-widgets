<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>House Ads</title>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/ie-style.css'  rel="stylesheet"/>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
	</head>
	<body>
		<div class="ctsrch_boxContainer">
	        <div class="ctsrch_header" style="">
	            <div class="ctsrch_headerText">Ads</div>
	        </div>
	        <div class="ctsrch_container">
	        	<s:iterator value="houseAds" status="hadStatus">
	        		<div class="ctsrch_leftSide">
	        			<!-- 
	        			<a target="_blank" href='<s:property value="destinationUrl" />'><img src='<s:property value="imageURL" />'/></a>
	        			-->
	        		</div>
	        		<div class="ctsrch_rightSide">
	        			<div class="ctsrch_mainLink">
	        				<a target="_blank" href='<s:property value="destinationUrl" />'><s:property value="title" /></a>
	        			</div>
	        			<div class="ctsrch_descFont">
	        				<s:property value="tagLine" />
	        			</div>
	        		</div>
	        		<s:if test="%{#hadStatus.index < 2}">
	 	         		<div class="ctsrch_lineMargin"></div>
	 	         	</s:if>
	        	</s:iterator>
	        </div>
		</div>
	</body>
</html>