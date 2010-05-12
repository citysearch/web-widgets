<%@ taglib uri="http://jawr.net/tags" prefix="jwr"%>
<%@ page import="java.util.*"%>
<%@ page import="com.citysearch.value.AdListBean"%>


<%! ArrayList adList ;
	AdListBean adListBean;
	String contextPath = null;
	String callbackURL;
	String listingIdURL;
	String cssfile;
	String callbackfunction;
	
	int[] rating;
	double ratings;
	String location;
	int userReviewCount;
%>

<% contextPath = request.getContextPath(); 
	  adList = (ArrayList) request.getAttribute("ResultList");
	  cssfile = (String)request.getAttribute("cssfile");
	  callbackfunction = (String)request.getAttribute("callbackfunction");
  %>


<html>
<head>
<script language="Javascript" type="text/javascript">
		
		var cssfile = <%= cssfile %>;

		function Launch(url)
		{
			window.top.location.href=url;
		}
		
		if( cssfile != null && cssfile.length() > 0){
			document.write("<link rel='stylesheet' type='text/css' href= ' <%= cssfile %>' />");
		}else
			<jwr:style src="/bundles/all.css" />
		
	</script>

</head>
<body>

<div class="ctsrch_boxContainer">
<div class="ctsrch_header" style="">
<div class="ctsrch_headerText">More to Try Nearby</div>
</div>
<div class="ctsrch_container"><!-- The Results Display --> <%  callbackURL = (String) request.getAttribute("callbackURL"); %>
<% 	for(int i = 0,size=adList.size(); i < size; i++){
						adListBean = (AdListBean) adList.get(i);
						rating = adListBean.getRating();
				  		location = adListBean.getLocation();
						userReviewCount = adListBean.getReviewCount();
						
						if(callbackURL != null && callbackURL.length() > 0){
							listingIdURL =  callbackURL + "?listing_id=" + adListBean.getListingId();
						} else if(callbackfunction != null && callbackfunction.length() > 0){
								out.write(callbackfunction)	;			
						} else {
							listingIdURL =  adListBean.getAdDisplayURL();
						}
						
				%>

<div class="ctsrch_leftSide">
<div class="ctsrch_bigStar"><a
	href="javascript:Launch('<%= listingIdURL %>')"><img
	src="<%= adListBean.getAdImageURL() %>" /></a></div>
<div class="ctsrch_milesFont ctsrch_reviewFont"><%= adListBean.getDistance() %>
mi away</div>
</div>


<div class="ctsrch_rightSide">
<div class="ctsrch_mainLink"><a class="ctsrch_busNameFont"
	href="javascript:Launch('<%= listingIdURL %>')"><%=  adListBean.getName() %></a>
</div>
<% if(adListBean.getRatings() != 0.0){ %>
<div class="ctsrch_starContainer">
<div class="ctsrch_stars">
<% for(int val = 0; val < rating.length; val++){ 
									 switch(rating[val]){ 
									   case 2: %><img src="img/Star.png" class="ctsrch_starImg" />
<% break;
										case 1: %><img src="img/HalfStar.png" class="ctsrch_starImg" />
<% break ;
										case 0: %><img src="img/EmptyStar.png" class="ctsrch_starImg" />
<% break;
									}
								} %>
</div>
<% if(userReviewCount != 0){ %>
<div class="ctsrch_reviewFont"><%= userReviewCount %> Reviews</div>
</div>
<% }
						 }%> <% if(location != null && location.length() > 0){ %>
<div class="ctsrch_cityFont"><%= location %></div>
<% } %>

<div class="ctsrch_subcategoryFont"><%= adListBean.getCategory() %>
</div>
</div>



<% if(i < 2) { %>
<div class="ctsrch_lineMargin"></div>
<% } %> <% } %>
</div>
</div>

</body>
</html>
