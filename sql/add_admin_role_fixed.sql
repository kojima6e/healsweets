-- ============================================
-- 管理者機能追加のためのDB修正SQL（修正版）
-- ============================================

USE healsweets;

-- Memberテーブルにroleカラムを追加（既に存在する場合はエラーを無視）
ALTER TABLE members 
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER enabled;

-- 既存データを更新（最初の会員を管理者に設定）
UPDATE members SET role = 'ADMIN' WHERE id = 1;

-- インデックス追加（既に存在する場合はエラーを無視）
CREATE INDEX idx_members_role ON members(role);

-- 確認
SELECT id, email, role, enabled FROM members;

-- ============================================
-- 既存の管理者アカウントを削除（存在する場合）
-- ============================================
DELETE FROM members WHERE email = 'admin@healsweets.com';

-- ============================================
-- テスト用管理者アカウント追加
-- email: admin@healsweets.com
-- password: admin123
-- BCryptハッシュ: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- ============================================
INSERT INTO members (last_name, first_name, email, password, phone, postal_code, prefecture, city, address1, newsletter, enabled, role) VALUES
('管理者', 'システム', 'admin@healsweets.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '090-0000-0000', '100-0001', '東京都', '千代田区', '丸の内1-1-1', FALSE, TRUE, 'ADMIN');

-- 最終確認
SELECT id, email, role, enabled, created_at FROM members WHERE role = 'ADMIN';

SELECT 'DB修正完了' AS message;
SELECT '管理者アカウント: admin@healsweets.com / admin123' AS login_info;
