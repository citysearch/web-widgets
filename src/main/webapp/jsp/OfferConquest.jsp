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
		<div class="ctsrch_wideContainer">
			<div class="ctsrch_sponserText">Ads by Citygrid</div>
			<div class="ctsrch_container">					
    			<s:if test="%{offer != null}">	
        			<div class="ctsrch_listing">	         	
			        	<div class="ctsrch_leftSide">
			 	         	<div class="ctsrch_bigStar">	
			 	         		<a href='<s:property value="offer.profileTrackingUrl" />' >
									<img src='<s:property value="offer.imageUrl" />'/>								
								</a>
			 	         	</div>	 
			 	         	<div class="ctsrch_milesFont ctsrch_reviewFont"><s:property value="offer.distance" />&nbsp; mi away</div>	         		         	 
		 	         	</div>
		 	         	<div class="ctsrch_rightSide">	
		 	         		<div class="ctsrch_mainLink">
         				 		<a href='<s:property value="offer.profileTrackingUrl" />' ><s:property value="offer.listingName" /></a>
         				 	</div>
	 	         			<div class="ctsrch_starContainer">	
	         				 	<div class="ctsrch_stars"> 	                    			
                            		<s:iterator value="offer.listingRating" status="stat"> <span class='<s:if test="%{offer.listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{offer.listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        		</div>
                        		<div class="ctsrch_reviewFont">                    		
	                    			<s:property value="offer.reviewCount" /> &nbsp; Reviews	 	         			
	                    		</div>
		 	         		</div> 
		 	         		<div class="ctsrch_cityFont">
	         					<s:property value="offer.location" />        					
	         				</div>
		 	         	</div> 	
 	         		</div>	
 	         		<div class="ctsrch_listing">
 	         			<div style="float:left;width:100%;padding-top:4px;">	
 	         				<div class="ctsrch_offer_title_conquest"> 	         					
                       			<s:property value="offer.offerTitle" />
	         				</div>	
                    		<div class="ctsrch_offer_description_conquest"> 	         					
	         					<s:property value="offer.offerDescription" />        						                   		
	         				</div>	
	         				<div class="ctsrch_offer_coupon">
			        			<a href='<s:property value="offer.profileTrackingUrl" />#target-couponLink' >
									Get Offer	
			        			</a>
	        				</div> 
 	         			</div>
 	         		</div>			              
				</s:if>			
		        <s:elseif test="%{!houseAds.isEmpty()}">	
					<s:iterator value="houseAds" status="hadStatus">
			            <div class="ctsrch_listing">
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
			            </div>
			        </s:iterator>
				</s:elseif> 
			</div> 
		</div>
	</body>
</html>