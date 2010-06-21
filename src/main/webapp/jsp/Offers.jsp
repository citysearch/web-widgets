<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Matching Offers</title>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
	</head>
	<body>
		<div class="ctsrch_boxContainer">
			<div class="ctsrch_header" style="">
		        <div class="ctsrch_headerText">Offers</div>
		    </div>
			<div class="ctsrch_container">
	        	<s:iterator value="offersList" status="stat">	 	         	
	        		<div class="ctsrch_offerFont"><s:property value="offerTtl" /></div>	
	        		<div class="ctsrch_getOffer"><a href='<s:property value="profileUrl" />' >Get Offer</a></div>
		        	<div class="ctsrch_leftSide">
		 	         	<div class="ctsrch_bigStar">	
		 	         		<a href='<s:property value="profileUrl" />' ><img src='<s:property value="imgUrl" />' border="0"/></a>
		 	         	</div>	 	         		         	 
	 	         	</div>
	 	         	<div class="ctsrch_rightSide">	
	 	         		<div class="ctsrch_mainLink">	
         				 	<a href='<s:property value="profileUrl" />' ><s:property value="listingName" /></a>
	 	         		</div> 
 	         		   	<div class="ctsrch_starContainer">
                    		<div class="ctsrch_stars">
                           		<s:iterator value="csRating"><span class='<s:if test="%{2}">full</s:if><s:elseif test="%{1}">half</s:elseif><s:else>empty</s:else>'></span></s:iterator>
                       		</div>	                    			                    		
                    		<div class="ctsrch_reviewFont"><s:property value="reviewCount" />&nbsp;Reviews</div>          		
                    	</div>	                    	
	 	         		<div class="ctsrch_cityFont">
         					<s:property value="city" /> , <s:property value="state" />          					
         				</div>
         				<div class="ctsrch_descFont"> 	         					
         					<s:property value="offerDesc" />        						                   		
         				</div>
	 	         	</div>		
	 	         	<s:if test="%{#stat.index < (offersList.size() - 1)}">
		 	        	<div class="ctsrch_lineMargin" style="height:9px;"></div>
					</s:if>         					              
	 	        </s:iterator>
	        </div>
		</div>
	</body>
</html>