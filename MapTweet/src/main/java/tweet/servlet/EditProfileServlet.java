package tweet.servlet;

import java.io.IOException;

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
    // フォワード先
    String forwardPath = null;

    // サーブレットクラスの動作を決定する「action」の値を
    // リクエストパラメータから取得
    String action = request.getParameter("action");

    // 「登録の開始」をリクエストされたときの処理
    if (action == null) {
      // フォワード先を設定
      forwardPath = "WEB-INF/jsp/editProfile.jsp";
    }
    // 登録確認画面から「登録実行」をリクエストされたときの処理
    else if (action.equals("done")) {
      // セッションスコープに保存された登録ユーザ
      HttpSession session = request.getSession();
      User modUser = (User) session.getAttribute("modUser");

      // 登録処理の呼び出し
//    UserDAO dao = new UserDAO(getServletContext());
    JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);
      
      //      RegisterUserLogic logic = new RegisterUserLogic();
//      logic.execute(registerUser, getServletContext());
      //      logic.execute(registerUser);
      dao.update(modUser);
            
      // ログインユーザーの情報を更新し、不要となったセッションスコープ内のインスタンスを削除
      session.setAttribute("longinUser", modUser);
      session.removeAttribute("modUser");
      
      
      // 登録後のフォワード先を設定
      forwardPath = "/profile";
    }

    // 設定されたフォワード先にフォワード
    request.getRequestDispatcher(forwardPath).forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// 登録処理の呼び出し
	HttpSession session = request.getSession();
    User loginUser = (User)session.getAttribute("loginUser");
    String id = loginUser.getId();
    
    // リクエストパラメータの取得
    request.setCharacterEncoding("UTF-8");
    String crrPass = request.getParameter("crrPass");
    String newPass = request.getParameter("newPass");
    String repPass = request.getParameter("repPass");
    String newName = request.getParameter("newName");      
    String newProfile = request.getParameter("newProfile");    
    //未入力なら変更しない（同じ内容で再登録）
    if (newPass == "" && repPass == "") {
    	newPass = crrPass;
    	repPass = crrPass;
    }
    if (newName == "") {newName = loginUser.getName();}
    if (newProfile == "") {newProfile = loginUser.getProfile();}    

    // 登録するユーザーの情報を設定
    User modUser = new User(id, newPass, newName, newProfile);
    
    // パスワードチェック
    JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);

    int checkCode = 0;
    if(dao.login(id, crrPass) == null) {
        // 現パスワードが間違っている場合
    	checkCode +=1;
    }
    if(!newPass.isEmpty() && !newPass.equals(repPass)) {
    	checkCode +=2;
    }
    
    if(newPass.isEmpty() && crrPass.isEmpty()) {
    	checkCode +=4;
    }
	
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
}
