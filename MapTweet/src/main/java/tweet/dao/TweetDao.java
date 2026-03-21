package tweet.dao;

import java.util.List;

import tweet.model.Tweet;

public interface TweetDao {
    void save(Tweet tweet);
    List<Tweet> findAll();
    void delete(int tweetId, String userId);
    void update(Tweet tweet);
    Tweet findById(int tweetId);
   }
