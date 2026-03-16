package tweet.servlet;

import java.io.IOException;
import java.util.List;

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

@WebServlet("/timeline")
public class TimelineServlet extends HttpServlet {

    private TweetDao dao;

    @Override
    public void init() {
//        String filePath = getServletContext().getRealPath("/WEB-INF/data/tweets.xml");
//      dao = new XmlTweetDao(filePath);
      dao = new JdbcTweetDao(DbConfig.URL);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse responce)
            throws ServletException, IOException {

    	// ★ 未ログインならログイン画面へ
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
        	responce.sendRedirect(request.getContextPath() + "/UserLogin");
            return;
        }
        
        List<Tweet> tweets = dao.findAll();

        request.setAttribute("tweets", tweets);
        request.getRequestDispatcher("timeline.jsp").forward(request, responce);
    }
}