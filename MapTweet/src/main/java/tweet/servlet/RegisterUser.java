package tweet.servlet;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import tweet.dao.DbConfig;
import tweet.dao.JdbcUserDAO;
import tweet.model.User;

@WebServlet("/RegisterUser")
public class RegisterUser extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // --- CSRF対策: トークンの生成 ---
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);
        
        session.setAttribute("errResponse", 2);
        request.getRequestDispatcher("WEB-INF/jsp/registerForm.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        // --- 1. CSRFトークンの検証 ---
        String requestToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (requestToken == null || !requestToken.equals(sessionToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです。");
            return;
        }

        if ("confirm".equals(action)) {
            // --- 2. 登録確認処理 ---
            handleConfirm(request, response, session);
        } else if ("execute".equals(action)) {
            // --- 3. 実際の登録（DB挿入）処理 ---
            handleInsert(request, response, session);
        }
    }

    private void handleConfirm(HttpServletRequest request, HttpServletResponse response, 
                              HttpSession session) throws ServletException, IOException {
        
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String pass = request.getParameter("pass");
        String profile = request.getParameter("profile");

        User registerUser = new User(id, pass, name, profile);
        JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);
        session.setAttribute("registerUser", registerUser);

        if (dao.exists(id)) {
            session.setAttribute("errResponse", 1);
            request.getRequestDispatcher("WEB-INF/jsp/registerForm.jsp").forward(request, response);
        } else if (id.isEmpty() || pass.isEmpty()) {
            session.setAttribute("errResponse", 3);
            request.getRequestDispatcher("WEB-INF/jsp/registerForm.jsp").forward(request, response);
        } else {
            session.setAttribute("errResponse", 0);
            request.getRequestDispatcher("WEB-INF/jsp/registerConfirm.jsp").forward(request, response);
        }
    }

    private void handleInsert(HttpServletRequest request, HttpServletResponse response, 
                             HttpSession session) throws ServletException, IOException {
        
        User registerUser = (User) session.getAttribute("registerUser");
        if (registerUser != null) {
            JdbcUserDAO dao = new JdbcUserDAO(DbConfig.URL);
            dao.insert(registerUser);
            
            // 不要になったデータを削除
            session.removeAttribute("registerUser");
            session.removeAttribute("csrfToken");
        }
        
        // 登録完了ページへフォワード（またはリダイレクト）
        request.getRequestDispatcher("WEB-INF/jsp/registerDone.jsp").forward(request, response);
    }
}
