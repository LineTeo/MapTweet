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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 未ログインならログイン画面へ
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/UserLogin");
            return;
        }

        String targetId = req.getParameter("id");
        if (targetId == null || targetId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/timeline");
            return;
        }

//      UserDAO dao = new UserDAO(getServletContext());
      JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);

      User targetUser = dao.findById(targetId); // ← UserDAO に追加するメソッド

        req.setAttribute("targetUser", targetUser);
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }
}