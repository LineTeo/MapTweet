package servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.TweetDao;
import dao.XmlTweetDao;
import model.Tweet;

@WebServlet("/timeline")
public class TimelineServlet extends HttpServlet {

    private TweetDao dao;

    @Override
    public void init() {
        String filePath = getServletContext()
                .getRealPath("/WEB-INF/data/tweets.xml");
        dao = new XmlTweetDao(filePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse responce)
            throws ServletException, IOException {

        List<Tweet> tweets = dao.findAll();

        request.setAttribute("tweets", tweets);
        request.getRequestDispatcher("timeline.jsp")
                .forward(request, responce);
    }
}