package tweet.servlet; // ← 変更

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import tweet.dao.DbConfig;
import tweet.dao.JdbcTweetDao;
import tweet.dao.TweetDao;    // ← 変更
import tweet.model.Tweet;     // ← 変更

@WebServlet("/post")
public class PostServlet extends HttpServlet {

    private TweetDao dao;

    @Override
    public void init() {
        dao = new JdbcTweetDao(DbConfig.URL);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loginUser") != null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/UserLogin");
            return;
        }

        // --- CSRF対策: トークンの生成と保存 ---
        HttpSession session = req.getSession();
        String csrfToken = java.util.UUID.randomUUID().toString();
        session.setAttribute("csrfToken", csrfToken);

        req.getRequestDispatcher("/post.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/UserLogin");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);

        // --- CSRF対策: トークンの検証 ---
        String requestToken = req.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (requestToken == null || !requestToken.equals(sessionToken)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "不正なリクエストです。");
            return;
        }

        String text = req.getParameter("text");
        String latStr = req.getParameter("latitude");
        String lngStr = req.getParameter("longitude");

        if (text == null || text.trim().isEmpty()) {
            // エラー時もリダイレクトではなく、一度メッセージを持って投稿ページへ
            resp.sendRedirect("post?error=empty"); 
            return;
        }

	    // ★ セッションからログインユーザーのIDを取得
        tweet.model.User loginUser = (tweet.model.User) session.getAttribute("loginUser");
        String userId = loginUser != null ? loginUser.getId() : "";


        // ...（緯度・経度の変換処理はそのまま）...

        double lat = 0.0, lng = 0.0;
        if (latStr != null && !latStr.isEmpty()) {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        }


        dao.save(new Tweet(text.trim(), lat, lng, LocalDateTime.now(), userId));
        
        // 投稿成功後はトークンを破棄（使い捨てにする場合）
        session.removeAttribute("csrfToken");
        
        resp.sendRedirect("timeline");
    }
}

/*
@WebServlet("/post")
public class PostServlet extends HttpServlet {

    private TweetDao dao;

    @Override
    public void init() {
//        String filePath = getServletContext().getRealPath("/WEB-INF/data/tweets.xml");
//        dao = new XmlTweetDao(filePath);
        dao = new JdbcTweetDao(DbConfig.URL);
    }

    // ★ セッションチェック共通メソッド
    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loginUser") != null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ★ 未ログインならログイン画面へ
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/UserLogin");
            return;
        }
        req.getRequestDispatcher("/post.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // ★ 未ログインならログイン画面へ
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/UserLogin");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String text = req.getParameter("text");
        String latStr = req.getParameter("latitude");
        String lngStr = req.getParameter("longitude");

        if (text == null || text.trim().isEmpty()) {
            resp.sendRedirect("post.jsp?error=empty");
            return;
        }

        double lat = 0.0, lng = 0.0;
        if (latStr != null && !latStr.isEmpty()) {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        }

		    // ★ セッションからログインユーザーのIDを取得
		    HttpSession session = req.getSession(false);
		    tweet.model.User loginUser =
		        (tweet.model.User) session.getAttribute("loginUser");
		    String userId = loginUser != null ? loginUser.getId() : "";

        dao.save(new Tweet(text.trim(), lat, lng, LocalDateTime.now(), userId));
        resp.sendRedirect("timeline");
    }
}*/