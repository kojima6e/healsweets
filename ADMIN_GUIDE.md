# 管理者機能実装ガイド

## 実装完了内容

### ✅ 実装済み機能

#### 1. 会員管理
- **会員一覧表示**: 全会員の詳細情報を表示（ID、氏名、メール、電話番号、権限、ステータス、登録日）
- **会員無効化**: 会員を論理削除（enabled=false）
- **会員復元**: 無効化した会員を再度有効化
- **統計表示**: 総会員数、有効会員数、無効会員数

#### 2. 商品管理
- **商品一覧表示**: 全商品の詳細情報を表示（ID、画像、商品名、価格、カテゴリ、在庫、ステータス、更新日）
- **商品追加**: 新規商品の登録
  - 基本情報（商品名、説明、価格、在庫、カテゴリ、画像URL）
  - 商品詳細（内容量、賞味期限、保存方法）
  - アレルゲン情報（複数選択）
  - 商品特徴（複数選択）
  - 販売設定（有効/無効）
- **商品編集**: 既存商品の情報更新
- **商品無効化**: 商品を販売停止（active=false）
- **商品有効化**: 無効化した商品を再度販売
- **統計表示**: 総商品数、販売中商品数、停止中商品数

#### 3. 注文管理
- **注文一覧表示**: 全注文の詳細情報を表示（注文番号、会員情報、商品数、金額、支払方法、配送オプション、ステータス、注文日時）
- **統計表示**: 総注文数、処理待ち、配送中、完了

#### 4. ダッシュボード
- **統計カード**: 会員数、商品数、注文数、今月の売上
- **クイックアクセス**: 各管理機能への素早いアクセス
- **最近の注文**: 直近10件の注文を表示

### 🗂️ 作成ファイル一覧

#### Java
```
src/main/java/com/healsweets/
├── controller/
│   └── AdminController.java          # 管理者コントローラー
├── service/
│   └── AdminService.java             # 管理者サービス
├── dto/
│   └── ProductAdminDto.java          # 商品管理用DTO
└── entity/
    └── Member.java                   # roleフィールド追加
```

#### HTML
```
src/main/resources/templates/admin/
├── dashboard.html                     # ダッシュボード
├── member-list.html                   # 会員一覧
├── product-list.html                  # 商品一覧
├── product-form.html                  # 商品追加・編集フォーム
└── order-list.html                    # 注文一覧
```

#### CSS
```
src/main/resources/static/css/
└── admin-style.css                    # 管理者ページ専用CSS
```

#### SQL
```
sql/
└── add_admin_role.sql                 # 管理者機能追加SQL
```

### 📊 データベース変更

#### 追加カラム
```sql
-- membersテーブル
ALTER TABLE members 
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';
```

**roleの値:**
- `USER`: 一般会員
- `ADMIN`: 管理者

## セットアップ手順

### 1. データベースの更新
```bash
mysql -u root -p < sql/add_admin_role.sql
```

### 2. アプリケーションの再起動
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### 3. 管理者アカウントでログイン
```
Email: admin@healsweets.com
Password: admin123
```

### 4. 管理者ダッシュボードにアクセス
```
http://localhost:8080/admin
```

## 使用方法

### 会員管理
1. `/admin/members` にアクセス
2. 会員一覧から操作したい会員を選択
3. 「無効化」または「復元」ボタンをクリック

### 商品管理
1. `/admin/products` にアクセス
2. 新規追加する場合は「新規商品追加」ボタンをクリック
3. 編集する場合は各商品の「編集」ボタンをクリック
4. 無効化/有効化する場合は対応するボタンをクリック

### 注文管理
1. `/admin/orders` にアクセス
2. 全注文の一覧と統計を確認

## セキュリティ

### アクセス制御
- 管理者ページ（`/admin/**`）は認証が必要
- `AdminController`内で管理者権限をチェック
- 一般ユーザーがアクセスすると自動的にトップページにリダイレクト

### 実装方法
```java
private boolean checkAdminRole(UserDetails userDetails) {
    return memberService.findByEmail(userDetails.getUsername())
            .map(Member::isAdmin)
            .orElse(false);
}
```

## 推奨する今後の拡張機能

### 1. 注文ステータス管理 ⭐⭐⭐
**優先度: 高**

#### 実装内容
- 注文詳細ページの作成
- ステータス変更機能（PENDING → PROCESSING → SHIPPED → DELIVERED）
- ステータス変更履歴の記録
- 会員への通知機能（メール）

