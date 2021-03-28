<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>Register Failure Page</title>
</head>
<body style="padding-top:150px;">
	<div class="container-lg">
		<p style="color:red;font-size:50px;">
		    Signup failed!
		</p>
		<p style="font-weight:bold">Error Message: <%=request.getAttribute("message")%></p>

	</div>
</body>