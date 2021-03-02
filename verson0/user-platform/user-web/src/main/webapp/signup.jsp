<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>Register Page</title>
</head>
<body style="padding-top:150px;">
	<div class="container-lg">
		<form id="regForm" action="/register">
		  <table style="table-layout:fixed;">
		    <tr>
		        <td align="right">姓名：</td>
		        <td><input name="name" type="text"/></td>
		        <td align="right">密码：</td>
		        <td><input name="password" type="password"/></td>
		    </tr>
		    <tr>
		        <td align="right">电子邮件：</td>
                <td><input name="email" type="text"/></td>
                <td align="right">电话号码：</td>
                <td><input name="phoneNumber" type="text"/></td>
            </tr>
            <tr>
                <td align="right" colspan="4"><input type="submit" value="提交" /></td>
            </tr>
		  </table>
		</form>
	</div>
</body>