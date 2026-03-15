<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="tweet.model.Tweet, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>タイムライン</title>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
  <style>
    body { font-family: sans-serif; max-width: 600px; margin: 2rem auto; padding: 0 1rem; }
    .tweet-card {
      border: 1px solid #ddd; border-radius: 8px;
      padding: 1rem; margin-bottom: 1rem;
    }
    .tweet-text { font-size: 16px; margin: 0 0 6px; }
    .tweet-meta { font-size: 12px; color: #888; margin-bottom: 8px; }
    .mini-map   { height: 150px; border-radius: 6px; border: 1px solid #ddd; }
  </style>
</head>
<body>

<h2>タイムライン</h2>
<a href="post.jsp">+ つぶやく</a>

<div style="margin-top:1rem;">
<%
  List<Tweet> tweets = (List<Tweet>) request.getAttribute("tweets");
  if (tweets == null || tweets.isEmpty()) {
%>
  <p>まだつぶやきはありません。</p>
<%
  } else {
    for (int i = 0; i < tweets.size(); i++) {
      Tweet t = tweets.get(i);
%>
  <div class="tweet-card">
    <p class="tweet-text"><%= t.getText() %></p>
    <p class="tweet-meta">
	  <!-- ★ 投稿者名をリンクに変更 -->
	  <% if (t.getUserId() != null && !t.getUserId().isEmpty()) { %>
        <a href="profile?id=<%= t.getUserId() %>" style="color:#1a8cff; text-decoration:none;">
      👤  <%= t.getUserId() %>
       </a>
       ／
      <% } %>    
      <%= t.getPostedAt() %>
      <% if (t.getLatitude() != 0.0 || t.getLongitude() != 0.0) { %>
        ／ 📍 <%= String.format("%.4f", t.getLatitude()) %>,
           <%= String.format("%.4f", t.getLongitude()) %>
      <% } %>
    </p>
    <% if (t.getLatitude() != 0.0 || t.getLongitude() != 0.0) { %>
      <div id="map-<%= i %>" class="mini-map"></div>
    <% } %>
  </div>
<%
    }
  }
%>
</div>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
<%
  if (tweets != null) {
    for (int i = 0; i < tweets.size(); i++) {
      Tweet t = tweets.get(i);
      if (t.getLatitude() != 0.0 || t.getLongitude() != 0.0) {
%>
  (function() {
    const m = L.map('map-<%= i %>', {
      zoomControl: false, dragging: false,
      scrollWheelZoom: false, doubleClickZoom: false
    }).setView([<%= t.getLatitude() %>, <%= t.getLongitude() %>], 14);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(m);
    L.marker([<%= t.getLatitude() %>, <%= t.getLongitude() %>]).addTo(m);
  })();
<%
      }
    }
  }
%>
</script>

</body>
</html>