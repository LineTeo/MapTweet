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
                list.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException("ツイート取得失敗", e);
        }
        return list;
    }
}