# OrderStatus エラー トラブルシューティング

## エラー: No enum constant com.healsweets.entity.OrderStatus.COMPLETED

### 🔍 問題の原因
データベースの `orders` テーブルに、Javaのenumで定義されていない値 `COMPLETED` が保存されています。

正しい `OrderStatus` の値:
- `PENDING` (処理中)
- `CONFIRMED` (確認済み)
- `SHIPPED` (発送済み)
- `DELIVERED` (配達完了) ← `COMPLETED`の代わりにこれを使用
- `CANCELLED` (キャンセル)

---

## 🔧 解決方法

### 方法1: 簡易修正（推奨）

最も簡単な方法です：

```bash
mysql -u root -p < sql/fix_order_status_simple.sql
```

または、MySQLに直接ログインして実行：

```sql
USE healsweets;

-- COMPLETEDをDELIVEREDに修正
UPDATE orders SET status = 'DELIVERED' WHERE status = 'COMPLETED';

-- 確認
SELECT DISTINCT status FROM orders;
```

### 方法2: 完全修正（制約付き）

CHECK制約を追加して今後の問題を防ぎます：

```bash
mysql -u root -p < sql/fix_order_status_complete.sql
```

---

## ✅ 修正後の確認

### 1. データベースの確認

```sql
USE healsweets;

-- ステータスの一覧を確認
SELECT DISTINCT status FROM orders;

-- 最近の注文を確認
SELECT id, order_number, status, created_at 
FROM orders 
ORDER BY created_at DESC 
LIMIT 10;
```

期待される出力:
```
+------------+
| status     |
+------------+
| PENDING    |
| CONFIRMED  |
| SHIPPED    |
| DELIVERED  |
| CANCELLED  |
+------------+
```

**`COMPLETED` が表示されないことを確認してください。**

### 2. アプリケーションの再起動

```bash
# アプリケーションを再起動
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### 3. 管理者ダッシュボードにアクセス

```
http://localhost:8080/admin
```

エラーが表示されなければ成功です！

---

## 🚫 今後この問題を防ぐには

### データベースにCHECK制約を追加

MySQL 8.0.16以降では、CHECK制約を使用できます：

```sql
ALTER TABLE orders 
ADD CONSTRAINT chk_order_status 
CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'));
```

これにより、不正な値がデータベースに保存されるのを防ぎます。

### コード側での検証

もしコードで直接SQLを実行している箇所がある場合は、必ず `OrderStatus` enumを使用してください：

```java
// ❌ 間違い
order.setStatus("COMPLETED");

// ✅ 正しい
order.setStatus(OrderStatus.DELIVERED);
```

---

## 🔍 デバッグ方法

### エラーが再発する場合

1. **データベースを再確認**

```sql
SELECT id, order_number, status, created_at 
FROM orders 
WHERE status NOT IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED');
```

このクエリで行が返された場合、不正なステータスがまだ存在します。

2. **アプリケーションログを確認**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql 2>&1 | grep -i "orderstatus"
```

3. **H2データベースを使用している場合**

H2データベースを使用している場合は、データベースがリセットされるため問題ありません：

```bash
# H2で起動（プロファイル指定なし）
mvn spring-boot:run
```

---

## 📝 よくある質問

### Q: なぜ `COMPLETED` という値が存在するのですか？

A: 以下の理由が考えられます：
1. 古いバージョンのコードで使用されていた
2. 手動でデータベースを更新した際に誤って入力した
3. 外部ツールやスクリプトで直接データを投入した

### Q: 既存の注文データは失われませんか？

A: いいえ、ステータスの値を `COMPLETED` から `DELIVERED` に変更するだけなので、注文データそのものは保持されます。

### Q: アプリケーションを再起動せずに修正できますか？

A: データベースの修正だけでは不十分です。アプリケーションのキャッシュに古いデータが残っている可能性があるため、必ず再起動してください。

---

## 🆘 それでも解決しない場合

### 完全リセット

最終手段として、データベースを完全にリセットします：

```bash
# 警告: 全てのデータが削除されます！

# 1. データベースを再作成
mysql -u root -p < sql/init_database.sql

# 2. 管理者権限を追加
mysql -u root -p < sql/upgrade_existing_user.sql

# 3. アプリケーションを再起動
mvn clean
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

---

## 📋 チェックリスト

修正完了後、以下を確認してください：

- [ ] `COMPLETED` ステータスがデータベースに存在しない
- [ ] 全ての注文が有効なステータスを持っている
- [ ] アプリケーションが正常に起動する
- [ ] 管理者ダッシュボードでエラーが表示されない
- [ ] 注文一覧ページでエラーが表示されない
- [ ] 新規注文が正常に作成できる

全てにチェックが入れば、修正完了です！
