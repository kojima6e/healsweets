-- ============================================
-- HealSweets データベース初期化SQL
-- MySQL 8.0用
-- ============================================

-- データベース作成
DROP DATABASE IF EXISTS healsweets;
CREATE DATABASE healsweets DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE healsweets;

-- ============================================
-- テーブル作成
-- ============================================

-- 会員テーブル
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    birth_date DATE,
    postal_code VARCHAR(10),
    prefecture VARCHAR(50),
    city VARCHAR(100),
    address1 VARCHAR(200),
    address2 VARCHAR(200),
    newsletter BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_members_email (email),
    INDEX idx_members_enabled (enabled)
) ENGINE=InnoDB;

-- 会員アレルギーテーブル（ElementCollection用）
CREATE TABLE member_allergies (
    member_id BIGINT NOT NULL,
    allergy VARCHAR(255) NOT NULL,
    PRIMARY KEY (member_id, allergy),
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 商品テーブル
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    price DECIMAL(10, 0) NOT NULL,
    category VARCHAR(50),
    image_url VARCHAR(500),
    quantity VARCHAR(100),
    expiry_days VARCHAR(50),
    storage_method VARCHAR(200),
    stock INT NOT NULL DEFAULT 100,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_products_category (category),
    INDEX idx_products_active (active)
) ENGINE=InnoDB;

-- 商品アレルゲンテーブル（ElementCollection用）
CREATE TABLE product_allergens (
    product_id BIGINT NOT NULL,
    allergen VARCHAR(255) NOT NULL,
    PRIMARY KEY (product_id, allergen),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 商品特徴テーブル（ElementCollection用）
CREATE TABLE product_features (
    product_id BIGINT NOT NULL,
    feature VARCHAR(255) NOT NULL,
    PRIMARY KEY (product_id, feature),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- カートアイテムテーブル
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_cart_items_member (member_id)
) ENGINE=InnoDB;

-- 注文テーブル
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    subtotal DECIMAL(10, 0) NOT NULL,
    shipping_fee DECIMAL(10, 0) NOT NULL,
    total_amount DECIMAL(10, 0) NOT NULL,
    coupon_code VARCHAR(50),
    discount DECIMAL(10, 0) DEFAULT 0,
    shipping_name VARCHAR(100) NOT NULL,
    shipping_postal_code VARCHAR(10) NOT NULL,
    shipping_prefecture VARCHAR(50) NOT NULL,
    shipping_city VARCHAR(100) NOT NULL,
    shipping_address1 VARCHAR(200) NOT NULL,
    shipping_address2 VARCHAR(200),
    shipping_phone VARCHAR(20) NOT NULL,
    delivery_option VARCHAR(50) DEFAULT 'standard',
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id),
    INDEX idx_orders_member (member_id),
    INDEX idx_orders_status (status),
    INDEX idx_orders_order_number (order_number)
) ENGINE=InnoDB;

-- 注文アイテムテーブル
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    unit_price DECIMAL(10, 0) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 0) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_order_items_order (order_id)
) ENGINE=InnoDB;

-- お問い合わせテーブル
CREATE TABLE contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    category VARCHAR(100) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    email VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE SET NULL,
    INDEX idx_contacts_status (status)
) ENGINE=InnoDB;

-- ============================================
-- 初期データ投入
-- ============================================

-- テスト会員データ（パスワード: password123）
-- BCryptでエンコード済み（$2a$形式 - Spring Security互換）
INSERT INTO members (last_name, first_name, email, password, phone, birth_date, postal_code, prefecture, city, address1, address2, newsletter, enabled) VALUES
('田中', '太郎', 'tanaka@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1xkD8kv5tE1yGYlEHKPcIzuVVb9e4Wy', '090-1234-5678', '1990-01-01', '123-4567', '東京都', '渋谷区', '○○ 1-2-3', '○○マンション 101号室', TRUE, TRUE);

-- テスト会員のアレルギー
INSERT INTO member_allergies (member_id, allergy) VALUES
(1, '小麦'),
(1, '乳製品');

