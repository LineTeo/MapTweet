package tweet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import tweet.model.Tweet;

public class JdbcTweetDao implements TweetDao {

    private final String url;

    public JdbcTweetDao(String url) {
        this.url = url;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public void save(Tweet tweet) {
        String sql = "INSERT INTO tweets (text, latitude, longitude, posted_at, user_id) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tweet.getText());
            ps.setDouble(2, tweet.getLatitude());
            ps.setDouble(3, tweet.getLongitude());
            ps.setTimestamp(4, Timestamp.valueOf(tweet.getPostedAt()));
            ps.setString(5, tweet.getUserId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("ツイート保存失敗", e);
        }
    }

    //全件取得メソッド（画面表示用）
    @Override
    public List<Tweet> findAll() {
        String sql = "SELECT * FROM tweets ORDER BY posted_at DESC";
        List<Tweet> list = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tweet t = new Tweet();
                t.setText(rs.getString("text"));
                t.setLatitude(rs.getDouble("latitude"));
                t.setLongitude(rs.getDouble("longitude"));
                t.setPostedAt(rs.getTimestamp("posted_at").toLocalDateTime());
                t.setUserId(rs.getString("user_id"));
                t.setTweetId(rs.getInt("tweet_id")); //ツイート削除機能のために追加
                list.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException("ツイート取得失敗", e);
        }
        return list;
    }
    
    //1件取得メソッド（編集画面用）
    @Override
    public Tweet findById(int tweetId) {
        String sql = "SELECT * FROM tweets WHERE tweet_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tweetId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tweet t = new Tweet();
                    t.setTweetId(rs.getInt("tweet_id"));
                    t.setText(rs.getString("text"));
                    t.setLatitude(rs.getDouble("latitude"));
                    t.setLongitude(rs.getDouble("longitude"));
                    t.setPostedAt(rs.getTimestamp("posted_at").toLocalDateTime());
                    t.setUserId(rs.getString("user_id"));
                    return t;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("取得失敗", e);
        }
        return null;
    }
    
    //ツイート削除
    @Override
    public void delete(int tweetId, String userId) {
        String sql = "DELETE FROM tweets WHERE tweet_id = ? AND user_id = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tweetId);
            ps.setString(2, userId); // ★ 自分の投稿だけ削除できる
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("削除失敗", e);
        }
    }

    //ツイート編集
    @Override
    public void update(Tweet tweet) {
        String sql = "UPDATE tweets SET text = ?, latitude = ?, longitude = ? " +
                     "WHERE tweet_id = ? AND user_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tweet.getText());
            ps.setDouble(2, tweet.getLatitude());
            ps.setDouble(3, tweet.getLongitude());
            ps.setInt(4, tweet.getTweetId());
            ps.setString(5, tweet.getUserId()); // ★ 自分の投稿だけ更新

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("更新失敗", e);
        }
    }
}


