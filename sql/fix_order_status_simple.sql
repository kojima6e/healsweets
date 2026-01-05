-- ============================================
-- OrderStatus簡易修正スクリプト
-- ============================================

USE healsweets;

-- COMPLETEDをDELIVEREDに修正
UPDATE orders SET status = 'DELIVERED' WHERE status = 'COMPLETED';

-- 不正な値を全てPENDINGに修正
UPDATE orders SET status = 'PENDING' 
WHERE status NOT IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED');

-- 確認
SELECT 'OrderStatus修正完了' AS message;
SELECT DISTINCT status FROM orders;
