package com.healsweets.service;

import com.healsweets.entity.Product;
import com.healsweets.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 在庫を減らす
     */
    @Transactional
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません。"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("在庫が不足しています: " + product.getName());
        }
        
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    /**
     * 在庫を戻す（キャンセル時など）
     */
    @Transactional
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません。"));
        
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    /**
     * 在庫があるかチェック
     */
    public boolean hasStock(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(p -> p.getStock() >= quantity)
                .orElse(false);
    }

    /**
     * 売り切れかどうか
     */
    public boolean isSoldOut(Product product) {
        return product.getStock() <= 0;
    }

    public List<Product> findAllActive() {
        return productRepository.findByActiveTrueOrderByIdAsc();
    }

    /**
     * カテゴリーで商品を検索（複数カテゴリー対応）
     * まず新しいcategoriesコレクションで検索し、結果がなければ旧categoryカラムでも検索
     */
    public List<Product> findByCategory(String category) {
        if (category == null || category.isEmpty() || "all".equalsIgnoreCase(category)) {
            return findAllActive();
        }
        
        // 新しい複数カテゴリーテーブルで検索
        List<Product> products = productRepository.findByCategoriesContainingAndActiveTrue(category);
        
        // 結果がなければ旧categoryカラムでも検索（後方互換性）
        if (products.isEmpty()) {
            products = productRepository.findByCategoryAndActiveTrue(category);
        }
        
        return products;
    }

    /**
     * 複数カテゴリーで商品を検索（AND検索）
     * 指定されたカテゴリーをすべて持つ商品を返す
     */
    public List<Product> findByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return findAllActive();
        }
        
        return productRepository.findByCategoriesAllAndActiveTrueOrderByIdAsc(categories, (long) categories.size());
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllActive();
        }
        return productRepository.searchProducts(keyword.trim());
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
