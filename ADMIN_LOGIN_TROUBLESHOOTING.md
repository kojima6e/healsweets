# 管理者ログイン トラブルシューティング

## 問題: 管理者アカウントでログインできない

### 解決方法（推奨順）

## 📌 方法1: 既存アカウントを管理者に昇格（最も簡単）

既存のテストアカウント `tanaka@example.com` を管理者に変更します。

```bash
mysql -u root -p < sql/upgrade_existing_user.sql
```

**ログイン情報:**
- Email: `tanaka@example.com`
- Password: `password123`

このアカウントで管理者ページ（http://localhost:8080/admin）にアクセスできます。

---

## 📌 方法2: 管理者アカウントを手動で作成

### ステップ1: MySQLにログイン
```bash
mysql -u root -p
```

### ステップ2: データベースを選択
```sql
USE healsweets;
```

### ステップ3: roleカラムが存在するか確認
```sql
DESC members;
```

もし `role` カラムが存在しない場合：
```sql
ALTER TABLE members 
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER enabled;
```

### ステップ4: 既存アカウントを管理者に変更
```sql
-- tanaka@example.comを管理者に
UPDATE members SET role = 'ADMIN' WHERE email = 'tanaka@example.com';

-- 確認
SELECT id, email, role, enabled FROM members;
```

### ステップ5: アプリケーションを再起動
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### ステップ6: ログイン
- Email: `tanaka@example.com`
- Password: `password123`
- アクセス: http://localhost:8080/admin

---

## 📌 方法3: 新規管理者アカウントを作成

### ステップ1: パスワードハッシュを生成

以下のJavaクラスを実行してパスワードハッシュを生成します：

```bash
mvn exec:java -Dexec.mainClass="com.healsweets.util.PasswordHashGenerator"
```

これにより、`admin123` のBCryptハッシュが表示されます。

### ステップ2: MySQLで管理者を追加

```sql
USE healsweets;

-- 既存の管理者アカウントを削除（重複を避けるため）
DELETE FROM members WHERE email = 'admin@healsweets.com';

-- 新規管理者を追加（パスワードハッシュは上記で生成したものを使用）
INSERT INTO members (
    last_name, first_name, email, password, 
    phone, postal_code, prefecture, city, address1, 
    newsletter, enabled, role
) VALUES (
    '管理者', 'システム', 'admin@healsweets.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    '090-0000-0000', '100-0001', '東京都', '千代田区', '丸の内1-1-1',
    FALSE, TRUE, 'ADMIN'
);

-- 確認
SELECT id, email, role, enabled FROM members WHERE role = 'ADMIN';
```

### ステップ3: ログイン
- Email: `admin@healsweets.com`
- Password: `admin123`

---

## 🔍 デバッグ方法

### 1. データベースの状態を確認

```sql
USE healsweets;

-- 1. roleカラムが存在するか
DESC members;

-- 2. 管理者アカウントが存在するか
SELECT id, email, role, enabled FROM members;

-- 3. 管理者が存在するか
SELECT * FROM members WHERE role = 'ADMIN';
```

### 2. アプリケーションログを確認

アプリケーションを起動して、ログイン試行時のエラーメッセージを確認：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

ログイン失敗時に以下のようなメッセージが表示される場合：
- `Bad credentials`: パスワードが間違っている
- `User not found`: メールアドレスが間違っている、またはアカウントが存在しない
- `Account is disabled`: アカウントが無効化されている（enabled=false）

### 3. パスワードハッシュの検証

生成したパスワードハッシュが正しいか確認：

```bash
mvn exec:java -Dexec.mainClass="com.healsweets.util.PasswordHashGenerator"
```

出力されたハッシュをデータベースの `password` カラムと比較します。

---

## ❗ よくある間違い

### 1. roleカラムが追加されていない
```sql
-- エラーメッセージ: Unknown column 'role' in 'field list'
-- 解決方法:
ALTER TABLE members ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';
```

### 2. パスワードハッシュが間違っている
```sql
-- パスワードを再設定
UPDATE members 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE email = 'admin@healsweets.com';
```

### 3. アカウントが無効化されている
```sql
-- アカウントを有効化
UPDATE members SET enabled = TRUE WHERE email = 'admin@healsweets.com';
```

### 4. プロファイルが間違っている
```bash
# H2ではなくMySQLで起動していることを確認
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 📝 確認チェックリスト

- [ ] MySQLデータベースが起動している
- [ ] `healsweets` データベースが存在する
- [ ] `members` テーブルに `role` カラムが存在する
- [ ] 管理者アカウントが登録されている（`role='ADMIN'`）
- [ ] 管理者アカウントが有効化されている（`enabled=TRUE`）
- [ ] 正しいプロファイルで起動している（MySQL）
- [ ] 正しいログイン情報を使用している

---

## 🆘 それでも解決しない場合

### 完全リセット

データベースを完全にリセットして最初からやり直す：

```bash
# 1. データベースを削除して再作成
mysql -u root -p < sql/init_database.sql

# 2. roleカラムを追加
mysql -u root -p < sql/upgrade_existing_user.sql

# 3. アプリケーションを再起動
mvn clean
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

**ログイン情報:**
- Email: `tanaka@example.com`
- Password: `password123`
- URL: http://localhost:8080/admin

---

## 🎯 最も簡単な方法のまとめ

```bash
# 1コマンドで解決
mysql -u root -p < sql/upgrade_existing_user.sql

# ログイン
# Email: tanaka@example.com
# Password: password123
```

これで管理者としてログインできます！