-- 商品データ
INSERT INTO products (name, description, price, category, image_url, quantity, expiry_days, storage_method, stock) VALUES
('ミックスナッツ スイーツボックス', '厳選されたナッツを使用したアレルゲンフリーのスイーツボックス。自然な甘さとナッツの香ばしさが楽しめます。', 540, 'allergen-free', '/images/item01.jpg', '8個入り', '製造日より30日', '直射日光・高温多湿を避けて保存', 100),
('グルテンフリー クッキーボックス', '米粉を使用したグルテンフリーのクッキー。サクサクの食感とやさしい甘さが特徴です。', 780, 'allergen-free', '/images/item02.jpg', '12枚入り', '製造日より45日', '直射日光・高温多湿を避けて保存', 100),
('アレルゲンフリー スナックパック', '特定原材料28品目不使用のスナックパック。お子様にも安心してお召し上がりいただけます。', 690, 'allergen-free', '/images/item03.jpg', '6袋入り', '製造日より60日', '直射日光・高温多湿を避けて保存', 100),
('低糖質 グルメクッキー', '糖質を抑えながらも、深いコクと香ばしさを実現した本格派クッキーです。厳選されたアーモンドプードルとバターを使用し、一枚一枚丁寧に焼き上げました。', 620, 'low-sugar', '/images/item04.jpg', '12枚入り（約200g）', '製造日より60日', '直射日光・高温多湿を避けて保存', 100),
('低糖質 マカロンギフト', '糖質を大幅カットしながらも、サクッとした外側としっとりした中身の絶妙な食感を実現。', 750, 'low-sugar', '/images/item05.jpg', '8個入り', '製造日より14日', '要冷蔵（10℃以下）', 100),
('低糖質 フレッシュフルーツタルト', '新鮮なフルーツをたっぷり使用した低糖質タルト。甘さ控えめでフルーツの自然な甘みを楽しめます。', 710, 'low-sugar', '/images/item06.jpg', '1ホール（4号サイズ）', '製造日より3日', '要冷蔵（10℃以下）', 100),
('カカオ70% リッチチョコレートケーキ', 'カカオ70%の上質なチョコレートを贅沢に使用。濃厚でありながら後味すっきりの大人の味わい。', 560, 'cacao', '/images/item07.jpg', '1ホール（5号サイズ）', '製造日より5日', '要冷蔵（10℃以下）', 100),
('カカオ70% プレミアムブラウニー', 'しっとり濃厚なブラウニー。カカオ70%のビターな味わいとナッツの食感が絶妙にマッチ。', 990, 'cacao', '/images/item08.jpg', '6個入り', '製造日より10日', '直射日光・高温多湿を避けて保存', 100),
('カカオ70% トリュフコレクション', '口の中でとろけるなめらかな食感。カカオ70%の深い味わいを堪能できる贅沢なトリュフ。', 770, 'cacao', '/images/item09.jpg', '9個入り', '製造日より21日', '直射日光・高温多湿を避けて保存', 100),
('カカオ70% 職人のボンボンショコラ', '一つ一つ職人が手作りした本格ボンボンショコラ。様々なフレーバーをお楽しみいただけます。', 620, 'cacao', '/images/item10.jpg', '12個入り', '製造日より30日', '直射日光・高温多湿を避けて保存', 100);

-- 商品アレルゲンデータ
INSERT INTO product_allergens (product_id, allergen) VALUES
(1, 'ナッツ類'),
(4, '小麦'), (4, '卵'), (4, '乳製品'), (4, 'アーモンド'),
(5, '卵'), (5, '乳製品'), (5, 'アーモンド'),
(6, '小麦'), (6, '卵'), (6, '乳製品'),
(7, '小麦'), (7, '卵'), (7, '乳製品'),
(8, '小麦'), (8, '卵'), (8, '乳製品'), (8, 'くるみ'),
(9, '乳製品'),
(10, '乳製品'), (10, 'アーモンド');

-- 商品特徴データ
INSERT INTO product_features (product_id, feature) VALUES
(1, '✓ グルテンフリー'), (1, '✓ 卵不使用'), (1, '✓ 乳製品不使用'), (1, '✓ 添加物不使用'),
(2, '✓ グルテンフリー'), (2, '✓ 卵不使用'), (2, '✓ 国産米粉100%'), (2, '✓ 添加物不使用'),
(3, '✓ 28品目不使用'), (3, '✓ 添加物不使用'), (3, '✓ 国産素材使用'), (3, '✓ 個包装'),
(4, '✓ 糖質70%カット（当社比）'), (4, '✓ 添加物不使用'), (4, '✓ 国産素材使用'), (4, '✓ 個包装で保存しやすい'),
(5, '✓ 糖質60%カット'), (5, '✓ 天然素材使用'), (5, '✓ 着色料不使用'), (5, '✓ ギフトボックス付き'),
(6, '✓ 糖質50%カット'), (6, '✓ 季節のフルーツ使用'), (6, '✓ 無添加クリーム'), (6, '✓ 低GI'),
(7, '✓ カカオ70%使用'), (7, '✓ ベルギー産チョコレート'), (7, '✓ ポリフェノール豊富'), (7, '✓ 甘さ控えめ'),
(8, '✓ カカオ70%使用'), (8, '✓ くるみたっぷり'), (8, '✓ 個包装'), (8, '✓ ギフト対応'),
(9, '✓ カカオ70%使用'), (9, '✓ 3種のフレーバー'), (9, '✓ ギフトボックス付き'), (9, '✓ 手作り'),
(10, '✓ カカオ70%使用'), (10, '✓ 職人手作り'), (10, '✓ 6種のフレーバー'), (10, '✓ ギフトボックス付き');

-- ============================================
-- 確認用クエリ
-- ============================================
SELECT 'データベース初期化完了' AS message;
SELECT COUNT(*) AS '会員数' FROM members;
SELECT COUNT(*) AS '商品数' FROM products;
