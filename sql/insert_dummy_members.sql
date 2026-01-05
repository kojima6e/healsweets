-- ============================================
-- HealSweets ダミー会員データ（10人分）
-- MySQL 8.0用
-- ============================================
-- パスワードはすべて「password123」（BCryptエンコード済み）
-- ============================================

USE healsweets;

-- ============================================
-- 会員データ投入
-- ============================================
-- パスワード: password123 のBCryptハッシュ
-- $2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC

INSERT INTO members (last_name, first_name, email, password, phone, birth_date, postal_code, prefecture, city, address1, address2, newsletter, enabled) VALUES
-- 1. 田中太郎（既存データがある場合はスキップ）
-- ('田中', '太郎', 'tanaka@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '090-1234-5678', '1990-01-01', '123-4567', '東京都', '渋谷区', '○○ 1-2-3', '○○マンション 101号室', TRUE, TRUE),

-- 2. 佐藤花子
('佐藤', '花子', 'sato@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '090-2345-6789', '1985-03-15', '150-0001', '東京都', '渋谷区', '神宮前 2-3-4', 'サトウビル 202号室', TRUE, TRUE),

-- 3. 鈴木一郎
('鈴木', '一郎', 'suzuki@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '080-3456-7890', '1992-07-22', '160-0022', '東京都', '新宿区', '新宿 3-4-5', NULL, FALSE, TRUE),

-- 4. 高橋美咲
('高橋', '美咲', 'takahashi@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '070-4567-8901', '1988-11-30', '106-0032', '東京都', '港区', '六本木 4-5-6', '六本木ヒルズ 1503号室', TRUE, TRUE),

-- 5. 伊藤健太
('伊藤', '健太', 'ito@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '090-5678-9012', '1995-05-05', '220-0012', '神奈川県', '横浜市西区', 'みなとみらい 5-6-7', NULL, FALSE, TRUE),

-- 6. 渡辺さくら
('渡辺', 'さくら', 'watanabe@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '080-6789-0123', '1998-04-01', '530-0001', '大阪府', '大阪市北区', '梅田 6-7-8', 'グランフロント大阪 801号室', TRUE, TRUE),

-- 7. 山本大輔
('山本', '大輔', 'yamamoto@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '070-7890-1234', '1983-09-12', '460-0008', '愛知県', '名古屋市中区', '栄 7-8-9', NULL, FALSE, TRUE),

-- 8. 中村あおい
('中村', 'あおい', 'nakamura@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '090-8901-2345', '2000-08-08', '812-0011', '福岡県', '福岡市博多区', '博多駅前 8-9-10', 'JRJPビル 305号室', TRUE, TRUE),

-- 9. 小林翔太
('小林', '翔太', 'kobayashi@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '080-9012-3456', '1991-12-25', '980-0021', '宮城県', '仙台市青葉区', '中央 9-10-11', NULL, FALSE, TRUE),

-- 10. 加藤結衣
('加藤', '結衣', 'kato@example.com', '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC', '070-0123-4567', '1996-02-14', '600-8001', '京都府', '京都市下京区', '四条通 10-11-12', '京都タワー 1201号室', TRUE, TRUE);

-- 既存の田中太郎のパスワードも更新
UPDATE members SET password = '$2a$10$ra/eV4J5NZx2XV.oQhOzKObzxaSLFFhzHIbCS1yIzmBU9yjjf7ZIC' WHERE email = 'tanaka@example.com';

-- ============================================
-- 会員アレルギーデータ投入
-- ============================================
-- 佐藤花子（卵アレルギー）
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '卵' FROM members WHERE email = 'sato@example.com';

-- 鈴木一郎（小麦、乳製品アレルギー）
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '小麦' FROM members WHERE email = 'suzuki@example.com';
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '乳製品' FROM members WHERE email = 'suzuki@example.com';

-- 高橋美咲（そばアレルギー）
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, 'そば' FROM members WHERE email = 'takahashi@example.com';

-- 伊藤健太（落花生、アーモンドアレルギー）
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '落花生' FROM members WHERE email = 'ito@example.com';
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, 'アーモンド' FROM members WHERE email = 'ito@example.com';

-- 渡辺さくら（えび、かにアレルギー）
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, 'えび' FROM members WHERE email = 'watanabe@example.com';
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, 'かに' FROM members WHERE email = 'watanabe@example.com';

-- 中村あおい（乳製品アレルギー）
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '乳製品' FROM members WHERE email = 'nakamura@example.com';

-- 加藤結衣（卵、小麦、乳製品アレルギー）
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '卵' FROM members WHERE email = 'kato@example.com';
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '小麦' FROM members WHERE email = 'kato@example.com';
INSERT INTO member_allergies (member_id, allergy) 
SELECT id, '乳製品' FROM members WHERE email = 'kato@example.com';

-- 山本大輔、小林翔太はアレルギーなし

-- ============================================
-- カートアイテムデータ投入（一部の会員にカート内商品を追加）
-- ============================================
-- 佐藤花子のカート
INSERT INTO cart_items (member_id, product_id, quantity)
SELECT m.id, 1, 2 FROM members m WHERE m.email = 'sato@example.com';
INSERT INTO cart_items (member_id, product_id, quantity)
SELECT m.id, 3, 1 FROM members m WHERE m.email = 'sato@example.com';

-- 高橋美咲のカート
INSERT INTO cart_items (member_id, product_id, quantity)
SELECT m.id, 7, 1 FROM members m WHERE m.email = 'takahashi@example.com';

-- 渡辺さくらのカート
INSERT INTO cart_items (member_id, product_id, quantity)
SELECT m.id, 4, 3 FROM members m WHERE m.email = 'watanabe@example.com';
INSERT INTO cart_items (member_id, product_id, quantity)
SELECT m.id, 5, 1 FROM members m WHERE m.email = 'watanabe@example.com';
INSERT INTO cart_items (member_id, product_id, quantity)
SELECT m.id, 9, 2 FROM members m WHERE m.email = 'watanabe@example.com';

-- ============================================
-- 注文データ投入（一部の会員に注文履歴を追加）
-- ============================================
-- 鈴木一郎の注文
INSERT INTO orders (order_number, member_id, subtotal, shipping_fee, total_amount, shipping_name, shipping_postal_code, shipping_prefecture, shipping_city, shipping_address1, shipping_address2, shipping_phone, delivery_option, payment_method, status)
SELECT 
    CONCAT('HS', UNIX_TIMESTAMP(), '001'),
    m.id, 1470, 500, 1970,
    '鈴木 一郎', '160-0022', '東京都', '新宿区', '新宿 3-4-5', NULL, '080-3456-7890',
    'standard', 'credit', 'COMPLETED'
FROM members m WHERE m.email = 'suzuki@example.com';

-- 鈴木一郎の注文アイテム
INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal)
SELECT 
    o.id, 2, 'グルテンフリー クッキーボックス', 780, 1, 780
FROM orders o 
JOIN members m ON o.member_id = m.id 
WHERE m.email = 'suzuki@example.com' AND o.status = 'COMPLETED'
LIMIT 1;

INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal)
SELECT 
    o.id, 3, 'アレルゲンフリー スナックパック', 690, 1, 690
FROM orders o 
JOIN members m ON o.member_id = m.id 
WHERE m.email = 'suzuki@example.com' AND o.status = 'COMPLETED'
LIMIT 1;

-- 伊藤健太の注文
INSERT INTO orders (order_number, member_id, subtotal, shipping_fee, total_amount, shipping_name, shipping_postal_code, shipping_prefecture, shipping_city, shipping_address1, shipping_address2, shipping_phone, delivery_option, payment_method, status)
SELECT 
    CONCAT('HS', UNIX_TIMESTAMP(), '002'),
    m.id, 2970, 0, 2970,
    '伊藤 健太', '220-0012', '神奈川県', '横浜市西区', 'みなとみらい 5-6-7', NULL, '090-5678-9012',
    'express', 'credit', 'SHIPPED'
FROM members m WHERE m.email = 'ito@example.com';

-- 伊藤健太の注文アイテム
INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal)
SELECT 
    o.id, 8, 'カカオ70% プレミアムブラウニー', 990, 3, 2970
FROM orders o 
JOIN members m ON o.member_id = m.id 
WHERE m.email = 'ito@example.com' AND o.status = 'SHIPPED'
LIMIT 1;

-- 中村あおいの注文
INSERT INTO orders (order_number, member_id, subtotal, shipping_fee, total_amount, shipping_name, shipping_postal_code, shipping_prefecture, shipping_city, shipping_address1, shipping_address2, shipping_phone, delivery_option, payment_method, status)
SELECT 
    CONCAT('HS', UNIX_TIMESTAMP(), '003'),
    m.id, 1860, 500, 2360,
    '中村 あおい', '812-0011', '福岡県', '福岡市博多区', '博多駅前 8-9-10', 'JRJPビル 305号室', '090-8901-2345',
    'standard', 'convenience', 'PENDING'
FROM members m WHERE m.email = 'nakamura@example.com';

-- 中村あおいの注文アイテム
INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal)
SELECT 
    o.id, 4, '低糖質 グルメクッキー', 620, 3, 1860
FROM orders o 
JOIN members m ON o.member_id = m.id 
WHERE m.email = 'nakamura@example.com' AND o.status = 'PENDING'
LIMIT 1;

-- ============================================
-- お問い合わせデータ投入
-- ============================================
INSERT INTO contacts (member_id, category, subject, message, email, status)
SELECT m.id, '商品について', 'アレルギー表示について', 'アレルゲンの詳細な情報を教えてください。', 'sato@example.com', 'NEW'
FROM members m WHERE m.email = 'sato@example.com';

INSERT INTO contacts (member_id, category, subject, message, email, status)
SELECT m.id, '配送について', '配送日時の変更', '注文した商品の配送日を変更したいのですが可能でしょうか？', 'ito@example.com', 'IN_PROGRESS'
FROM members m WHERE m.email = 'ito@example.com';

INSERT INTO contacts (member_id, category, subject, message, email, status)
SELECT m.id, 'その他', 'ギフト包装について', 'ギフト用のラッピングは可能ですか？', 'kato@example.com', 'RESOLVED'
FROM members m WHERE m.email = 'kato@example.com';

-- ============================================
-- 確認用クエリ
-- ============================================
SELECT '===== ダミーデータ投入完了 =====' AS message;

SELECT '会員一覧' AS title;
SELECT id, CONCAT(last_name, ' ', first_name) AS 名前, email, phone, prefecture, newsletter AS メルマガ
FROM members ORDER BY id;

SELECT '会員アレルギー一覧' AS title;
SELECT m.id, CONCAT(m.last_name, ' ', m.first_name) AS 名前, GROUP_CONCAT(ma.allergy) AS アレルギー
FROM members m
LEFT JOIN member_allergies ma ON m.id = ma.member_id
GROUP BY m.id, m.last_name, m.first_name
ORDER BY m.id;

SELECT 'カート内商品一覧' AS title;
SELECT m.id AS 会員ID, CONCAT(m.last_name, ' ', m.first_name) AS 名前, p.name AS 商品名, c.quantity AS 数量
FROM cart_items c
JOIN members m ON c.member_id = m.id
JOIN products p ON c.product_id = p.id
ORDER BY m.id;

SELECT '注文一覧' AS title;
SELECT o.order_number AS 注文番号, CONCAT(m.last_name, ' ', m.first_name) AS 名前, o.total_amount AS 合計, o.status AS ステータス
FROM orders o
JOIN members m ON o.member_id = m.id
ORDER BY o.id;

SELECT 'お問い合わせ一覧' AS title;
SELECT c.id, CONCAT(m.last_name, ' ', m.first_name) AS 名前, c.category AS 種別, c.subject AS 件名, c.status AS ステータス
FROM contacts c
JOIN members m ON c.member_id = m.id
ORDER BY c.id;
