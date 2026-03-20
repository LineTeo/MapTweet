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
        ├── EditProfileServlet.java   ← プロフィール編集　追加
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
    │   ├── registerDone.jsp      ← 登録完了ページ
    │   ├── editProfile.jsp       ← 登録情報編集ページ　　追加
    │   └── editComfirm.jsp       ← 編集確認ページ　　　　追加
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
- データベースが接続済みであること
### 手順

1. リポジトリをクローン

```bash
git clone https://github.com/your-username/your-repo.git
```

2. `WEB-INF/data/` フォルダを手動で作成

```
WebContent/WEB-INF/data/
```

~~3. opencsv の jar を `WEB-INF/lib/` に配置~~
3. DbConfig.javaにデータベースのURLを記述

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
```mermaid
graph TD
    %% 開始とログインを最上部に配置
    Start((開始)) --> Login[ログイン画面]

    %% ログアウト
    Timeline -.->|ログアウト| Login

    %% 登録フロー（左側にまとめる）
    Login -.->|新規登録| Register[ユーザー登録画面]
    Register -->|確認ボタン| RegConfirm[登録内容確認画面]
    RegConfirm -->|登録ボタン| RegComplete[登録終了画面]
    RegComplete -.->|ログイン画面へ| Login

    %% メイン機能（中央・右側に流す）
    Login -->|認証成功| Main[ツイート投稿画面]
    Main -->|つぶやく| Timeline[ツイート一覧画面]
    
    %% プロフィール関連
    Timeline -->|投稿者IDクリック| Profile[プロフィール画面]
    
    %% プロフィール分岐と編集
    Profile -->|タイムライン| Timeline
    Profile -.->|自分の場合のみ| ProfEdit[プロフィール編集画面]
    
    ProfEdit -->|変更ボタン| EditConfirm[編集内容確認画面]
    EditConfirm -->|変更| Timeline

    %% レイアウト調整用のスタイル（任意）
    style Start fill:#f9f,stroke:#333,stroke-width:2px
    style Login fill:#e1f5fe,stroke:#01579b
```
---
詳細フロー
```mermaid
graph TD
    %% 認証・登録フロー
    Start((開始)) -->|GET: /UserLogin|login1(tweet.servlet.UserLogin.java<br/>ログインページ表示) 
    login1-.->|forword|Login[ログインページ<br/>/loginForm.jsp]
    Login -->|ユーザー登録<br/>GET:/RegisterUser|register1(tweet.servlet.RegisterUser.java<br/>登録開始)
    register1 -.->|forword|Register[ユーザー登録ページ<br/>/registerForm.jsp]
    Register -->|確認ボタン<br/>POST:/RegisterUser|register2(tweet.servlet.RegisterUser.java<br/>登録確認)
    register2 -.->|forword|RegConfirm[登録確認ページ<br/>/registerConfirm.jsp]
    RegConfirm -->|登録<br/>GET:/RegisterUser?action=done|register3(tweet.servlet.RegisterUser.java<br/>登録実行)
    register3 -.->|forword|RegComplete[登録完了ページ<br/>/registerDone.jsp]
    RegComplete -->|完了<br/>GET:/UserLogin| login1
    Login -->|ログイン<br/>POST:/UserLogin|login2(tweet.servlet.UserLogin.java<br/>ユーザー認証)
    login2-.->|redirect GET:/post|post1(tweet.servlet.PostServlet.java<br/>ログインチェック)

    %% メイン機能
    post1-.->|forword|Main[投稿ページ<br/>/post.jsp]
    Main -->|つぶやく<br/>POST:/post|post2(tweet.servlet.PostServlet.java<br/>投稿)
    post2-.->|redirect GET:/timeline|timeline
    Main -->|タイムラインへ<br/>GET:/timeline|timeline(tweet.servlet.timeline.java<br/>ツイートデータ取得)
    timeline-.->|forword|Timeline[タイムライン<br/>/Timeline.jsp]

    %% プロフィール関連
    Timeline -->|GET: /profile?id=xxx<br/>投稿者IDクリック| profile(tweet.servlet.ProfileServlet.java<br/>登録情報取得)
    profile-.->|forward|Profile[プロフィール表示<br/>/Profile.jsp]
    Profile-->|戻る<br/>GET:/timeline| timeline
    Profile-->|自ID時プロフィール編集<br/>GET:/EditProfile|editprofile1(tweet.servlet.EditProfileServlet.java<br/>登録画面表示)
    editprofile1-.->|forward| ProfEdit[登録情報編集ページ<br/>/editProfile.jsp]
    ProfEdit -->|POST:/EditProfile?action=confirm<br/>確認ボタン|editprofile2(tweet.servlet.EditProfileServlet.java<br/>変更内容確認)
    editprofile2-.->|forward| EditConfirm[確認ページ<br/>/editComfirm.jsp]
    EditConfirm -->|POST:/EditProfile?action=update<br/>変更ボタン|editprofile3(tweet.servlet.EditProfileServlet.java<br/>変更処理実行)
    editprofile3-.->|redirect GET:/profile| profile

    %% ログアウト
    Timeline -->|ログアウト<br/>GET:UserLogin?action=done| login3[tweet.servlet.UserLogin.java<br/>ログアウト処理]
    login3 -.->|redirect|Login[ログインページ<br/>/loginForm.jsp]

    %% デザイン調整
    style Start fill:#f9f
    style Login fill:#e1f5fe
    style Register fill:#e1f5fe
    style RegConfirm fill:#e1f5fe
    style RegComplete fill:#e1f5fe
    style Main  fill:#e1f5fe
    style Timeline fill:#e1f5fe
    style Profile fill:#e1f5fe
    style ProfEdit  fill:#e1f5fe
    style EditConfirm fill:#e1f5fe
```
---

