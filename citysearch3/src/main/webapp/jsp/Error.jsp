<%@ page isErrorPage="true" import="com.citysearch.util.AdListConstants" %>

<% String errMsg = (String) session.getAttribute(AdListConstants.ERR_MSG);
	if(errMsg != null && errMsg.length() > 0){
		errMsg = errMsg + " is required.Please modify Your Search criteria and try again";
	}else{
		errMsg = "Unexpected Error Occurred";
	}
%>
<html>
<body>
	<%= errMsg %> 
</body>
</html>