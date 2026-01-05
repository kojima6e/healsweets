-- ============================================
-- OrderStatus修正用SQL
-- ============================================

USE healsweets;

-- 1. 現在のステータスを確認
SELECT DISTINCT status FROM orders;

-- 2. 不正な値 'COMPLETED' を 'DELIVERED' に修正
UPDATE orders 
SET status = 'DELIVERED' 
WHERE status = 'COMPLETED';

-- 3. 修正結果を確認
SELECT id, order_number, status, created_at 
FROM orders 
ORDER BY created_at DESC 
LIMIT 10;

-- 4. 再度ステータスを確認（'COMPLETED'が残っていないことを確認）
SELECT DISTINCT status FROM orders;

SELECT 'OrderStatus修正完了' AS message;
SELECT '有効なステータス: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED' AS valid_statuses;
