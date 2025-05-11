# Book Manager API

Kotlin + Spring Boot を使用した書籍管理API。

---

### 環境構築

* JDK 17

```bash
# リポジトリのクローン
git clone https://github.com/dhirabayashi/book-manager-system.git
cd book-manager-system

# アプリケーションの起動
./gradlew bootRun
```

---

### 設計

* オニオンアーキテクチャを採用
* 著者は書籍側に属するデータという整理にしている

---

### API一覧

#### 著者API

- **`GET /authors/list`** - すべての著者を取得。書籍の登録のために著者IDを知るために使用する。
- **`GET /authors/{author_id}/books`** - 特定の著者の書籍一覧を取得
- **`POST /authors`** - 著者を登録
- **`PUT /authors`** - 著者を更新

---

#### 書籍API

- **`POST /books`** - 書籍を登録
- **`PUT /books`** - 書籍を更新

---

### テスト実行方法

```bash
./gradlew test
```

---

### あえてやっていないこと

このプロジェクトは実用を意図していません。そのため以下は考慮していません：

* 著者、書籍の削除機能
* 全く同一の情報の登録の抑止
* 書籍情報を直接知るためのAPIの提供
* 著者の取得対象を絞り込めるAPIの提供
* 適切なロギング
* 厳密なバリデーション
* 厳密なエラーハンドリング
* ログへの機密情報漏洩防止
* 認証やセキュリティの厳密な考慮
* 複数トランザクションによる排他制御
* N+1問題の考慮
* GitHub Actionsなどの設定
* 各種設定値の環境ごとの管理
* 厳密なバリデーション
* Version Catalogなどを使った厳密な依存管理
* メトリクス
* OpenAPIなどを使ったAPI定義の管理
* その他もろもろ

---

### ライセンス

このリポジトリは CC0 1.0 Universal (パブリックドメイン) で提供されています。詳細は LICENSE ファイルをご確認ください。
