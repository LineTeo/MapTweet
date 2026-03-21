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
import tweet.model.Tweet;

@WebServlet("/editTweet")
public class EditTweetServlet extends HttpServlet {

    private TweetDao dao;

    @Override
    public void init() {
        dao = new JdbcTweetDao(DbConfig.URL);
    }

    // 編集画面表示
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int tweetId = Integer.parseInt(req.getParameter("id"));

        Tweet tweet = dao.findById(tweetId);

        req.setAttribute("tweet", tweet);
        req.getRequestDispatcher("/WEB-INF/jsp/editTweet.jsp")
           .forward(req, resp);
    }

    // 更新処理
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int tweetId = Integer.parseInt(req.getParameter("tweetId"));
        String text = req.getParameter("text");
        double lat = Double.parseDouble(req.getParameter("latitude"));
        double lng = Double.parseDouble(req.getParameter("longitude"));

        HttpSession session = req.getSession(false);
        
        
        // --- CSRF対策: トークンの検証 ---
        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = req.getParameter("csrfToken");
        if (sessionToken == null || !sessionToken.equals(requestToken)) {
            System.out.println("トークン拒否！");
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        tweet.model.User user =
            (tweet.model.User) session.getAttribute("loginUser");

        Tweet t = new Tweet();
        t.setTweetId(tweetId);
        t.setText(text);
        t.setLatitude(lat);
        t.setLongitude(lng);
        t.setUserId(user.getId());

        dao.update(t);

        // 削除成功後はトークンを破棄（使い捨てにする）
        session.removeAttribute("csrfToken");

        resp.sendRedirect("timeline");
    }
}