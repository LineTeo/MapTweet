package dao;

import model.Tweet;
import java.util.List;

public interface TweetDao {
    void save(Tweet tweet);
    List<Tweet> findAll();
}