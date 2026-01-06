package com.healsweets.service;

import com.healsweets.dto.ProductAdminDto;
import com.healsweets.entity.Member;
import com.healsweets.entity.Order;
import com.healsweets.entity.OrderStatus;
import com.healsweets.entity.Product;
import com.healsweets.repository.MemberRepository;
import com.healsweets.repository.OrderRepository;
import com.healsweets.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // ====== 会員管理 ======

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Member> getActiveMembers() {
        return memberRepository.findByEnabledTrue();
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会員が見つかりません"));
        member.setEnabled(false);
        memberRepository.save(member);
    }

    @Transactional
    public void restoreMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会員が見つかりません"));
        member.setEnabled(true);
        memberRepository.save(member);
    }

    // ====== 商品管理 ======

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
    }

    @Transactional
    public Product createProduct(ProductAdminDto dto) {
        // 空の値を除外したリストを作成
        List<String> categories = filterEmptyStrings(dto.getCategories());
        List<String> allergens = filterEmptyStrings(dto.getAllergens());
        List<String> features = filterEmptyStrings(dto.getFeatures());

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .categories(categories)
                .imageUrl(dto.getImageUrl())
                .quantity(dto.getQuantity())
                .expiryDays(dto.getExpiryDays())
                .storageMethod(dto.getStorageMethod())
                .allergens(allergens)
                .features(features)
                .stock(dto.getStock())
                .active(dto.getActive())
                .build();

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductAdminDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setQuantity(dto.getQuantity());
        product.setExpiryDays(dto.getExpiryDays());
        product.setStorageMethod(dto.getStorageMethod());
        product.setStock(dto.getStock());
        product.setActive(dto.getActive());

        // ElementCollectionの更新: カテゴリー（空の値を除外）
        product.getCategories().clear();
        product.getCategories().addAll(filterEmptyStrings(dto.getCategories()));

        // ElementCollectionの更新: アレルゲン（空の値を除外）
        product.getAllergens().clear();
        product.getAllergens().addAll(filterEmptyStrings(dto.getAllergens()));

        // ElementCollectionの更新: 特徴（空の値を除外）
        product.getFeatures().clear();
        product.getFeatures().addAll(filterEmptyStrings(dto.getFeatures()));

        return productRepository.save(product);
    }

    /**
     * リストから空文字・null・空白のみの値を除外
     */
    private List<String> filterEmptyStrings(List<String> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
        product.setActive(true);
        productRepository.save(product);
    }

    // ====== 注文管理 ======

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithOrderItems();
    }

    @Transactional(readOnly = true)
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findTop10WithOrderItems();
    }

    /**
     * 注文ステータスを更新
     * キャンセル時は在庫を戻す
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("注文が見つかりません"));

        OrderStatus oldStatus = order.getStatus();

        // キャンセルへの変更時は在庫を戻す
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        // キャンセルから他のステータスへの変更時は在庫を減らす
        if (oldStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            reduceStock(order);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    /**
     * 注文の在庫を戻す
     */
    private void restoreStock(Order order) {
        for (var item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }

    /**
     * 注文の在庫を減らす
     */
    private void reduceStock(Order order) {
        for (var item : order.getOrderItems()) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("在庫が不足しています: " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
    }

    // ====== ダッシュボード統計 ======

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 会員統計
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByEnabledTrue();

        // 商品統計
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByActiveTrue();

        // 注文統計
        long totalOrders = orderRepository.count();

        // 今日の注文
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayOrders = orderRepository.countByCreatedAtAfter(startOfDay);

        // 今月の売上（キャンセル分は除く）
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Order> monthOrders = orderRepository.findByCreatedAtAfter(startOfMonth);
        BigDecimal monthlyRevenue = monthOrders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)  // キャンセル分を除外
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalMembers", totalMembers);
        stats.put("activeMembers", activeMembers);
        stats.put("totalProducts", totalProducts);
        stats.put("activeProducts", activeProducts);
        stats.put("totalOrders", totalOrders);
        stats.put("todayOrders", todayOrders);
        stats.put("monthlyRevenue", monthlyRevenue);

        return stats;
    }
}
