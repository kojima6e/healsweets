package com.healsweets.controller;

import com.healsweets.entity.Product;
import com.healsweets.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping({"/", "/index"})
    public String index(
            @RequestParam(required = false, defaultValue = "all") String category,
            Model model) {
        
        List<Product> products = productService.findByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("currentCategory", category);
        
        return "index";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,
            Model model) {
        
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentCategory", "all");
        
        return "index";
    }
}
