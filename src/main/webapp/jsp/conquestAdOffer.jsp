<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags" %>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Matching Offers</title>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/ie-style.css'  rel="stylesheet"/>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/citysearch.css'  rel="stylesheet"/>
		<link type="text/css" href='<s:property value="resourceRootPath"/>/static/css/Review.css'  rel="stylesheet"/>		
	</head>
	<body>
		<div class="ctsrch_boxContainer_conquestAd">
			<div class="ctsrch_logoFont_conquestAd">Ads by Citygrid</div>
			<div>
	        	<s:iterator value="offersList" status="stat">	 	         	
	        		<div id="cs_longTitle_detail_conquestAd" >
	        			<!--<s:property value="offerTitle" />-->
	        			<div style="font-size:10px;padding-left:40px;">Special Offers Nearby</div>
	        			<!--
	        			<span style="float:right;padding-right:35px;font-size:12px;width:90px:height:15px;">
		        			<a target="_blank" href='<s:property value="profileUrl" />#target-couponLink' >
								Coupon - Button Link	
		        			</a>
	        			</span>
	        			-->
	        		</div>	
		        	<div class="ctsrch_leftSide">
		 	         	<div class="ctsrch_maxStar_conquestAd">	
		 	         		<a target="_blank" href='<s:property value="profileUrl" />' >
								<img src='<s:property value="imgUrl" />'/>								
							</a>
		 	         	</div>	 
		 	         	<div style="font-size:10px;"><s:property value="distance" />&nbsp; miles away</div>	         		         	 
	 	         	</div>
	 	         	<div class="ctsrch_rightSideWideOffers">	
	 	         		<div>
	 	         			<div class="ctsrch_starContainer">	
	         				 	<span>
	         				 		<label style="float:left;width:60%" class="ctsrch_mainLink"><a target="_blank" href='<s:property value="profileUrl" />' ><s:property value="listingName" /></a></label>
		 	         			</span>
		 	         			<span style="float:right;font-size:12px;background-color:orange;padding:4px;">
				        			<a target="_blank" href='<s:property value="profileUrl" />#target-couponLink' >
										Coupon - Button Link	
				        			</a>
			        			</span>
		 	         		</div> 
	 	         		   	<div class="ctsrch_starContainer">	 	         		   	
	                    		<div class="ctsrch_stars"> 	                    			
                            		<s:iterator value="listingRating" status="stat"> <span class='<s:if test="%{listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        			<!--<label class="ctsrch_reviewFont" style="float:right;"> <s:property value="reviewCount" /> &nbsp; Reviews</label>-->	 	         			
                        		</div>
                        		<div style="font-size:11px;float:right;width:67%;padding-top:10px;font-weight:bold;"> 	         					
                        			<s:property value="offerTitle" />
		         				</div>	
	                    		<div style="font-size:10px;float:right;width:67%;"> 	         					
		         					<s:property value="offerDescription" />        						                   		
		         				</div>	                    		     		
	                    	</div>
	                    	<div>                    		
	                    		<label style="width:32%" class="ctsrch_reviewFont" > <s:property value="reviewCount" /> &nbsp; Reviews</label>	 	         			
	                    	</div>
		 	         		<div style="float:left;" class="ctsrch_cityFont">
	         					<s:property value="city" /> <!-- , <s:property value="state" />  -->        					
	         				</div>
	 	         		</div> 	         				       				        				
	 	         	</div> 					              
	 	        </s:iterator>
	        </div>
		</div>
	</body>
</html>