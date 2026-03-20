<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8" %>
<%@ page import="tweet.model.User" %>

<% User registerUser = (User) session.getAttribute("registerUser"); %>
<%
    // sessionから取得（この時点では Object 型）
    Object errObj = session.getAttribute("errResponse");
    
    // nullチェックをしてから int に変換する
    int errType = 0; 
    if (errObj != null) {
        errType = (int) errObj;
    }
%>

<% 	String id = "";
	String name = "";
	String profile = "";
if (registerUser != null) {
    switch(errType){
	    case 1:
	    case 2:
		case 3: 
	        // スコープから以前入力した文字の取得
	        id = registerUser.getId();
	        name = registerUser.getName();
	        profile = registerUser.getProfile();
			break;
		 default:			
        }
    } %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー登録</title>
<style>
  /* 各入力行のスタイル */
  .form-item {
    display: flex;          /* 横並びにする */
    align-items: center;    /* 縦方向の中央揃え */
    margin-bottom: 10px;    /* 行間の余白 */
  }

  /* ラベルのスタイル */
  .form-item label {
    width: 100px;           /* ラベルの幅を固定して左端を揃える */
    flex-shrink: 0;         /* 幅を縮ませない */
  }

  /* 自己紹介など、複数行入力のスタイル調整 */
  .form-item.align-top {
    align-items: flex-start; /* ラベルを上側に配置 */
  }

  /* 自己紹介欄のサイズ指定 */
  .large-field {
    width: 300px;           /* 横幅 */
    height: 100px;          /* 高さ */
    resize: vertical;       /* ユーザーが縦方向にのみサイズ変更可能にする */
  }
</style>

</head>
<body>
<H1> ユーザー登録　</H1>
<form action="RegisterUser" method="post">
  <div class="form-item">
    <label for="id">ログインID：</label>
    <input type="text" id="id" name="id" value="<%= id %>">
  </div>

  <div class="form-item">
    <label for="pass">パスワード：</label>
    <input type="password" id="pass" name="pass">
  </div>

  <div class="form-item">
    <label for="name">名前：</label>
    <input type="text" id="name" name="name" value="<%= name %>">
  </div>

  <div class="form-item align-top">
    <label for="intro">自己紹介：</label>
    <textarea id="intro" name="profile" class="large-field"><%= profile %></textarea>
  </div>

  <div class="form-item">
    <button type="submit" name="action" value="confirm">確認する</button>
  </div>
  <input type="hidden" name="action" value="confirm">
　<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"> 
</form>
<a href="UserLogin">ログイン画面へ</a>

<hr>

    <% if (registerUser != null) {
        switch(errType){
		    case 1: %>
	            <p>登録済みのIDです</p>
      			<% session.removeAttribute("registerUser");
      			session.removeAttribute("errType");
				break;
		    case 2: 
      			session.removeAttribute("registerUser");
      			session.removeAttribute("errType");
				break;
		    case 3: %>
	            <p>IDまたはパスワードが入力されていません</p>
      			<% session.removeAttribute("registerUser");
      			session.removeAttribute("errType");
				break;
		    default:			
        }
    } %>

</body>
</html>