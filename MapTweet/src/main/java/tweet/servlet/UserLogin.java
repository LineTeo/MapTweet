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
        String forwardPath;

        if (action == null) {
            forwardPath = "WEB-INF/jsp/loginForm.jsp";
        } else if (action.equals("done")) {
            // ★ ログアウト処理：loginUser を削除
            HttpSession session = request.getSession();
            session.removeAttribute("loginUser");
            session.removeAttribute("errResponse");
            forwardPath = "WEB-INF/jsp/loginForm.jsp";
        } else {
            forwardPath = "WEB-INF/jsp/loginForm.jsp";
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPath);
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
        HttpSession session = request.getSession();

        if (loginUser != null) {
            // ★ ログイン成功 → main.jsp の代わりにつぶやきアプリの /post へ
            session.setAttribute("loginUser", loginUser);
            response.sendRedirect(request.getContextPath() + "/post");
        } else {
            // ログイン失敗 → ログインフォームに戻る
            session.setAttribute("errResponse", 1);
            request.getRequestDispatcher("WEB-INF/jsp/loginForm.jsp")
                   .forward(request, response);
        }
    }
}