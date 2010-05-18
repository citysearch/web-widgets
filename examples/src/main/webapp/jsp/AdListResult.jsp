<%@ taglib uri="http://jawr.net/tags" prefix="jwr"%>
<%@ page import="java.util.*"%>
<%@ page import="com.citysearch.value.AdListBean"%>
<%@ page import="com.citysearch.helper.CommonConstants"%>

<%! String listingIdURL; %>
<% String callBackFunction = (String)request.getAttribute(CommonConstants.CALL_BACK_FUNCTION_PARAM);
    %>
<html>
<head>
<script language="Javascript" type="text/javascript">
	    function Launch(url)
	    {
	    	window.top.location.href=url;
	    }
    </script>
<jwr:style src="/citysearch.css" />
</head>
<body>

<div class="ctsrch_boxContainer">
<div class="ctsrch_header" style="">
<div class="ctsrch_headerText">More to Try Nearby</div>
</div>
<div class="ctsrch_container"><!-- The Results Display --> <% ArrayList<AdListBean> adList = (ArrayList<AdListBean>) request.getAttribute("ResultList") ;           
                for(int i = 0,size=adList.size(); i < size; i++){
                	AdListBean adListBean = (AdListBean) adList.get(i);
                    int[] rating = adListBean.getRating();
                    String     location = adListBean.getLocation();
                    if(callBackFunction != null && callBackFunction.length() > 0){
                    	// This is for debug only out.write(callBackFunction)               ;                                           
                    } else {
                   		 listingIdURL =  adListBean.getAdDisplayURL();
                    } 
             %>
<div class="ctsrch_leftSide">
<div class="ctsrch_bigStar"><a
	href="javascript:<%= callBackFunction %>('<%= adListBean.getListingId() %>')"><img
	src="<%= adListBean.getAdImageURL() %>" /></a></div>
<div class="ctsrch_milesFont ctsrch_reviewFont"><%= adListBean.getDistance() %>
mi away</div>
</div>
<div class="ctsrch_rightSide">
<div class="ctsrch_mainLink"><a class="ctsrch_busNameFont"
	href="javascript:<%= callBackFunction %>('<%= adListBean.getListingId() %>')">
<%=  adListBean.getName() %></a></div>
<% if(adListBean.getRatings() > 2.5 ){ %>
<div class="ctsrch_starContainer">
<div class="ctsrch_stars">
<% for(int val = 0; val < rating.length; val++){
                            	switch(rating[val]){
                                	case 2: %><img
	src="<%= request.getContextPath() %>/img/Star.png"
	class="ctsrch_starImg" />
<% break;
                                    case 1: %><img
	src="<%= request.getContextPath() %>/img/HalfStar.png"
	class="ctsrch_starImg" />
<% break ;
                                    case 0: %><img
	src="<%= request.getContextPath() %>/img/EmptyStar.png"
	class="ctsrch_starImg" />
<% break;
                                }
                   } %>
</div>
<% if(adListBean.getReviewCount() != 0){ %>
<div class="ctsrch_reviewFont"><%= adListBean.getReviewCount() %>
Reviews</div>
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