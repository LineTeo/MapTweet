<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<%-- a href="post.jsp">+ つぶやく</a>--%>
<%-- ログインユーザー情報をセッションから取得 --%>
<%
    tweet.model.User loginUser =
        (tweet.model.User) session.getAttribute("loginUser");
%>
<%-- ヘッダー部分を変更 --%>
<div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem;">
  <div>
    <a href="post">＋ つぶやく</a>
  </div>
  <div>
    <%-- ログインユーザー名の表示＋ログアウトリンク--%> 
    <span style="font-size:13px; color:#666; margin-right:1rem;">      
      	  <% if (loginUser != null) { %>
        <a href="profile?id=<%= loginUser.getId() %>" style="color:#1a8cff; text-decoration:none;">
      👤  ${fn:escapeXml(loginUser.getId())}
       </a>
       <% } %>
    </span>
    <a href="UserLogin?action=done"
       style="font-size:13px; color:#e55; text-decoration:none;"
       onclick="return confirm('ログアウトしますか？')">
      ログアウト
    </a>
  </div>
</div>

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
      pageContext.setAttribute("t", t); // EL式でtを呼ぶため、tをページスコープに格納

%>
  <div class="tweet-card">
    <%--p class="tweet-text"><%= t.getText() %></p--%>
    <p class="tweet-text">${fn:escapeXml(t.text)}</p>
    <p class="tweet-meta">
	  <!-- ★ 投稿者名をリンクに変更 -->
	  <% if (t.getUserId() != null && !t.getUserId().isEmpty()) { %>
        <a href="profile?id=<%= t.getUserId() %>" style="color:#1a8cff; text-decoration:none;">
      👤  ${fn:escapeXml(t.userId)}
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
	<%-- 削除ボタン--%>
		<% if (loginUser != null && 
		loginUser.getId().equals(t.getUserId())) { %>

		<form action="delete" method="post" style="display:inline;">
		<input type="hidden" name="tweetId" value="<%= t.getTweetId() %>">
		<input type="hidden" name="csrfToken" value="${csrfToken}">
		<button onclick="return confirm('削除しますか？')">削除</button>
		</form>

	<%-- 編集リンク--%>
		<a href="editTweet?id=<%= t.getTweetId() %>">編集</a>
	<% } %>
		
  </div>	
    <%}
  }
%>
</div>

<%-- 地図表示--%>
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