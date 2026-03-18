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
<a href="RegisterUser">戻る</a>
<a href="RegisterUser?action=done">登録</a>
</body>
</html>