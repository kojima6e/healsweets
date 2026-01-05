package com.healsweets.controller;

import com.healsweets.entity.CartItem;
import com.healsweets.entity.Member;
import com.healsweets.service.CartService;
import com.healsweets.service.MemberService;
import com.healsweets.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;
    private final ShippingService shippingService;

    @GetMapping
    public String viewCart(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        List<CartItem> cartItems = cartService.getCartItems(member);
        BigDecimal subtotal = cartService.getCartSubtotal(member);
        BigDecimal shipping = cartItems.isEmpty() ? BigDecimal.ZERO : shippingService.getStandardShipping();
        BigDecimal total = subtotal.add(shipping);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", total);
        
        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            RedirectAttributes redirectAttributes) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        cartService.addToCart(member, productId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "カートに追加しました。");
        
        return "redirect:/cart";
    }

    @PostMapping("/update/{id}")
    public String updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam int quantity,
            RedirectAttributes redirectAttributes) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        cartService.updateQuantity(id, quantity, member);
        redirectAttributes.addFlashAttribute("successMessage", "数量を更新しました。");
        
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        cartService.removeFromCart(id, member);
        redirectAttributes.addFlashAttribute("successMessage", "カートから削除しました。");
        
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        cartService.clearCart(member);
        redirectAttributes.addFlashAttribute("successMessage", "カートをクリアしました。");
        
        return "redirect:/cart";
    }
}
