# つぶやきアプリ（Java / Servlet / JSP + 地図機能）

Java + Servlet + JSP で作る「場所情報付きつぶやきアプリ」。  
地図ライブラリに **Leaflet.js + OpenStreetMap**（無料・APIキー不要）を使用。  
データ保存は XML ファイルで行い、DAO パターンにより将来の SQL 移行に対応できる構成にしている。

---

## 技術スタック

| 項目 | 内容 |
|------|------|
| サーバーサイド | Java / Jakarta Servlet / JSP |
| サーブレットコンテナ | Apache Tomcat 10.x |
| 地図ライブラリ | Leaflet.js 1.9.4 |
| 地図データ | OpenStreetMap（無料） |
| データ保存 | XML ファイル（`WEB-INF/data/tweets.xml`） |
| import パッケージ | `jakarta.servlet.*`（Tomcat 10 以降） |

> Tomcat 9 以前を使う場合は `jakarta.servlet.*` を `javax.servlet.*` に読み替えること。

---

## ファイル構成

```
src/
├── model/
│   └── Tweet.java             ← データ Bean
├── dao/
│   ├── TweetDao.java          ← インタフェース（差し替えポイント）
│   └── XmlTweetDao.java       ← XML 実装（現在）
└── servlet/
    ├── PostServlet.java
    └── TimelineServlet.java

WebContent/
├── post.jsp
├── timeline.jsp
└── WEB-INF/
    ├── web.xml
    └── data/
        └── tweets.xml         ← 永続化ファイル（自動生成）
```

> `WEB-INF/data/` フォルダは手動で作成しておく。  
> `tweets.xml` は初回投稿時に `XmlTweetDao` が自動生成する。

---

## アーキテクチャ設計（DAO パターン）

サーブレットは `TweetDao` インタフェースしか知らない。  
`XmlTweetDao` か `JdbcTweetDao` かは意識しない。

```
[post.jsp] フォーム送信
    ↓ POST /post
[PostServlet] text / lat / lng を受け取り → dao.save()
    ↓ リダイレクト
[TimelineServlet] GET /timeline → dao.findAll()
    ↓ forward
[timeline.jsp] 一覧表示
```

---

## XML スキーマ

```xml
<?xml version="1.0" encoding="UTF-8"?>
<tweets>
  <tweet>
    <text>桜が咲いてた！</text>
    <lat>35.6762</lat>
    <lng>139.6503</lng>
    <postedAt>2026-03-14T10:32:00</postedAt>
  </tweet>
</tweets>
```

---

## 将来の発展

### ログイン機能を付ける

別のサーブレット演習で作成したユーザー登録とログイン機能を合体させ、登録ユーザーのみ書き込み可能にする。

### SQL 移行

1. `JdbcTweetDao` クラスを新規作成し `TweetDao` を `implements` する
2. `PostServlet` と `TimelineServlet` の `init()` 内の 1 行を変更する

```java
// 変更前
dao = new XmlTweetDao(filePath);

// 変更後
dao = new JdbcTweetDao(/* DB 接続情報 */);
```

サーブレットも JSP も一切触らなくてよい。

---

## セットアップ

1. `WEB-INF/data/` フォルダを手動で作成する
2. Tomcat 10.x にデプロイする
3. ブラウザで `/post` にアクセスして投稿する
4. 投稿後、`/timeline` に自動遷移して一覧が表示される

---

## 注意事項

- ファイルパスの取得には `getServletContext().getRealPath()` を使う
- `tweets.xml` を `WEB-INF` 以下に置くことでブラウザからの直接アクセスを防ぐ
- Tomcat 10 以降では `javax.servlet.*` ではなく `jakarta.servlet.*` を使う