package com.healsweets.repository;

import com.healsweets.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByActiveTrue();
    
    // ID昇順で有効な商品を取得（ユーザー向けページ用）
    List<Product> findByActiveTrueOrderByIdAsc();
    
    // ID昇順で全商品を取得（管理画面用）
    List<Product> findAllByOrderByIdAsc();
    
    // 旧：単一カテゴリー検索（後方互換性のため残す）
    List<Product> findByCategoryAndActiveTrue(String category);
    
    // 新：複数カテゴリー対応 - 指定カテゴリーを持つ商品を検索（ID昇順）
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE c = :category AND p.active = true ORDER BY p.id ASC")
    List<Product> findByCategoriesContainingAndActiveTrue(@Param("category") String category);
    
    // 複数カテゴリーのいずれかを持つ商品を検索（OR検索・ID昇順）
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE c IN :categories AND p.active = true ORDER BY p.id ASC")
    List<Product> findByCategoriesInAndActiveTrueOrderByIdAsc(@Param("categories") List<String> categories);
    
    // 複数カテゴリーをすべて持つ商品を検索（AND検索・ID昇順）
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(SELECT COUNT(DISTINCT c) FROM Product p2 JOIN p2.categories c WHERE p2 = p AND c IN :categories) = :categoryCount " +
           "ORDER BY p.id ASC")
    List<Product> findByCategoriesAllAndActiveTrueOrderByIdAsc(@Param("categories") List<String> categories, @Param("categoryCount") Long categoryCount);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY p.id ASC")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    // 旧：単一カテゴリー複数検索（後方互換性のため残す）
    List<Product> findByCategoryInAndActiveTrue(List<String> categories);
    
    long countByActiveTrue();
}