#### 必要な変更
- `AdminController`に注文詳細とステータス更新のエンドポイント追加
- `AdminService`にステータス更新メソッド追加
- 注文詳細ページ（`order-detail.html`）の作成
- 注文履歴テーブル（`order_history`）の追加（オプション）

### 2. お問い合わせ管理 ⭐⭐
**優先度: 中**

#### 実装内容
- お問い合わせ一覧表示
- お問い合わせ詳細表示
- 返信機能
- ステータス管理（NEW → IN_PROGRESS → RESOLVED）

#### 必要な変更
- `AdminController`にお問い合わせ管理のエンドポイント追加
- お問い合わせ一覧・詳細ページの作成
- 返信機能の実装（メール送信）

### 3. 売上レポート ⭐⭐⭐
**優先度: 高**

#### 実装内容
- 期間別売上グラフ（日別、月別、年別）
- 商品別売上ランキング
- カテゴリー別売上分析
- CSV/PDFエクスポート機能

#### 必要な変更
- `AdminService`に売上分析メソッド追加
- グラフ表示用のJavaScriptライブラリ（Chart.js等）の導入
- レポートページ（`reports.html`）の作成

### 4. 在庫管理強化 ⭐⭐
**優先度: 中**

#### 実装内容
- 在庫アラート機能（閾値以下で通知）
- 自動在庫補充通知
- 在庫履歴管理
- 在庫一括更新機能

#### 必要な変更
- 商品テーブルに`stock_threshold`カラム追加
- 在庫履歴テーブル（`stock_history`）の追加
- バッチ処理で在庫チェック

### 5. クーポン管理 ⭐
**優先度: 低**

#### 実装内容
- クーポンの作成・編集・削除
- クーポンコードの生成
- 利用条件の設定（最低購入金額、有効期限等）
- 利用状況の確認

#### 必要な変更
- クーポンテーブル（`coupons`）の追加
- クーポン使用履歴テーブル（`coupon_usage`）の追加
- チェックアウト時のクーポン適用ロジック修正

### 6. 複数管理者・権限管理 ⭐⭐
**優先度: 中**

#### 実装内容
- 管理者の権限レベル設定（SUPER_ADMIN, ADMIN, MODERATOR等）
- 機能ごとのアクセス制御
- 操作ログの記録
- 管理者アカウント管理画面

#### 必要な変更
- 権限テーブル（`roles`, `permissions`）の追加
- Spring Securityの権限設定強化
- 操作ログテーブル（`admin_logs`）の追加

## トラブルシューティング

### 管理者ページが表示されない
```bash
# 1. roleカラムが存在するか確認
mysql -u root -p healsweets -e "DESC members;"

# 2. 管理者アカウントが存在するか確認
mysql -u root -p healsweets -e "SELECT id, email, role FROM members WHERE role='ADMIN';"

# 3. add_admin_role.sqlを再実行
mysql -u root -p < sql/add_admin_role.sql
```

### 一般ユーザーでログインすると管理者ページにアクセスできてしまう
- `AdminController`の各メソッドで`checkAdminRole()`が呼ばれているか確認
- 権限がない場合は`return "redirect:/";`でリダイレクトされているか確認

### 商品画像が表示されない
```bash
# 1. 画像ファイルが正しい場所にあるか確認
ls src/main/resources/static/images/

# 2. imageUrlの形式を確認（正しい形式: /images/item01.jpg）
mysql -u root -p healsweets -e "SELECT id, name, image_url FROM products;"
```

## パフォーマンス最適化のヒント

### 1. ページネーション
大量のデータを扱う場合は、ページネーションの実装を推奨
```java
// Spring Data JPAのPageableを使用
Page<Member> findAll(Pageable pageable);
```

### 2. キャッシュ
頻繁にアクセスされる統計データはキャッシュを利用
```java
@Cacheable("dashboardStats")
public Map<String, Object> getDashboardStats() { ... }
```

### 3. インデックス
検索が遅い場合はインデックスを追加
```sql
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_products_category ON products(category);
```

## まとめ

管理者機能の実装により、以下が可能になりました：
- ✅ 会員の管理（一覧表示、無効化、復元）
- ✅ 商品の管理（一覧表示、追加、編集、無効化、有効化）
- ✅ 注文の管理（一覧表示、統計）
- ✅ ダッシュボードでの統計確認

今後の拡張として、注文ステータス管理、お問い合わせ管理、売上レポートなどの実装を推奨します。
