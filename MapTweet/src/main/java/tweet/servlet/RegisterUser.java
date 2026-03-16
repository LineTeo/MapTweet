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
import tweet.dao.UserDAO;
import tweet.model.User;

@WebServlet("/RegisterUser")
public class RegisterUser extends HttpServlet {
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
      forwardPath = "WEB-INF/jsp/registerForm.jsp";
    }
    // 登録確認画面から「登録実行」をリクエストされたときの処理
    else if (action.equals("done")) {
      // セッションスコープに保存された登録ユーザ
      HttpSession session = request.getSession();
      User registerUser = (User) session.getAttribute("registerUser");

      // 登録処理の呼び出し
//    UserDAO dao = new UserDAO(getServletContext());
    JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);
      
      //      RegisterUserLogic logic = new RegisterUserLogic();
//      logic.execute(registerUser, getServletContext());
      //      logic.execute(registerUser);
      dao.insert(registerUser);
      
      
      
      
      // 不要となったセッションスコープ内のインスタンスを削除
      session.removeAttribute("registerUser");

      // 登録後のフォワード先を設定
      forwardPath = "WEB-INF/jsp/registerDone.jsp";
    }

    // 設定されたフォワード先にフォワード
    request.getRequestDispatcher(forwardPath).forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // リクエストパラメータの取得
    request.setCharacterEncoding("UTF-8");
    String id = request.getParameter("id");
    String name = request.getParameter("name");
    String pass = request.getParameter("pass");
    String profile = request.getParameter("profile");    

    // 登録するユーザーの情報を設定
    User registerUser = new User(id, pass, name, profile);
    // 登録処理の呼び出し
    UserDAO dao = new UserDAO(getServletContext());
    
    HttpSession session = request.getSession();
    session.setAttribute("registerUser", registerUser);
    
    if (dao.exists(id)) {
        // セッションスコープに登録ユーザーを保存
        session.setAttribute("errResponse", 1);
    	
        // フォワード
        request.getRequestDispatcher("WEB-INF/jsp/registerForm.jsp").forward(request, response);      	
    }else {
        // セッションスコープに登録ユーザーを保存
        session.setAttribute("registerUser", registerUser);
        session.setAttribute("errResponse", 0);

        // フォワード
        request.getRequestDispatcher("WEB-INF/jsp/registerConfirm.jsp").forward(request, response);
    }
  }
}