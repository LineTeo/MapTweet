package tweet.servlet;

import java.io.IOException;
import java.util.UUID; // トークン生成に使用

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import tweet.dao.DbConfig;
import tweet.dao.JdbcUserDAO;
import tweet.model.User;



@WebServlet("/EditProfile")
public class EditProfileServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	// ログインチェック（未ログインならログイン画面へ）
      HttpSession session = request.getSession();
      if (session.getAttribute("loginUser") == null) {
          response.sendRedirect(request.getContextPath() + "/UserLogin");
          return;
      }

   // --- CSRF対策: トークンの生成と保存 ---
      String csrfToken = UUID.randomUUID().toString();
      session.setAttribute("csrfToken", csrfToken);
      
      
      // 編集画面を表示
      request.getRequestDispatcher("WEB-INF/jsp/editProfile.jsp").forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// 登録処理の呼び出し
	HttpSession session = request.getSession();
    User loginUser = (User)session.getAttribute("loginUser");
 // ボタンのname属性などで処理を分岐させる
    String action = request.getParameter("action");

    if ("confirm".equals(action)) {
        // --- 1. 確認画面への遷移処理 ---
        handleConfirm(request, response, session, loginUser);
        
    } else if ("update".equals(action)) {
        // --- 2. 実際のDB更新処理 ---
    	// --- CSRF対策: トークンの検証 ---
        String requestToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (requestToken == null || !requestToken.equals(sessionToken)) {
            // トークンが不正な場合は403エラーを返す
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです。");
            return;
        }
        
        // 検証成功なら更新処理へ
        handleUpdate(request, response, session);
    }    
  }
    /**
     * 入力内容をチェックし、確認画面へフォワードする
     */
  private void handleConfirm(HttpServletRequest request, HttpServletResponse response, 
                              HttpSession session, User loginUser) throws ServletException, IOException {   

	String id = loginUser.getId();

	  // リクエストパラメータの取得
    request.setCharacterEncoding("UTF-8");
    String crrPass = request.getParameter("crrPass");
    String newPass = request.getParameter("newPass");
    String repPass = request.getParameter("repPass");
    String newName = request.getParameter("newName");      
    String newProfile = request.getParameter("newProfile");    
    //未入力なら変更しない（同じ内容で再登録）
    if (newPass.isEmpty() && repPass.isEmpty()) {
    	newPass = crrPass;
    	repPass = crrPass;
    }
    if (newName.isEmpty()) {newName = loginUser.getName();}
    if (newProfile.isEmpty()) {newProfile = loginUser.getProfile();}    

    // 登録するユーザーの情報を設定
    User modUser = new User(id, newPass, newName, newProfile);
    
    // パスワードチェック
    JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);

    int checkCode = 0;
    if(dao.login(id, crrPass) == null) {checkCode +=1;}  					// 現パスワードが間違っている場合
    if(!newPass.isEmpty() && !newPass.equals(repPass)) {checkCode +=2;}		// 確認パスワードが間違っている場合
    if(newPass.isEmpty() && crrPass.isEmpty()) {checkCode +=4;}				// 現パスワードが""で、新パスワードも""の場合
	
    session.setAttribute("modUser", modUser);

	if (checkCode != 0) {
    	session.setAttribute("errResponse", checkCode);
        // フォワード
    	request.getRequestDispatcher("WEB-INF/jsp/editProfile.jsp").forward(request, response);      	
    }else {
    	// セッションスコープに登録ユーザーを保存
    	session.setAttribute("errResponse", 0);
    	// フォワード
    	request.getRequestDispatcher("WEB-INF/jsp/editConfirm.jsp").forward(request, response);
    
    }
  }
  /**
   * DBを更新し、プロフィール画面へリダイレクトする
   */
  private void handleUpdate(HttpServletRequest request, HttpServletResponse response, 
                           HttpSession session) throws IOException {
      
      // ここでCSRFトークンのチェックを入れると完璧です
      
      User modUser = (User) session.getAttribute("modUser");
      if (modUser != null) {
          JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);
          dao.update(modUser);
          
          // セッション情報を最新に更新
          session.setAttribute("loginUser", modUser);
          session.removeAttribute("modUser");
       // 使用済みトークンを破棄
          session.removeAttribute("csrfToken");
      }
      
      // 更新後は「リダイレクト」で二重投稿を防止
      response.sendRedirect(request.getContextPath() + "/profile");
  }
}
