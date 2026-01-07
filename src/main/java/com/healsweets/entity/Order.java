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
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal shippingFee;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal totalAmount;

    @Column(length = 50)
    private String couponCode;

    @Column(precision = 10, scale = 0)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    // 配送先情報
    @Column(nullable = false, length = 100)
    private String shippingName;

    @Column(nullable = false, length = 10)
    private String shippingPostalCode;

    @Column(nullable = false, length = 50)
    private String shippingPrefecture;

    @Column(nullable = false, length = 100)
    private String shippingCity;

    @Column(nullable = false, length = 200)
    private String shippingAddress1;

    @Column(length = 200)
    private String shippingAddress2;

    @Column(nullable = false, length = 20)
    private String shippingPhone;

    @Column(length = 50)
    @Builder.Default
    private String deliveryOption = "standard";

    // 支払い情報
    @Column(nullable = false, length = 50)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateOrderNumber() {
        return "HS" + System.currentTimeMillis();
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public String getFormattedTotal() {
        return "¥" + String.format("%,d", totalAmount.intValue());
    }

    public String getFormattedShippingFee() {
        return "¥" + String.format("%,d", shippingFee.intValue());
    }

    public String getFullShippingAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append("〒").append(shippingPostalCode).append("\n");
        sb.append(shippingPrefecture).append(shippingCity).append(shippingAddress1);
        if (shippingAddress2 != null && !shippingAddress2.isEmpty()) {
            sb.append(" ").append(shippingAddress2);
        }
        return sb.toString();
    }

    /**
     * 配送先住所を取得（都道府県、市区町村、番地を結合）
     */
    public String getShippingAddress() {
        StringBuilder sb = new StringBuilder();
        if (shippingPrefecture != null) sb.append(shippingPrefecture);
        if (shippingCity != null) sb.append(shippingCity);
        if (shippingAddress1 != null) sb.append(shippingAddress1);
        if (shippingAddress2 != null && !shippingAddress2.isEmpty()) {
            sb.append(" ").append(shippingAddress2);
        }
        return sb.toString();
    }

    /**
     * 注文内の全商品の合計数量を取得
     */
    public int getTotalItemCount() {
        if (orderItems == null) {
            return 0;
        }
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * 注文明細の種類数を取得
     */
    public int getItemTypeCount() {
        if (orderItems == null) {
            return 0;
        }
        return orderItems.size();
    }
}
