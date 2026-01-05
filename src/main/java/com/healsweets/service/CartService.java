package com.healsweets.service;

import com.healsweets.entity.CartItem;
import com.healsweets.entity.Member;
import com.healsweets.entity.Product;
import com.healsweets.repository.CartItemRepository;
import com.healsweets.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Member member) {
        return cartItemRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    public CartItem addToCart(Member member, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません。"));

        // 在庫チェック
        if (product.getStock() <= 0) {
            throw new RuntimeException("この商品は売り切れです。");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByMemberAndProduct(member, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            // 在庫数を超えないようにチェック
            if (newQuantity > product.getStock()) {
                throw new RuntimeException("在庫数を超えています。在庫: " + product.getStock() + "個");
            }
            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            // 在庫数を超えないようにチェック
            if (quantity > product.getStock()) {
                throw new RuntimeException("在庫数を超えています。在庫: " + product.getStock() + "個");
            }
            CartItem newItem = CartItem.builder()
                    .member(member)
                    .product(product)
                    .quantity(quantity)
                    .build();
            return cartItemRepository.save(newItem);
        }
    }

    public CartItem updateQuantity(Long cartItemId, int quantity, Member member) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("カートアイテムが見つかりません。"));
        
        // 所有者チェック
        if (!item.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("このカートアイテムを操作する権限がありません。");
        }
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeFromCart(Long cartItemId, Member member) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("カートアイテムが見つかりません。"));
        
        // 所有者チェック
        if (!item.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("このカートアイテムを削除する権限がありません。");
        }
        
        cartItemRepository.delete(item);
    }

    public void clearCart(Member member) {
        cartItemRepository.deleteByMember(member);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCartSubtotal(Member member) {
        return getCartItems(member).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public int getCartItemCount(Member member) {
        return cartItemRepository.countByMember(member);
    }

    public String getFormattedSubtotal(Member member) {
        return "¥" + String.format("%,d", getCartSubtotal(member).intValue());
    }
}
