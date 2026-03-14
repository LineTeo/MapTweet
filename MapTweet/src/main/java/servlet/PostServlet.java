package servlet;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.TweetDao;
import dao.XmlTweetDao;
import model.Tweet;

@WebServlet("/post")
public class PostServlet extends HttpServlet {

    private TweetDao dao;

    @Override
    public void init() {
        String filePath = getServletContext()
                .getRealPath("/WEB-INF/data/tweets.xml");
        dao = new XmlTweetDao(filePath);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	request.setCharacterEncoding("UTF-8");

        String text = request.getParameter("text");
        String latStr = request.getParameter("latitude");
        String lngStr = request.getParameter("longitude");

        // 簡易バリデーション
        if (text == null || text.trim().isEmpty()) {
        	response.sendRedirect("post.jsp?error=empty");
            return;
        }

        double lat = 0.0;
        double lng = 0.0;
        if (latStr != null && !latStr.isEmpty()) {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        }

        Tweet tweet = new Tweet(text.trim(), lat, lng, LocalDateTime.now());
        dao.save(tweet);

        response.sendRedirect("timeline");
    }
}