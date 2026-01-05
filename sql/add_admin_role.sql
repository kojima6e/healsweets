-- ============================================
-- 管理者機能追加のためのDB修正SQL
-- ============================================

USE healsweets;

-- Memberテーブルにroleカラムを追加
ALTER TABLE members 
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER enabled;

-- 既存データを更新（最初の会員を管理者に設定）
UPDATE members SET role = 'ADMIN' WHERE id = 1;

-- インデックス追加
CREATE INDEX idx_members_role ON members(role);

-- 確認
SELECT id, email, role, enabled FROM members;

-- ============================================
-- テスト用管理者アカウント追加
-- email: admin@healsweets.com
-- password: admin123
-- ============================================
INSERT INTO members (last_name, first_name, email, password, phone, postal_code, prefecture, city, address1, newsletter, enabled, role) VALUES
('管理者', 'システム', 'admin@healsweets.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1xkD8kv5tE1yGYlEHKPcIzuVVb9e4Wy', '090-0000-0000', '100-0001', '東京都', '千代田区', '丸の内1-1-1', FALSE, TRUE, 'ADMIN');

SELECT 'DB修正完了' AS message;
