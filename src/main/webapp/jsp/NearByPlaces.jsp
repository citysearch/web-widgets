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
	            <div class="ctsrch_headerText">More to Try</div>
	        </div>
	        <div class="ctsrch_container">
	        	<s:iterator value="nearbyPlaces" status="placesStatus">
		        	<div class="ctsrch_leftSide">
		 	         	<div class="ctsrch_bigStar">
		 	         		<s:if test="%{isValidCallbackFunction == true}">
		 	         			<a href='<s:property value="callBackFunction" />'><img src='<s:property value="adImageURL" />'/></a>
		 	         		</s:if>
		 	         		<s:else>
		 	         			<a href='<s:property value="listingUrl" />' ><img src='<s:property value="adImageURL" />'/></a>
		 	         		</s:else>
		 	         	</div>
		 	         	<div class="ctsrch_milesFont ctsrch_reviewFont">
		 	         		<s:property value="distance" /> mi away
	                	</div>  
	 	         	</div>
	 	         	<div class="ctsrch_rightSide">
	 	         		<div class="ctsrch_mainLink" >
	 	         			<s:if test="%{isValidCallbackFunction == true}">
	 	         				<a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
	 	         			</s:if>
	 	         			<s:else>
	 	         				<a href='<s:property value="listingUrl" />' ><s:property value="name" /></a>
	 	         			</s:else>
	 	         		</div>
	 	         		<s:if test="%{ratings > 2.5}">
		 	         		<div class="ctsrch_starContainer">
		                    	<div class="ctsrch_stars">
		                    		<s:iterator value="rating"><s:if test="%{2}"><img src='<s:property value="resourceRootPath"/>/static/img/Star.png' class="ctsrch_starImg"/></s:if><s:elseif test="%{1}"><img src='<s:property value="resourceRootPath"/>/static/img/HalfStar.png' class="ctsrch_starImg"/></s:elseif><s:else><img src='<s:property value="resourceRootPath"/>/static/img/EmptyStar.png' class="ctsrch_starImg"/></s:else></s:iterator>
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
	 	         	<s:if test="%{#placesStatus.index < 2}">
	 	         		<div class="ctsrch_lineMargin"></div>
	 	         	</s:if>
	 	         	
 	         	</s:iterator>
	        </div>
		</div>
	</body>
</html>