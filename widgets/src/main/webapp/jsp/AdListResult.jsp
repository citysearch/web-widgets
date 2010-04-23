<%@ page import="java.util.*" %>
<%@ page import="util.AdListBean" %>


<%! ArrayList adList ;
	AdListBean adListBean;
	String contextPath = null;
	String callbackURL;
	String listingIdURL;
	
	String name;
	int[] rating;
	String location;
	double distance;
	int userReviewCount;
	String category;
	int resultListSize;

%>
   

   <% contextPath = request.getContextPath(); 
	  adList = (ArrayList) request.getAttribute("ResultList");
	  resultListSize = adList.size(); %>
	  

<html>
<head>
	<script language="Javascript" type="text/javascript">
			if (navigator.appName == "Microsoft Internet Explorer") {
					document.write("<link rel='stylesheet' type='text/css' href= ' <%= contextPath %>/css/ie-style.css' />");
			}
			else {
					document.write("<link rel='stylesheet' type='text/css' href= ' <%= contextPath %>/css/style.css' />");
			}
	</script>
</head>
<body>
				<% if (resultListSize > 0) { %>
    <div class="boxContainer">
        <div class="header">
            Nearby Places
        </div>
        <div style="padding-left: 6px;">
				<div class="ddbDiv">
					<select id="Select1" name="D1" style="font-size: 9px; width: 129px;">
						<option>Restaurants-All</option>
						<option>Pizza</option>
						<option>Bakery</option>
					</select>
				</div>
						
				
				<!-- The Results Display -->
				<%  callbackURL = (String) request.getAttribute("callbackURL"); %>

					<!-- This condition is for temporary testing as callbackURL is always sent from host -->
				
				
					<!-- The iteration is limited to 3 only temporarily.It should be replaced by size later -->
					<% if(resultListSize > 3)
						 resultListSize = 3; %>
						 
				<% for(int i = 0; i < resultListSize; i++){
						
						adListBean = (AdListBean) adList.get(i);
						//listingIdURL = callbackURL + adListBean.getListingId(); 
						 name = adListBean.getName();
						 rating = adListBean.getRating(); 
						location = adListBean.getLocation();
						distance = adListBean.getDistance();
						userReviewCount = adListBean.getReviewCount();
						category = adListBean.getCategory();
						if(callbackURL != null && callbackURL.length() > 0){
							listingIdURL =  callbackURL + "?listing_id=" + adListBean.getListingId();
						} else {
							listingIdURL =  adListBean.getAdDisplayURL();
						}%>


						<div class="divStar" >
							<a class="busNameFont" href="  <%= listingIdURL %>  "><%= name %></a>
						</div>
						<div class="divStarContainer">
										<% for(int val = 0; val < rating.length; val++){ 
											 switch(rating[val]){ 
											   case 2: %><img src="img/Star.png" class="starImg"/><% break;
												case 1: %><img src="img/HalfStar.png" class="starImg"/><% break ;
												case 0: %><img src="img/EmptyStar.png" class="starImg"/><% break;
											}
										} %>
										<span class="reviewFont"><%= userReviewCount %> Reviews</span>
						</div>
						<div class="cityDistance">
							<div class="cityFont"><%= location %></div>							
							<div class="distanceFont"><%= distance %> mi away</div>
						</div>
						<div class="subcategoryFont">
						<%= category %>
						</div>
					<% if(i < 2) { %>
							<div class="lineMargin">
							</div>
					<% } %>
				<% } %>
			</div>
			<div style="text-align:right;padding-right:4px"><img src="img/logo.png" alt="Citysearch"/></div>
		</div>
<% } else {%>
					<h4>No Search Results Found.Please Modify Your Search Parameters and Try Again</h4>
				<% } %>
		
</body>
</html>
