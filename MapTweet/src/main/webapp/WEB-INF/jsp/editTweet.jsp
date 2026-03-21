<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="tweet.model.Tweet" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>つぶやく</title>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
  <style>
    body { font-family: sans-serif; max-width: 600px; margin: 2rem auto; padding: 0 1rem; }
    #map { height: 300px; margin: 0.5rem 0 1rem; border: 1px solid #ccc; border-radius: 8px; }
    textarea { width: 100%; box-sizing: border-box; padding: 8px; font-size: 15px; }
    button { margin-top: 0.75rem; padding: 8px 24px; font-size: 15px; cursor: pointer; }
    #location-label { font-size: 13px; color: #666; margin-bottom: 0.5rem; }
  </style>
</head>
<body>

<%
    Tweet t = (Tweet) request.getAttribute("tweet");
%>
<h2>つぶやき編集</h2>

<form action="editTweet" method="post">
  <input type="hidden" name="tweetId" value="<%= t.getTweetId() %>">

  <textarea name="text" rows="3"><%= t.getText() %></textarea>

  <input type="hidden" name="latitude" value="<%= t.getLatitude() %>">
  <input type="hidden" name="longitude" value="<%= t.getLongitude() %>">

<input type="hidden" name="csrfToken" value="${csrfToken}">
  <button type="submit">更新</button>
</form>

<a href="timeline">戻る</a>

</body>
</html>