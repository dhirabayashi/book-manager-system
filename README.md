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

- **`POST /authors`** - 著者を登録
- **`PUT /authors`** - 著者を更新
- **`GET /authors/list`** - すべての著者を取得。書籍の登録のために著者IDを知るために使用する。
- **`GET /authors/{author_id}/books`** - 特定の著者の書籍一覧を取得

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

### テスト例（curl）

#### 著者API

```bash
# 著者の登録
curl -X POST http://localhost:8080/authors \
-H "Content-Type: application/json" \
-d '{
    "name": "John Smith",
    "birthDate": "1985-03-10"
}'

# 著者の更新
curl -X PUT http://localhost:8080/authors \
-H "Content-Type: application/json" \
-d '{
    "id": "author1",
    "name": "John Smith Jr.",
    "birthDate": "1985-03-10"
}'

# 著者一覧の取得
curl -X GET http://localhost:8080/authors/list

# 特定の著者の書籍一覧を取得
curl -X GET http://localhost:8080/authors/author1/books
```

---

#### 書籍API

```bash
# 書籍の登録
curl -X POST http://localhost:8080/books \
-H "Content-Type: application/json" \
-d '{
    "title": "Mastering Kotlin",
    "price": 3000,
    "authorIds": ["author1"],
    "publishingStatus": "PUBLISHED"
}'

# 書籍の更新
curl -X PUT http://localhost:8080/books \
-H "Content-Type: application/json" \
-d '{
    "id": "book3",
    "title": "Mastering Kotlin - Revised Edition",
    "price": 3500,
    "authorIds": ["author1"],
    "publishingStatus": "PUBLISHED"
}'
```

---

### 補足

* **localhost:8080** は、アプリケーションがローカルで起動していることを想定しています。
* もしDocker Composeで起動している場合、`localhost` を適切なホスト名に変更してください。

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
* 複数トランザクションによる更新の同時実行時の考慮
* N+1問題の考慮
* GitHub Actionsなどの設定
* 各種設定値の環境ごとの管理
* Version Catalogなどを使った厳密な依存管理
* メトリクス
* OpenAPIなどを使ったAPI定義の管理
* その他もろもろ

---

### ライセンス

このリポジトリは CC0 1.0 Universal (パブリックドメイン) で提供されています。詳細は LICENSE ファイルをご確認ください。
