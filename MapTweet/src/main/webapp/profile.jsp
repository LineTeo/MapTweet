<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="tweet.model.User" %>
<%
    User targetUser = (User) request.getAttribute("targetUser");

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
    .profile-name { font-size: 22px; font-weight: bold; margin-bottom: 0.3rem; }
    .profile-id   { font-size: 13px; color: #888; margin-bottom: 1rem; }
    .profile-bio  { font-size: 15px; line-height: 1.6; white-space: pre-wrap; }
    .back-link    { display: inline-block; margin-top: 1.5rem; color: #555; }
  </style>
</head>
<body>

<h2>プロフィール</h2>

<% if (targetUser == null) { %>
  <p>ユーザーが見つかりませんでした。</p>
<% } else { %>
  <div class="profile-card">
    <p class="profile-name">${fn:escapeXml(targetUser.name)}</p>
    <p class="profile-id">@${fn:escapeXml(targetUser.id)}</p>
    <p class="profile-bio">${fn:escapeXml(targetUser.profile)}</p>
  </div>
<% } %>

<a class="back-link" href="timeline">← タイムラインへ戻る</a>

</body>
</html>