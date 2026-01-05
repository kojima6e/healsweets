-- ============================================
-- 簡単な解決方法：既存会員を管理者に昇格
-- ============================================

USE healsweets;

-- 方法1: 既存のtanaka@example.comを管理者に昇格
-- このアカウントでログイン可能: tanaka@example.com / password123
UPDATE members SET role = 'ADMIN' WHERE email = 'tanaka@example.com';

-- 確認
SELECT id, email, role, enabled FROM members WHERE email = 'tanaka@example.com';

SELECT '既存アカウントを管理者に変更しました' AS message;
SELECT 'ログイン: tanaka@example.com / password123' AS login_info;
