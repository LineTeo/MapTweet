package tweet.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletContext;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import tweet.model.User;


public class UserDAO {
/********************************************
 * 　ユーザー情報を記録したCSVファイルに明日セスするためのクラス
 *
 *　OpenCSVを使って入出力をすることでCSVファイルを扱う際に改行やカンマなどの問題を回避
 *　するといった対策をここに集中することで管理を楽にする
 *　将来的にデータベースへのアクセスをする場合もこのクラスだけを差し替えることで対応する
********************************************/

	String dataPath; 	//データファイルのパスを格納する
    File file; 			// データファイルを格納する

    public UserDAO( ServletContext context) {
        this.dataPath = context.getRealPath("/WEB-INF/data/recordData.csv");
        this.file = new File(dataPath);
	}


    
    /********************************************
     * 　全ユーザーデータを格納したList<>を返すメソッド
     * 　他のメソッドから呼び出される
    ********************************************/
	
	public List<User> findAll() {

	    List<User> list = new ArrayList<>();

	    if (!file.exists()) {
	        return list;
	    }

	    try (CSVReader reader =
	            new CSVReader(
	                    new InputStreamReader(
	                            new FileInputStream(file),
	                            StandardCharsets.UTF_8))) {

	        List<String[]> rows = reader.readAll();

	        for (int i = 1; i < rows.size(); i++) {

	            String[] row = rows.get(i);

	            User user = new User(
	                    row[0],
	                    row[1],
	                    row[2],
	                    row[3]
	            );

	            list.add(user);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
		

	public boolean insert(User user) {
        // すでに同じIDが存在する場合は登録失敗とする
        if (exists(user.getId())) {
            System.out.println("挿入失敗：IDが既に存在します。");
            return false;
        }

        
        boolean isNewFile = !file.exists();

        // 追記モードでCSVWriterをオープン
        // FileOutputStream(file, true) の true が「追記」を意味します
        try (CSVWriter writer =
        		new CSVWriter(
        			new OutputStreamWriter(
        				new FileOutputStream(file, true), 
        				StandardCharsets.UTF_8))) {
        	// 新規ファイルならヘッダーを書き込む
            if (isNewFile) {
                String[] header = {"ログインID", "パスワード", "名前", "自己紹介"};
                writer.writeNext(header);
            }
            // UserオブジェクトをString配列に変換
            String[] row = {
                user.getId(),
                user.getPass(),
                user.getName(),
                user.getProfile()            };

            // ファイルに書き出し
            writer.writeNext(row);
            System.out.println("挿入成功：" + user.getId());
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	 // ID存在チェック
    public boolean exists(String id) {

        if (!file.exists()) {
            return false;
        }

        try (CSVReader reader =
                new CSVReader(
                        new InputStreamReader(
                                new FileInputStream(file),
                                StandardCharsets.UTF_8))) {

            List<String[]> list = reader.readAll();

            for (int i = 1; i < list.size(); i++) { // ヘッダー除外
                String[] row = list.get(i);

                if (row[0].equals(id)) {
                    return true; //一致するIDがあればTrueを返す
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
	
 // ★ IDでユーザーを1件取得するメソッドを追加
    public User findById(String id) {
        List<User> list = findAll();
        for (User user : list) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null; // 見つからない場合
    }	
	public User login(String id,String pass) {
		List<User> list = findAll();
		for (User someOne : list) {
			if (someOne.getId().equals(id) && someOne.getPass().equals(pass)) {
				return someOne; // 一致するユーザーが見つかった
			}
		}
		return null; // 見つからなかった
	}
}
