<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>User List Page</title>
</head>
<body style="padding-top:150px;">
	 <table class="table table-bordered" style="width:50%;text-align:center;">
        <thead>
            <tr>
                <th>id</th>
                <th>姓名</th>
                <th>密码</th>
                <th>电子邮件</th>
                <th>手机号码</th>
            </tr>
        </thead>
          <c:forEach items="${users}" var="user">
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.password}</td>
                <td>${user.email}</td>
                <td>${user.phoneNumber}</td>
            </tr>
          </c:forEach>
        </table>
</body>