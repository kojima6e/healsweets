package com.healsweets.controller;

import com.healsweets.entity.Product;
import com.healsweets.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping({"/", "/index"})
    public String index(
            @RequestParam(required = false) List<String> categories,
            Model model) {
        
        // カテゴリーが未指定または空の場合は全商品
        if (categories == null || categories.isEmpty() || categories.contains("all")) {
            categories = new ArrayList<>();
        }
        
        List<Product> products = productService.findByCategories(categories);
        model.addAttribute("products", products);
        model.addAttribute("selectedCategories", categories);
        
        return "index";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            Model model) {
        
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategories", new ArrayList<String>());
        
        return "index";
    }
}
