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
			<div class="ctsrch_container">
	        	<s:iterator value="offers" status="stat">	 	         	
	        		<div class="cs_offer_text" >
	        			<s:property value="offerTitle" />
	        		</div>
	        		<div class="cs_offer_getoffer">
	        			<a target="_blank" href='<s:property value="couponUrl" />' >get offer</a>
	        		</div>	
		        	<div class="ctsrch_leftSide">
		 	         	<div class="ctsrch_bigStar">	
		 	         		<a href='<s:property value="profileTrackingUrl" />' >
								<img src='<s:property value="imageUrl" />'/>								
							</a>
		 	         	</div>	 	         		         	 
	 	         	</div>
	 	         	<div class="ct_offer_rightSide">	
	 	         		<div>
	 	         		   	<div class="ctsrch_starContainer">	 	         		   	
	                    		<div class="ctsrch_stars"> 
                            		<s:iterator value="listingRating" status="stat"><span class='<s:if test="%{listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        		</div>           		                		
	                    		<div class="ctsrch_reviewFont">                    		
	                    			<s:property value="reviewCount" /> &nbsp; Reviews 	         			
	                    		</div>          		
	                    	</div>	                    	
	         				<div class="ctsrch_mainLink">	
	         				 	<a href='<s:property value="profileTrackingUrl" />' ><s:property value="listingName" /></a>
		 	         		</div> 
		 	         		<div class="ctsrch_cityFont">
	         					<s:property value="location" />          					
	         				</div>
	         				<div class="ctsrch_subcategoryFont"> 	         					
	         					<s:property value="offerDescription" />        						                   		
	         				</div>
	 	         		</div> 	         				       				        				
	 	         	</div>		
	 	         	<s:if test="%{#stat.index < (offersList.size()-1)}">
		 	        	<div class="ctsrch_lineMargin"></div>
					</s:if>         					              
	 	        </s:iterator>
	        </div>
	        <s:if test="%{!backfill.isEmpty() && !offers.isEmpty()}">
              	<div class="ctsrch_lineMargin"></div>
         	</s:if>
	        <s:iterator value="backfill" status="placesStatus">
	            <div class="ctsrch_leftSide">
	                <a href='<s:property value="adDisplayTrackingURL" />'><img src='<s:property value="adImageURL"/>' border="0"/></a>
	            </div>
	            <div class="ctsrch_rightSide">
	                <div class="ctsrch_mainLink_bf" >
	                    <a href='<s:property value="adDisplayTrackingURL" />'><s:property value="category" /></a>
	                </div>
	                <div class="ctsrch_descFont" >
	                    <s:property value="description" />
	                </div>
	                <div class="ctsrch_subLink_bf" >
	                    <a href='http://<s:property value="adDisplayTrackingURL" />'><s:property value="adDisplayURL" /></a>
	                </div>
	                <s:if test='%{offers != null && !"".equals(offers)}'>
	                 <div class="ctsrch_offersFont" >
	                     <s:property value="offers" />
	                 </div>
	                </s:if>
	            </div>
	            <s:if test="%{#placesStatus.index < (backfill.size() - 1)}">
	                  <div class="ctsrch_lineMargin"> </div>
	              </s:if>
	        </s:iterator>
	        <s:if test="%{(!backfill.isEmpty() || !offers.isEmpty()) && !houseAds.isEmpty()}">
	              <div class="ctsrch_lineMargin"></div>
	         </s:if>
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
	        <div class="ctsrch_logoFont" style="float:right;font-size:11px;padding-top:9px;padding-right:5px;">Ads by Citysearch</div>	        
		</div>
	</body>
</html>