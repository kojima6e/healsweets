package com.healsweets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal price;

    // 複数カテゴリー対応（中間テーブル product_categories を使用）
    @ElementCollection
    @CollectionTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category")
    @Builder.Default
    private List<String> categories = new ArrayList<>();

    // 後方互換性のため、単一カテゴリーとしても取得可能
    // ※ 旧categoryカラムは削除後、このメソッドはcategoriesの先頭を返す
    @Column(name = "category", length = 50)
    private String category;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 100)
    private String quantity;

    @Column(length = 50)
    private String expiryDays;

    @Column(length = 200)
    private String storageMethod;

    @ElementCollection
    @CollectionTable(name = "product_allergens", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "allergen")
    @Builder.Default
    private List<String> allergens = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_features", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "feature")
    @Builder.Default
    private List<String> features = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 100;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getFormattedPrice() {
        return "¥" + String.format("%,d", price.intValue());
    }

    /**
     * カテゴリー表示用（複数の場合はカンマ区切り）
     */
    public String getCategoriesDisplay() {
        if (categories == null || categories.isEmpty()) {
            // 後方互換: 旧categoryカラムがあればそれを返す
            return category != null ? getCategoryDisplayName(category) : "";
        }
        return categories.stream()
                .map(this::getCategoryDisplayName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    /**
     * カテゴリーコードを表示名に変換
     */
    private String getCategoryDisplayName(String code) {
        if (code == null) return "";
        switch (code) {
            case "allergen-free": return "アレルゲンフリー";
            case "low-sugar": return "低糖質";
            case "cacao": return "カカオ70%";
            default: return code;
        }
    }

    /**
     * 指定カテゴリーに属するかチェック
     */
    public boolean hasCategory(String categoryCode) {
        if (categories != null && !categories.isEmpty()) {
            return categories.contains(categoryCode);
        }
        // 後方互換: 旧categoryカラムもチェック
        return categoryCode != null && categoryCode.equals(category);
    }
}
