<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>   
<html>
<head>
	
	<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
	
</head>
<body>
<div class="ctsrch_wideContainer">
	<div class="ctsrch_sponserText">
		Ads by Citygrid
	</div>
	<div class="ctsrch_container clearfix">					
  		<s:if test="%{offer != null}">	
      		<div class="ctsrch_listing">
      			<div class="ctsrch_offers_caption">
					Special Offers Nearby
				</div>	
	        	<div class="ctsrch_leftSide">
		 	         <div class="ctsrch_bizPhoto">	
	 	         		<a href='<s:property value="offer.profileTrackingUrl" />' >
							<img src='<s:property value="offer.imageUrl" />'/>								
						</a>
		 	         </div>	 
	 	         	<div class="ctsrch_milesFont"><s:property value="offer.distance" />&nbsp; mi away</div>	         		         	 
 	         	</div>
 	         	<div class="ctsrch_rightSide">	
 	         		<div class="ctsrch_mainLink">
       					<a href='<s:property value="offer.profileTrackingUrl" />' ><s:property value="offer.listingName" /></a>
       				</div>
	         		<div class="ctsrch_starContainer clearfix">	
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
       			<div class="ctsrch_offers_description">	
       				<div class="ctsrch_getOfferButton_div">
		       			<a class="ctsrch_getOfferButton" href='<s:property value="offer.couponUrl" />#target-couponLink' >
							Get Offer	
		       			</a>
     				</div> 
       				<div class="ctsrch_offer_title_conquest"> 	         					
                   		<s:property value="offer.offerTitle" />
      				</div>	
                		<div class="ctsrch_offer_description_conquest"> 	         					
      					<s:property value="offer.offerDescription" />        						                   		
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