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
<title>ユーザー情報変更</title>
</head>
<body>
<p>下記のユーザーを登録します</p>
<p>
ログインID：${fn:escapeXml(loginUser.id)}<br>
名前：${fn:escapeXml(modUser.name)}<br>
自己紹介：${fn:escapeXml(modUser.profile)}<br>
※パスワードは表示されません。
</p></p>
<a href="EditProfile">戻る</a>
<a href="EditProfile?action=done">変更</a>
</body>
</html>