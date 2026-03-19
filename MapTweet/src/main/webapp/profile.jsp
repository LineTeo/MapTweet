<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="tweet.model.User" %>
<%
    User targetUser = (User) request.getAttribute("targetUser");
    User loginUser = (User) session.getAttribute("loginUser");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>プロフィール</title>
  <style>
    body { font-family: sans-serif; max-width: 500px; margin: 2rem auto; padding: 0 1rem; }
    .profile-card {
      border: 1px solid #ddd;
      border-radius: 12px;
      padding: 2rem;
      background: #fafafa;
    }
    .item   { font-size: 16px; color: #883; margin-bottom: 0.1rem; }
    .profile-name { font-size: 20px; font-weight: bold; margin-bottom: 2rem; }
    .profile-bio  { font-size: 16px; line-height: 1.6; white-space: pre-wrap; }
    .back-link    { display: inline-block; margin-top: 1.5rem; color: #555; }
  </style>
</head>
<body>

<h2>${fn:escapeXml(targetUser.id)} さんのプロフィール</h2>

<% if (targetUser == null) { %>
  <p>ユーザーが見つかりませんでした。</p>
<% } else { %>
  <div class="profile-card">
    <p class="item">名前</p>
    <p class="profile-name">${fn:escapeXml(targetUser.name)}</p>
    <p class="item">プロフィール</p>
    <p class="profile-bio">${fn:escapeXml(targetUser.profile)}</p>
  </div>
<% } %>

<a class="back-link" href="timeline">← タイムラインへ戻る</a>

<% if (targetUser.getId() == loginUser.getId()){ %>
<a class="back-link" href="EditProfile">プロファイル編集→</a>
<% } %>
</body>
</html>