## セキュリティ対策

本アプリケーションでは、Webアプリケーションにおける主要な脆弱性に対して以下の対策を講じています。

### 1. 認証・セッション管理
* **アクセス制御**: 未ログイン状態で `/post`・`/timeline`・`/profile` 等の主要機能へ直接アクセスした場合、フィルターまたはサーブレットによりログイン画面へ強制リダイレクトします。
* **セッション固定攻撃 (Session Fixation) 対策**: 認証成功（ログイン時）に `request.changeSessionId()` を実行。既存のセッションIDを破棄し、新しいIDを再発行することで、第三者によるセッション乗っ取りを防止しています。
* **セッション管理**: `HttpSession` を利用し、サーバー側で安全にユーザーのログイン状態を保持しています。

### 2. リクエスト偽造・改ざん防止
* **CSRF (クロスサイト・リクエスト・フォージェリ) 対策**: 
    * ユーザー登録、プロフィール編集、ツイート投稿のすべての `POST` リクエストにおいて、UUIDによる**ワンタイムトークン**を生成・検証し、不正な第三者サイトからの操作を遮断しています。
    * トークンはセッションごとに管理され、更新処理が実行されるたびに検証・破棄される設計です。
* **PRG (Post-Redirect-Get) パターンの適用**: データの更新処理（POST）完了後は必ずリダイレクトを行うことで、ブラウザの「更新」ボタンによる**二重投稿や二重登録を防止**しています。

### 3. データ保護
* **SQLインジェクション対策**: すべてのDB操作（JDBC）において `PreparedStatement` を使用。静的プレースホルダによるバインド機構を利用し、SQLの構造とデータを完全に分離しています。
* **XSS (クロスサイト・スクリプティング) 対策**: ユーザー入力値をWebページに出力する際、JSTLの `<c:out>` や `escapeXML()` によるサニタイズを行い、悪意のあるスクリプトの実行を無効化しています。
* **パスワードの秘匿化**: データベースに保存するパスワードは `BCrypt` を用いてソルト付きハッシュ化を行い、万が一のデータ流出時にも生パスワードが露出しない設計としています。
---

## 注意事項

- ~~`WEB-INF/data/` フォルダは **手動で作成** が必要です。~~
- Tomcat 10 以降は `javax.servlet.*` ではなく `jakarta.servlet.*` を使用してください。
- ~~データ保存は XML / CSV ファイルのため、本番運用には適していません。将来的には `TweetDao` インタフェースと `UserDAO` クラスをそれぞれ JDBC 実装に差し替えることで SQL 移行が可能です。~~

---

## アップデート

- 2026-03-17:SQL データベース（H2 Database）への移行    
- 2026-03-18:パスワードのハッシュ化（BCrypt）
- 2026-03-19:ユーザー登録情報の編集機能追加、他
- 2026-03-20:セキュリティ対策（セッション固定攻撃対策、CSRF対策、PRGパターンの適用）の実装
---

## 将来の拡張案

- つぶやきの削除・編集機能
- ユーザーごとのタイムラインフィルタリング
