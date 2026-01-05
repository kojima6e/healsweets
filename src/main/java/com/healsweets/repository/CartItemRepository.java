package com.healsweets.repository;

import com.healsweets.entity.CartItem;
import com.healsweets.entity.Member;
import com.healsweets.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByMember(Member member);
    
    List<CartItem> findByMemberOrderByCreatedAtDesc(Member member);
    
    Optional<CartItem> findByMemberAndProduct(Member member, Product product);
    
    void deleteByMember(Member member);
    
    int countByMember(Member member);
}
