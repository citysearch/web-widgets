<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ page import="java.util.*" %>
<%@ page import="com.citysearch.value.AdListBean" %>
<%@ page import="com.citysearch.helper.CommonConstants" %>

<html>
<head>
    <script language="Javascript" type="text/javascript">
        function Launch(url)
        {
            window.top.location.href=url;
        }
    </script>
    <jwr:style src="/citysearch.css"/>
</head>
<body marginwidth="0" marginheight="0" topmargin="0" leftmargin="0">
    <div class="ctsrch_boxContainer">
        <div class="ctsrch_header" style="">
            <div class="ctsrch_headerText">More to Try Nearby</div>
        </div>
        <div class="ctsrch_container">
             <!-- The Results Display -->
             <% ArrayList<AdListBean> adList = (ArrayList<AdListBean>) request.getAttribute("ResultList") ;
                 String callBackFunction = (String)request.getParameter(CommonConstants.CALL_BACK_FUNCTION_PARAM);
                 String callBackURL = (String) request.getParameter(CommonConstants.CALL_BACK_URL);
                for(int i = 0,size=adList.size(); i < size; i++){
                    AdListBean adListBean = (AdListBean) adList.get(i);
                    int[] rating = adListBean.getRating();
                    String location = adListBean.getLocation();
                    String listingIdURL = null;
                    if(callBackURL != null && callBackURL.length() > 0){
                        //listingIdURL = callBackURL + "?listing_id=" + adListBean.getListingId() + "&phone=" + adListBean.getPhone();
                        listingIdURL = callBackURL.replace("$l",adListBean.getListingId());
                        listingIdURL = "http://ad.doubleclick.net/clk;225291110;48835962;h?" + listingIdURL.replace("$p",adListBean.getPhone());
                    } else {
                            listingIdURL =  adListBean.getAdDisplayURL();
                    }
             %>
             <div class="ctsrch_leftSide">
                  <div class="ctsrch_bigStar">


            <% if(callBackFunction != null && callBackFunction.length() > 0) {%>

                        <a href="javascript:<%= callBackFunction %>('<%= adListBean.getListingId() %>','<%= adListBean.getPhone() %>')" ><img src="<%= adListBean.getAdImageURL() %>" /></a>
            <%} else {%>
                <a href="<%= listingIdURL %>" ><img src="<%= adListBean.getAdImageURL() %>" /></a>
            <%}%>

                </div>
                <div class="ctsrch_milesFont ctsrch_reviewFont">
                    <%= adListBean.getDistance() %> mi away
                </div>
             </div>
             <div class="ctsrch_rightSide">
                 <div class="ctsrch_mainLink" >
            <% if(callBackFunction != null && callBackFunction.length() > 0) {%>

                        <a class="ctsrch_busNameFont" href="javascript:<%= callBackFunction %>('<%= adListBean.getListingId() %>','<%= adListBean.getPhone() %>')" ><%=  adListBean.getName() %></a>

            <%} else {%>
                <a href="<%= listingIdURL %>" ><%=  adListBean.getName() %></a>
             <%}%>

                </div>
                <% if(adListBean.getRatings() > 2.5 ){ %>
                    <div class="ctsrch_starContainer">
                        <div class="ctsrch_stars">
                            <% for(int val = 0; val < rating.length; val++){
                                switch(rating[val]){
                                    case 2: %><img src="<%= request.getContextPath() %>/static/img/Star.png" class="ctsrch_starImg"/><% break;
                                    case 1: %><img src="<%= request.getContextPath() %>/static/img/HalfStar.png" class="ctsrch_starImg"/><% break ;
                                    case 0: %><img src="<%= request.getContextPath() %>/static/img/EmptyStar.png" class="ctsrch_starImg"/><% break;
                                }
                   } %>
                        </div>
                 <% if(adListBean.getReviewCount() != 0){ %>
                        <div class="ctsrch_reviewFont"><%= adListBean.getReviewCount() %> Reviews</div>
                        </div>
                        <% }
                  }%>
                 <% if(location != null && location.length() > 0){ %>
                        <div class="ctsrch_cityFont">
                           <%= location %>
                        </div>
                 <% } %>
                 <div class="ctsrch_subcategoryFont">
                     <%= adListBean.getCategory() %>
                 </div>
             </div>
             <% if(i < 2) { %>
                 <div class="ctsrch_lineMargin">
                </div>
                 <% } %>
             <% } %>
             </div>
         </div>

</body>
</html>