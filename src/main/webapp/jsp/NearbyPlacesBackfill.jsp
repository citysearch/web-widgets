<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Nearby Places</title>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/ie-style.css'  rel="stylesheet"/>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
	</head>
	<body>
		<div class="ctsrch_boxContainer">
	        <div class="ctsrch_header" style="">
	            <div class="ctsrch_headerText">Backfill</div>
	        </div>
	        <div class="ctsrch_container">
	        	<s:iterator value="nearbyPlaces" status="placesStatus">
	        		<div class="ctsrch_leftSide">
	        			<a target="_blank" href='<s:property value="adDestinationUrl" />'><img src='<s:property value="adImageURL" />'/></a>
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
	        		<s:if test="%{#placesStatus.index < 2}">
	 	         		<div class="ctsrch_lineMargin"></div>
	 	         	</s:if>
	        	</s:iterator>
	        </div>
		</div>
	</body>
</html>