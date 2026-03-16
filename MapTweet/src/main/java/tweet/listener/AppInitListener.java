package tweet.listener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitListener implements ServletContextListener {
	
	  // データベース接続に使用する情報
	  private final String JDBC_URL = "jdbc:h2:tcp://localhost/~/example";
	  private final String DB_USER = "sa";
	  private final String DB_PASS = "";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection con = DriverManager.getConnection(JDBC_URL,DB_USER,DB_PASS);
             Statement st = con.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id      VARCHAR(50)  PRIMARY KEY,
                    pass    VARCHAR(100) NOT NULL,
                    name    VARCHAR(100) NOT NULL,
                    profile VARCHAR(500)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS tweets (
                    tweet_id  INT          PRIMARY KEY AUTO_INCREMENT,
                    text      VARCHAR(500) NOT NULL,
                    latitude  DOUBLE       NOT NULL DEFAULT 0.0,
                    longitude DOUBLE       NOT NULL DEFAULT 0.0,
                    posted_at TIMESTAMP    NOT NULL,
                    user_id   VARCHAR(50),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            System.out.println("DB初期化完了");

        } catch (Exception e) {
            throw new RuntimeException("DB初期化失敗", e);
        }
    }
}