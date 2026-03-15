<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8" %>
<%@ page import="tweet.model.User" %>
<%
User registerUser = (User) session.getAttribute("loginUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー情報</title>
</head>
<body>
<p><%= registerUser.getName() %>さんの登録情報</p>
<p>
ログインID：<%= registerUser.getId() %><br>
パスワード：************<br>
自己紹介文：<%= registerUser.getProfile() %><br>
</p></p>
<a href="UserLogin?action=done">ログアウト</a>
</body>
</html>