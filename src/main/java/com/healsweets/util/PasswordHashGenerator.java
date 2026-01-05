package com.healsweets.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * パスワードハッシュ生成ユーティリティ
 * 管理者アカウント作成時などに使用
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // admin123のハッシュを生成
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("=== パスワードハッシュ生成 ===");
        System.out.println("元のパスワード: " + password);
        System.out.println("BCryptハッシュ: " + hash);
        System.out.println();
        
        // 検証
        boolean matches = encoder.matches(password, hash);
        System.out.println("検証結果: " + (matches ? "成功" : "失敗"));
        System.out.println();
        
        // SQLサンプル
        System.out.println("=== SQL挿入用 ===");
        System.out.println("INSERT INTO members (last_name, first_name, email, password, ..., role)");
        System.out.println("VALUES ('管理者', 'システム', 'admin@healsweets.com', '" + hash + "', ..., 'ADMIN');");
    }
}
