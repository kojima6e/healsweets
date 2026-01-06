package com.healsweets.controller;

import com.healsweets.dto.ProductAdminDto;
import com.healsweets.entity.Member;
import com.healsweets.entity.Product;
import com.healsweets.service.AdminService;
import com.healsweets.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    // ====== 管理者ログインページ ======

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    // 管理者権限チェック
    private boolean checkAdminRole(UserDetails userDetails) {
        return memberService.findByEmail(userDetails.getUsername())
                .map(Member::isAdmin)
                .orElse(false);
    }

    // ====== ダッシュボード ======

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        Map<String, Object> stats = adminService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("recentOrders", adminService.getRecentOrders(10));

        return "admin/dashboard";
    }

    // ====== 会員管理 ======

    @GetMapping("/members")
    public String memberList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        model.addAttribute("members", adminService.getAllMembers());
        return "admin/member-list";
    }

    @PostMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        try {
            adminService.deleteMember(id);
            redirectAttributes.addFlashAttribute("successMessage", "会員を無効化しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "会員の無効化に失敗しました: " + e.getMessage());
        }

        return "redirect:/admin/members";
    }

    @PostMapping("/members/{id}/restore")
    public String restoreMember(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        try {
            adminService.restoreMember(id);
            redirectAttributes.addFlashAttribute("successMessage", "会員を復元しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "会員の復元に失敗しました: " + e.getMessage());
        }

        return "redirect:/admin/members";
    }

    // ====== 商品管理 ======

    @GetMapping("/products")
    public String productList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        model.addAttribute("products", adminService.getAllProducts());
        return "admin/product-list";
    }

    @GetMapping("/products/new")
    public String newProductForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        model.addAttribute("productDto", new ProductAdminDto());
        model.addAttribute("isEdit", false);
        return "admin/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        Product product = adminService.getProductById(id);

        ProductAdminDto dto = ProductAdminDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categories(product.getCategories())
                .imageUrl(product.getImageUrl())
                .quantity(product.getQuantity())
                .expiryDays(product.getExpiryDays())
                .storageMethod(product.getStorageMethod())
                .allergens(product.getAllergens())
                .features(product.getFeatures())
                .stock(product.getStock())
                .active(product.getActive())
                .build();

        model.addAttribute("productDto", dto);
        model.addAttribute("isEdit", true);
        return "admin/product-form";
    }

    @PostMapping("/products/new")
    public String createProduct(@Valid @ModelAttribute ProductAdminDto productDto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/product-form";
        }

        try {
            adminService.createProduct(productDto);
            redirectAttributes.addFlashAttribute("successMessage", "商品を追加しました");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "商品の追加に失敗しました: " + e.getMessage());
            model.addAttribute("isEdit", false);
            return "admin/product-form";
        }
    }

    @PostMapping("/products/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                               @Valid @ModelAttribute ProductAdminDto productDto,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            productDto.setId(id);
            return "admin/product-form";
        }

        try {
            adminService.updateProduct(id, productDto);
            redirectAttributes.addFlashAttribute("successMessage", "商品を更新しました");
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "商品の更新に失敗しました: " + e.getMessage());
            model.addAttribute("isEdit", true);
            productDto.setId(id);
            return "admin/product-form";
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        try {
            adminService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "商品を無効化しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "商品の無効化に失敗しました: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/activate")
    public String activateProduct(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        try {
            adminService.activateProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "商品を有効化しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "商品の有効化に失敗しました: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // ====== 注文管理 ======

    @GetMapping("/orders")
    public String orderList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        model.addAttribute("orders", adminService.getAllOrders());
        model.addAttribute("statuses", com.healsweets.entity.OrderStatus.values());
        return "admin/order-list";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") String status,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        if (!checkAdminRole(userDetails)) {
            return "redirect:/";
        }

        try {
            com.healsweets.entity.OrderStatus newStatus = com.healsweets.entity.OrderStatus.valueOf(status);
            adminService.updateOrderStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "注文ステータスを更新しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ステータスの更新に失敗しました: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }
}
