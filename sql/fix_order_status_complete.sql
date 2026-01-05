-- ============================================
-- OrderStatus完全修正スクリプト
-- ============================================

USE healsweets;

-- ステップ1: 現在の問題を診断
SELECT '=== 現在のステータス一覧 ===' AS step;
SELECT status, COUNT(*) as count 
FROM orders 
GROUP BY status;

-- ステップ2: 不正な値を修正
SELECT '=== ステータスの修正 ===' AS step;

-- COMPLETED → DELIVERED に変更
UPDATE orders 
SET status = 'DELIVERED' 
WHERE status = 'COMPLETED';

-- その他の不正な値も修正（存在する場合）
UPDATE orders 
SET status = 'PENDING' 
WHERE status NOT IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED');

-- ステップ3: 修正結果を確認
SELECT '=== 修正後のステータス一覧 ===' AS step;
SELECT status, COUNT(*) as count 
FROM orders 
GROUP BY status;

-- ステップ4: 最近の注文を確認
SELECT '=== 最近の注文（上位10件）===' AS step;
SELECT 
    id, 
    order_number, 
    status,
    total_amount,
    created_at 
FROM orders 
ORDER BY created_at DESC 
LIMIT 10;

-- ステップ5: CHECK制約を追加（MySQL 8.0.16以降）
SELECT '=== CHECK制約の追加 ===' AS step;

-- 既存の制約を削除（存在する場合）
ALTER TABLE orders DROP CONSTRAINT IF EXISTS chk_order_status;

-- 新しい制約を追加
ALTER TABLE orders 
ADD CONSTRAINT chk_order_status 
CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'));

-- 完了メッセージ
SELECT '===================================' AS '';
SELECT 'OrderStatus修正完了！' AS message;
SELECT '有効なステータス:' AS '';
SELECT '  - PENDING (処理中)' AS valid_status
UNION ALL SELECT '  - CONFIRMED (確認済み)'
UNION ALL SELECT '  - SHIPPED (発送済み)'
UNION ALL SELECT '  - DELIVERED (配達完了)'
UNION ALL SELECT '  - CANCELLED (キャンセル)';
SELECT '===================================' AS '';
