package tweet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import tweet.model.User;

public class JdbcUserDAO {

    private final String url;

    public JdbcUserDAO(String url) {
        this.url = url;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    // 全ユーザー取得
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> list = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new User(
                    rs.getString("id"),
                    rs.getString("pass"),
                    rs.getString("name"),
                    rs.getString("profile")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("ユーザー取得失敗", e);
        }
        return list;
    }

    // IDで1件取得
    public User findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("id"),
                        rs.getString("pass"),
                        rs.getString("name"),
                        rs.getString("profile")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("ユーザー取得失敗", e);
        }
        return null;
    }

    // ログイン認証
    public User login(String id, String pass) {
        String sql = "SELECT * FROM users WHERE id = ?";		// hash化した為、sqlでパスワード比較削除→AND pass = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("id"),
                        rs.getString("pass"),
                        rs.getString("name"),
                        rs.getString("profile")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("ログイン失敗", e);
        }
        return null;
    }

    // ID存在チェック
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("ID存在チェック失敗", e);
        }
        return false;
    }

    // ユーザー登録
    public boolean insert(User user) {
        if (exists(user.getId())) {
            return false;
        }

        // ★ パスワードをハッシュ化してからDBに保存
        String hashedPass = BCrypt.hashpw(user.getPass(), BCrypt.gensalt());        
        
        String sql = "INSERT INTO users (id, pass, name, profile) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getId());
//            ps.setString(2, user.getPass());     	//プレーンテキストは危険
            ps.setString(2, hashedPass);       		//ハッシュ化して保存
            ps.setString(3, user.getName());
            ps.setString(4, user.getProfile());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("ユーザー登録失敗", e);
        }
    }
}