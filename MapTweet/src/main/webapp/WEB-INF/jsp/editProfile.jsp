<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8" %>
<%@ page import="tweet.model.User" %>

<% User loginUser = (User) session.getAttribute("loginUser"); %>
<% User modUser = (User) session.getAttribute("modUser"); %>

<%
    // sessionから取得（この時点では Object 型）
    Object errObj = session.getAttribute("errResponse");
    
    // nullチェックをしてから int に変換する
    int errType = 0; 
    if (errObj != null) {
        errType = (int) errObj;
    }
 	String name = loginUser.getName();
	String profile = loginUser.getProfile();
if (modUser != null) {
	switch(errType){
		case 0:
			//break;
		default:			
			// スコープから以前入力した文字の取得
			name = modUser.getName();
		 	profile = modUser.getProfile();
        }
    } %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>登録情報修正</title>
<style>
  /* 各入力行のスタイル */
  .form-item {
    display: flex;          /* 横並びにする */
    align-items: center;    /* 縦方向の中央揃え */
    margin-bottom: 10px;    /* 行間の余白 */
  }

  /* ラベルのスタイル */
  .form-item label {
    width: 170px;           /* ラベルの幅を固定して左端を揃える */
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
<H1> 登録情報修正　</H1>
<H2><%= loginUser.getId() %>さん</H2>

<form action="EditProfile" method="post">
	※変更項目がある場合は必ず入力してください。
  <div class="form-item">
    <label for="pass">現パスワード：</label>
    <input type="password" id="passCrr" name="crrPass">
  </div>
	以下、変更項目のみ記入してください。（空欄項目は変更されません）
  <div class="form-item">
    <label for="pass">新パスワード：</label>
    <input type="password" id="passNew" name="newPass">
  </div>
  <div class="form-item">
    <label for="pass">新パスワード(確認用):</label>
    <input type="password" id="passNewRep" name="repPass">
  </div>

  <div class="form-item">
    <label for="name">名前：</label>
    <input type="text" id="name" name="newName" value="<%= name %>">
  </div>

  <div class="form-item align-top">
    <label for="intro">自己紹介：</label>
    <textarea id="intro" name="newProfile" class="large-field"><%= profile %></textarea>
  </div>
  <div class="form-item">
    <button type="submit" name="action" value="confirm">確認する</button>
    <!--label></label> <input type="submit" value="変更"-->
  </div>
	<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
    <input type="hidden" name="action" value="confirm">
</form>
 <a href="profile?id=<%= loginUser.getId() %>" style="color:#1a8cff; text-decoration:none;">戻る</a>
<hr>

    <% if (modUser != null) {
    	// 1, 2, 4, 8... と定義（これらは 2進数で 1, 10, 100, 1000...）
        int ERR_INVALID_PASS = 1 << 0; // 1 (0001) 現パスワード間違い
        int ERR_DEFF_PASS = 1 << 1; // 2 (0010)	　確認用パスワードが合ってない
        int ERR_NO_PASS   = 1 << 2; // 4 (0100)   パスワードが設定されてない
//      int ERR_hoge2   = 1 << 3; // 8 (1000)		予備
		if ((errType & ERR_INVALID_PASS) != 0) {%>
	            <p>現パスワードが不正です</p>
	    <%}
		if ((errType & ERR_DEFF_PASS) != 0) {%>
	            <p>確認用新パスワードがあっていません</p>
        <%}
		if ((errType & ERR_NO_PASS) != 0) {%>
	        	<p>パスワードを設定してください</p>
		<%}
	}%>

</body>
</html>