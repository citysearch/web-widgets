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
		<div class="ctsrch_boxContainer">
			<div>
	        	<s:iterator value="offersList" status="stat">	 	         	
	        		<div id="cs_longTitle_detail" >
	        			<s:property value="offerTitle" />
	        			<span style="float:right;padding-right:10px;">
	        			<a target="_blank" href='<s:property value="couponUrl" />' >
							get offer		
	        			</a>
	        			</span>
	        		</div>	
		        	<div class="ctsrch_leftSide">
		 	         	<div class="ctsrch_maxStar">	
		 	         		<a href='<s:property value="profileTrackingUrl" />' >
								<img src='<s:property value="imageUrl" />'/>								
							</a>
		 	         	</div>	 	         		         	 
	 	         	</div>
	 	         	<div class="ctsrch_rightSideWide_Offer">	
	 	         		<div>
	 	         		   	<div class="ctsrch_starContainer">	 	         		   	
	                    		<div class="ctsrch_stars"> 
                            		<s:iterator value="listingRating" status="stat"><span class='<s:if test="%{listingRating[#stat.index] == 2}">full</s:if><s:elseif test="%{listingRating[#stat.index] == 1}">half</s:elseif><s:else>empty</s:else>'> </span></s:iterator>
                        		</div>           		                		
	                    		<div>                    		
	                    			<label class="ctsrch_reviewFont_Offer" > <s:property value="reviewCount" /> &nbsp; Reviews</label>	 	         			
	                    		</div>          		
	                    	</div>	                    	
	         				<div class="ctsrch_starContainer">	
	         				 	<label style="float:left;" class="ctsrch_mainLink"><a href='<s:property value="profileTrackingUrl" />' ><s:property value="listingName" /></a></label>
		 	         		</div> 
		 	         		<div style="float:left;" class="ctsrch_cityFont_Offer">
	         					<s:property value="city" /> , <s:property value="state" />          					
	         				</div>
	         				<div style="font-size:13px;float:left;width:69%;"> 	         					
	         					<s:property value="offerDescription" />        						                   		
	         				</div>
	 	         		</div> 	         				       				        				
	 	         	</div>		
	 	         	<s:if test="%{#stat.index == 0 }">
		 	        	<div class="ctsrch_lineMargin" style="height:9px;"></div>
					</s:if>         					              
	 	        </s:iterator>
	        </div>
	        <div class="ctsrch_logoFont" style="float:right;font-size:11px;padding-top:9px;padding-right:5px;">Ads by Citysearch</div>	        
		</div>
	</body>
</html>