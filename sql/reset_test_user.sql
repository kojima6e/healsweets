-- ============================================
-- テストユーザーパスワードリセット
-- パスワード: password123
-- ============================================

USE healsweets;

-- BCrypt形式のパスワードハッシュ（password123）
-- Spring Security互換の$2a$形式
UPDATE members 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMye1xkD8kv5tE1yGYlEHKPcIzuVVb9e4Wy'
WHERE email = 'tanaka@example.com';

-- 確認
SELECT id, email, last_name, first_name, enabled FROM members WHERE email = 'tanaka@example.com';
