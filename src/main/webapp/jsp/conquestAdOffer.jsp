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
	    <s:if test="%{!offers.isEmpty()}">	
			<div class="ctsrch_boxContainer_conquestAd">
				<div class="ctsrch_logoFont_conquestAd">Ads by Citygrid</div>
				<div>			
		        	<s:iterator value="offers" status="stat">	 	         	
		        		<div id="cs_longTitle_detail_conquestAd" >
		        			<div class="heading">Special Offers Nearby</div>	        			
		        		</div>	
			        	<div class="ctsrch_leftSide">
			 	         	<div class="ctsrch_maxStar_conquestAd">	
			 	         		<a href='<s:property value="profileUrl" />' >
									<img src='<s:property value="imageUrl" />'/>								
								</a>
			 	         	</div>	 
			 	         	<div class="distance"><s:property value="distance" />&nbsp; miles away</div>	         		         	 
		 	         	</div>
		 	         	<div class="ctsrch_rightSideWideOffers">	
		 	         		<div>
		 	         			<div class="ctsrch_starContainer">	
		         				 	<span>
		         				 		<label class="ctsrch_mainLink_conquestAd"><a href='<s:property value="profileUrl" />' ><s:property value="listingName" /></a></label>
			 	         			</span>
			 	         			<span class="coupon">
					        			<a href='<s:property value="profileUrl" />#target-couponLink' >
											Coupon - Button Link	
					        			</a>
				        			</span>
			 	         		</div> 
		 	         		   	<div class="ctsrch_starContainer">	 	         		   	
		                    		<div class="ctsrch_stars"> 	                    			
	                            		<s:iterator value="listingRating" status="stat"> <span class='<s:if test="%{listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
	                        		</div>
	                        		<div class="title"> 	         					
	                        			<s:property value="offerTitle" />
			         				</div>	
		                    		<div class="description"> 	         					
			         					<s:property value="offerDescription" />        						                   		
			         				</div>	                    		     		
		                    	</div>
		                    	<div>                    		
		                    		<label class="ctsrch_reviewFont" > <s:property value="reviewCount" /> &nbsp; Reviews</label>	 	         			
		                    	</div>
			 	         		<div class="ctsrch_cityFont">
		         					<s:property value="location" />        					
		         				</div>
		 	         		</div> 	         				       				        				
		 	         	</div> 					              
		 	        </s:iterator>	 	        
		        </div>
			</div>
		</s:if>			
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
        <s:if test="%{!houseAds.isEmpty()}">	
			<div class="ctsrch_boxContainer_conquestAd">
				<div class="ctsrch_logoFont_conquestAd">Ads by Citygrid</div>
				<div>					
			        <s:iterator value="houseAds" status="hadStatus">
			            <div class="ctsrch_leftSide">
			            	<a href=#" title="placeholder" ><img src="" height="99" width="99" />
			            </div>
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
		</s:if>  			        				
	</body>
</html>