package tweet.dao;

public class DbConfig {
    // H2の埋め込みモード（ファイルで永続化）
    public static final String URL =
//        "jdbc:h2:~/tweetapp;AUTO_SERVER=TRUE";
//    	"jdbc:h2:tcp://localhost/~/tweetapp;AUTO_SERVER=TRUE";
    		 "jdbc:h2:tcp://localhost/~/example;USER=sa;PASSWORD=";

    // ※ Eclipseで確認したURLに合わせて変更してください
}