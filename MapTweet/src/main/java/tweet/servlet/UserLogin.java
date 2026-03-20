package tweet.servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import tweet.dao.DbConfig;
import tweet.dao.JdbcUserDAO;
import tweet.model.User;

@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
//        String forwardPath;
        // ログアウト処理：action=done の場合
        if ("done".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // セッションを完全に破棄して無効化
            }
            // ログアウト後は再ログインを促すためリダイレクト（二重送信防止）
            response.sendRedirect(request.getContextPath() + "/UserLogin");
            return;
        }

        // 通常の表示（actionがnullまたはそれ以外）
        RequestDispatcher dispatcher =request.getRequestDispatcher("WEB-INF/jsp/loginForm.jsp");
        dispatcher.forward(request, response);
  
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String pass = request.getParameter("pass");

//        UserDAO dao = new UserDAO(getServletContext());
        JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);

        User loginUser = dao.login(id, pass);

        if (loginUser != null) {
            // ★ ログイン成功 → main.jsp の代わりにつぶやきアプリの /post へ
            // ★【セッション固定攻撃対策】★
            // ログイン成功直後にセッションIDを新しく作り直す
            request.changeSessionId();

            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginUser);
            // 認証成功後はリダイレクト
            response.sendRedirect(request.getContextPath() + "/post");
        } else {
            // ログイン失敗：エラーフラグを立ててログイン画面へ戻る
            HttpSession session = request.getSession();
            // ログイン失敗 → ログインフォームに戻る
            session.setAttribute("errResponse", 1);
            request.getRequestDispatcher("WEB-INF/jsp/loginForm.jsp").forward(request, response);
        }
    }
}
        


            
