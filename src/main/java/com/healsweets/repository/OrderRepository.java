package com.healsweets.repository;

import com.healsweets.entity.Member;
import com.healsweets.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByMemberOrderByCreatedAtDesc(Member member);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findAllByOrderByCreatedAtDesc();
    
    List<Order> findTop10ByOrderByCreatedAtDesc();
    
    long countByCreatedAtAfter(LocalDateTime date);
    
    List<Order> findByCreatedAtAfter(LocalDateTime date);

    // 注文と注文明細を一緒に取得（N+1問題回避）
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems LEFT JOIN FETCH o.member ORDER BY o.createdAt DESC")
    List<Order> findAllWithOrderItems();

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems LEFT JOIN FETCH o.member ORDER BY o.createdAt DESC LIMIT 10")
    List<Order> findTop10WithOrderItems();
}
