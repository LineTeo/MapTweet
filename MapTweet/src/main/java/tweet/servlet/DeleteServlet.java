package tweet.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import tweet.dao.DbConfig;
import tweet.dao.JdbcTweetDao;
import tweet.dao.TweetDao;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {

    private TweetDao dao;

    @Override
    public void init() {
        dao = new JdbcTweetDao(DbConfig.URL);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("UserLogin");
            return;
        }

        // --- CSRF対策: トークンの検証 ---
        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = req.getParameter("csrfToken");

        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }       
        
        
        tweet.model.User user =
            (tweet.model.User) session.getAttribute("loginUser");

        int tweetId = Integer.parseInt(req.getParameter("tweetId"));

        dao.delete(tweetId, user.getId());
        
        // 削除成功後はトークンを破棄（使い捨てにする）
        session.removeAttribute("csrfToken");

        resp.sendRedirect("timeline");
    }
}
