# 📍 場所情報付きつぶやきアプリ

Java / Servlet / JSP で作る、ログイン機能・地図機能付きのつぶやきアプリです。

---

## 機能一覧

- ユーザー登録 / ログイン / ログアウト
- つぶやきの投稿（テキスト＋地図で場所を選択）
- タイムライン表示（投稿者名リンク付き）
- 投稿者名をクリックしてプロフィールを表示
- 未ログイン時はすべてのページをログイン画面にリダイレクト

---

## 技術スタック

| 項目 | 内容 |
|---|---|
| サーバーサイド | Java / Jakarta Servlet / JSP |
| サーブレットコンテナ | Apache Tomcat 10.x |
| 地図ライブラリ | Leaflet.js 1.9.4 |
| 地図データ | OpenStreetMap（無料・APIキー不要） |
| ~~つぶやきデータ保存~~ | ~~XML ファイル（`WEB-INF/data/tweets.xml`）~~ |
| ~~ユーザーデータ保存~~ | ~~CSV ファイル（`WEB-INF/data/recordData.csv`）~~ |
| ~~CSV ライブラリ~~ | ~~opencsv 5.9~~ |
| SQL サーバー | H2 Database |
| JDBCドライバ | h2-2.3.240.jar |
| import パッケージ | `jakarta.servlet.*`（Tomcat 10 以降） |

---

## ファイル構成（JDBC版）

```
src/
└ tweet/
    ├── listener/
    │   └── AppInitListener.java  ← データベース初期化リスナー
    ├── model/
    │   ├── Tweet.java            ← つぶやきデータBean
    │   └── User.java             ← ユーザーデータBean
    ├── dao/
    │   ├── TweetDao.java         ← つぶやきDAOインタフェース
    │   ├── (XmlTweetDao.java      ← XML実装)
    │   ├── DBConfig.java          ← データベースのURLを持つクラス
    │   ├── jdbcTweetDao.java      ← jdbc版ツイートデータアクセスクラス
    │   ├── (UserDAO.java          ← ユーザーCSVアクセスクラス)
    │   └── jdbcUserDAO.java       ← jdbc版ユーザーデータアクセスクラス
    └── servlet/
        ├── PostServlet.java      ← つぶやき投稿
        ├── TimelineServlet.java  ← タイムライン表示
        ├── ProfileServlet.java   ← プロフィール表示
        ├── UserLogin.java        ← ログイン / ログアウト
        └── RegisterUser.java     ← ユーザー登録

WebContent/
├── post.jsp          ← 投稿フォーム（地図付き）
├── timeline.jsp      ← タイムライン
├── profile.jsp       ← プロフィール表示
└── WEB-INF/
    ├── jsp/
    │   ├── loginForm.jsp         ← ログインページ
    │   ├── registerForm.jsp      ← ユーザー登録ページ
    │   ├── registerConfirm.jsp   ← 登録確認ページ
    │   └── registerDone.jsp      ← 登録完了ページ
（以下はjdbc版で不要）
    └── data/                     ← 手動で作成が必要
        ├── tweets.xml            （初回投稿時に自動生成）
        └── recordData.csv        （初回登録時に自動生成）
```

---

## セットアップ

### 前提条件

- JDK 17 以上
- Apache Tomcat 10.x
- ~~opencsv 5.9（`WEB-INF/lib/` に配置）~~
- データベースが接続済みで、そのURLをDBConfig.javaに記述済みであること
### 手順

1. リポジトリをクローン

```bash
git clone https://github.com/your-username/your-repo.git
```

2. `WEB-INF/data/` フォルダを手動で作成

```
WebContent/WEB-INF/data/
```

3. opencsv の jar を `WEB-INF/lib/` に配置

4. Tomcat にデプロイして起動

5. ブラウザで以下にアクセス

```
http://localhost:8080/{プロジェクト名}/UserLogin
```

---

## 画面遷移

```
[loginForm.jsp] → POST /UserLogin
    ↓ 認証成功
[PostServlet] doGet → post.jsp（投稿フォーム）
    ↓ POST /post
[PostServlet] doPost → dao.save()
    ↓ リダイレクト
[TimelineServlet] → timeline.jsp
    ↓ 投稿者名クリック
[ProfileServlet] → profile.jsp
```

---

## セキュリティ

- 未ログイン状態で `/post`・`/timeline`・`/profile` にアクセスすると、ログイン画面にリダイレクトされます。
- セッションに `loginUser` が存在するかどうかでログイン状態を判定しています。

---

## 注意事項

- `WEB-INF/data/` フォルダは **手動で作成** が必要です。
- Tomcat 10 以降は `javax.servlet.*` ではなく `jakarta.servlet.*` を使用してください。
- データ保存は XML / CSV ファイルのため、本番運用には適していません。将来的には `TweetDao` インタフェースと `UserDAO` クラスをそれぞれ JDBC 実装に差し替えることで SQL 移行が可能です。

---

## アップデート

- 2026-03-17:SQL データベース（H2 Database）への移行    
- 2026-03-18:パスワードのハッシュ化（BCrypt）            
  
---

## 将来の拡張案

- つぶやきの削除・編集機能
- ユーザーごとのタイムラインフィルタリング
