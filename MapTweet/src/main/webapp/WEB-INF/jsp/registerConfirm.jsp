<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8" %>
<%@ page import="tweet.model.User" %>
<%
// User registerUser = (User) session.getAttribute("registerUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー登録</title>
</head>
<body>
<p>下記のユーザーを登録します</p>
<p>
ログインID：${fn:escapeXml(registerUser.id)}<br>
名前：${fn:escapeXml(registerUser.name)}<br>
自己紹介：${fn:escapeXml(registerUser.profile)}<br>
</p></p>
<form action="EditProfile" method="post"> 
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="action" value="update">
<button type="submit">登録</button>

</form>
<a href="RegisterUser">戻る</a>
</body>
</html>