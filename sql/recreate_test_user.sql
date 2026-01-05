-- ============================================
-- テストユーザー再作成SQL
-- HealSweets
-- ============================================

USE healsweets;

-- 既存のテストユーザーを削除（関連データも含めて）
DELETE FROM member_allergies WHERE member_id = (SELECT id FROM members WHERE email = 'tanaka@example.com');
DELETE FROM cart_items WHERE member_id = (SELECT id FROM members WHERE email = 'tanaka@example.com');
DELETE FROM members WHERE email = 'tanaka@example.com';

-- テストユーザーを新規作成
-- パスワード: password123
-- このハッシュはSpring SecurityのBCryptPasswordEncoderで生成されたものと互換性があります
INSERT INTO members (
    last_name, 
    first_name, 
    email, 
    password, 
    phone, 
    birth_date, 
    postal_code, 
    prefecture, 
    city, 
    address1, 
    address2, 
    newsletter, 
    enabled,
    created_at,
    updated_at
) VALUES (
    '田中', 
    '太郎', 
    'tanaka@example.com', 
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    '090-1234-5678', 
    '1990-01-01', 
    '123-4567', 
    '東京都', 
    '渋谷区', 
    '○○ 1-2-3', 
    '○○マンション 101号室', 
    TRUE, 
    TRUE,
    NOW(),
    NOW()
);

-- 作成したユーザーのIDを取得してアレルギー情報を追加
SET @member_id = LAST_INSERT_ID();

INSERT INTO member_allergies (member_id, allergy) VALUES 
(@member_id, '小麦'),
(@member_id, '乳製品');

-- 確認
SELECT '=== テストユーザー作成完了 ===' AS message;
SELECT id, email, last_name, first_name, enabled, created_at FROM members WHERE email = 'tanaka@example.com';
SELECT * FROM member_allergies WHERE member_id = @member_id;

-- ============================================
-- ログイン情報
-- Email: tanaka@example.com
-- Password: password123
-- ============================================
