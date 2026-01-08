package com.healsweets.service;

import com.healsweets.dto.CheckoutDto;
import com.healsweets.entity.*;
import com.healsweets.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ShippingService shippingService;
    private final ProductService productService;

    public Order createOrder(Member member, CheckoutDto dto) {
        List<CartItem> cartItems = cartService.getCartItems(member);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("カートが空です。");
        }

        // 在庫チェック
        for (CartItem cartItem : cartItems) {
            if (!productService.hasStock(cartItem.getProduct().getId(), cartItem.getQuantity())) {
                throw new RuntimeException("在庫が不足しています: " + cartItem.getProduct().getName());
            }
        }

        BigDecimal subtotal = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = shippingService.calculateShipping(dto.getDeliveryOption());

        BigDecimal totalAmount = subtotal.add(shippingFee);

        Order order = Order.builder()
                .member(member)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .totalAmount(totalAmount)
                .shippingName(dto.getShippingName())
                .shippingPostalCode(dto.getShippingPostalCode())
                .shippingPrefecture(dto.getShippingPrefecture())
                .shippingCity(dto.getShippingCity())
                .shippingAddress1(dto.getShippingAddress1())
                .shippingAddress2(dto.getShippingAddress2())
                .shippingPhone(dto.getShippingPhone())
                .deliveryOption(dto.getDeliveryOption())
                .paymentMethod(dto.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .build();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .productName(cartItem.getProduct().getName())
                    .unitPrice(cartItem.getProduct().getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(cartItem.getSubtotal())
                    .build();
            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // 在庫を減らす
        for (CartItem cartItem : cartItems) {
            productService.reduceStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }

        // カートをクリア
        cartService.clearCart(member);

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> findByMember(Member member) {
        return orderRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("注文が見つかりません。"));
        
        OrderStatus previousStatus = order.getStatus();
        
        // キャンセルされた場合、在庫を戻す
        // ただし、既にキャンセル済みの場合は二重に戻さないようにする
        if (status == OrderStatus.CANCELLED && previousStatus != OrderStatus.CANCELLED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                productService.restoreStock(
                    orderItem.getProduct().getId(), 
                    orderItem.getQuantity()
                );
            }
        }
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
