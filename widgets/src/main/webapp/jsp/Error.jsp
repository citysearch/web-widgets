<%@ page isErrorPage="true" import="util.AdListConstants" %>

<% String errMsg = (String) session.getAttribute(AdListConstants.ERR_MSG); %>
<html>
<body>
	<%= errMsg %> is required.Please modify Your Search criteria and try again
</body>
</html>