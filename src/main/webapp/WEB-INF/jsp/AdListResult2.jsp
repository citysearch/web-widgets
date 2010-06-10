<%@ page import="java.util.*" %>
<%@ page import="com.citysearch.value.AdListBean" %>
<%@ page import="com.citysearch.helper.CommonConstants" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

var content = [];

<%-- Widget Container --%>
content.push('<div class="ctsrch_wideContainer">');

<%-- Header --%>
content.push('<div class="ctsrch_sponserText">Ads by CityGrid Media</div>');
content.push('<div class="ctsrch_container">');

<%-- Loop for 2 Businesses --%>
<%
    ArrayList<AdListBean> adList = (ArrayList<AdListBean>) request.getAttribute("ResultList") ;
    String callBackFunction = (String)request.getParameter(CommonConstants.CALL_BACK_FUNCTION_PARAM);
    String callBackURL = (String) request.getParameter(CommonConstants.CALL_BACK_URL);
    for(int i = 0,size=adList.size(); i < size && i < 2; i++){
        AdListBean adListBean = (AdListBean) adList.get(i);
        int[] rating = adListBean.getRating();
        String location = adListBean.getLocation();
        String listingIdURL = null;
        if (callBackURL != null && callBackURL.length() > 0) {
            listingIdURL = callBackURL.replace("$l",adListBean.getListingId());
            listingIdURL = "http://ad.doubleclick.net/clk;225291110;48835962;h?" + listingIdURL.replace("$p",adListBean.getPhone());
        } else {
            listingIdURL =  adListBean.getAdDisplayURL();
        }
%>
content.push('<div class="ctsrch_listing">');

<%-- Left Side (Image) --%>
content.push('<div class="ctsrch_leftSide">');
content.push('    <div class="ctsrch_bigStar">');
<% if (callBackFunction != null && callBackFunction.length() > 0) { %>
content.push('        <a href="javascript:<%= callBackFunction %>(\'<%= adListBean.getListingId() %>\',\'<%= adListBean.getPhone() %>\')" ><img src="<%= adListBean.getAdImageURL() %>" /></a>');
<% } else {	%>
content.push('        <a href="<%= listingIdURL %>" ><img src="<%= adListBean.getAdImageURL() %>" /></a>');
<% }%>
content.push('    </div>');
content.push('    <div class="ctsrch_milesFont ctsrch_reviewFont">');
content.push('        <%= adListBean.getDistance() %> mi away');
content.push('    </div>');
content.push('</div>');

<%-- Right Side (Business Details) --%>
content.push('<div class="ctsrch_rightSide">');

<%-- Business Name --%>
content.push('<div class="ctsrch_mainLink" >');
<% if(callBackFunction != null && callBackFunction.length() > 0) {%>
content.push('    <a class="ctsrch_busNameFont" href="javascript:<%= callBackFunction %>(\'<%= adListBean.getListingId() %>\',\'<%= adListBean.getPhone() %>\')" ><c:out value="<%=  adListBean.getName() %>" /></a>');
<%} else {%>
content.push('    <a href="<%= listingIdURL %>" ><c:out value="<%=  adListBean.getName() %>" /></a>');
<%}%>
content.push('</div>');


<%-- Stars and Reviews Count --%>
<% if(adListBean.getRatings() > 2.5 ){ %>
content.push('<div class="ctsrch_starContainer">');
content.push('    <div class="ctsrch_stars">');
<% for (int val = 0; val < rating.length; val++) {
       switch (rating[val]) {
           case 2: %>content.push('<span class="full">&nbsp;</span>');<% break;
           case 1: %>content.push('<span class="half">&nbsp;</span>');<% break;
           case 0: %>content.push('<span class="empty">&nbsp;</span>');<% break;
       }
   } %>
content.push('    </div>');
<% if(adListBean.getReviewCount() != 0){ %>
content.push('    <div class="ctsrch_reviewFont"><%= adListBean.getReviewCount() %> Reviews</div>');
<% } %>
content.push('</div>');
<% } %>

<%-- City --%>
<% if(location != null && location.length() > 0){ %>
content.push('<div class="ctsrch_cityFont">');
content.push('    <c:out value="<%= location %>" />');
content.push('</div>');
<% } %>

<%-- Business Category --%>
content.push('<div class="ctsrch_subcategoryFont">');
content.push('    <c:out value="<%= adListBean.getCategory() %>" />');
content.push('</div>');

<%-- Closing Right Side (Business Details) --%>
content.push('</div>');
content.push('</div>');

<%-- Closing Loop, Content Container and Widget Container--%>
<% } %>
content.push('</div>');
content.push('</div>');

content = content.join('');
citygrid.common.loadWidget(content);