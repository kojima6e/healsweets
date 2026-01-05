package com.healsweets.controller;

import com.healsweets.dto.CheckoutDto;
import com.healsweets.entity.CartItem;
import com.healsweets.entity.Member;
import com.healsweets.entity.Order;
import com.healsweets.service.CartService;
import com.healsweets.service.MemberService;
import com.healsweets.service.OrderService;
import com.healsweets.service.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final ShippingService shippingService;

    @GetMapping
    public String checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        List<CartItem> cartItems = cartService.getCartItems(member);
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        BigDecimal subtotal = cartService.getCartSubtotal(member);
        BigDecimal shipping = shippingService.getStandardShipping();
        BigDecimal total = subtotal.add(shipping);
        
        // 既存住所をDTOにセット
        CheckoutDto checkoutDto = CheckoutDto.builder()
                .shippingName(member.getFullName())
                .shippingPostalCode(member.getPostalCode())
                .shippingPrefecture(member.getPrefecture())
                .shippingCity(member.getCity())
                .shippingAddress1(member.getAddress1())
                .shippingAddress2(member.getAddress2())
                .shippingPhone(member.getPhone())
                .deliveryOption("standard")
                .paymentMethod("credit")
                .build();
        
        model.addAttribute("member", member);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("checkoutDto", checkoutDto);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", total);
        model.addAttribute("currentStep", 1);
        
        return "checkout/checkout";
    }

    @PostMapping("/confirm")
    public String confirmOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("checkoutDto") CheckoutDto dto,
            BindingResult result,
            Model model) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        List<CartItem> cartItems = cartService.getCartItems(member);
        BigDecimal subtotal = cartService.getCartSubtotal(member);
        BigDecimal shipping = shippingService.calculateShipping(dto.getDeliveryOption());
        BigDecimal total = subtotal.add(shipping);
        
        if (result.hasErrors()) {
            model.addAttribute("member", member);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shipping", shipping);
            model.addAttribute("total", total);
            model.addAttribute("currentStep", 1);
            return "checkout/checkout";
        }
        
        model.addAttribute("member", member);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("checkoutDto", dto);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", total);
        model.addAttribute("currentStep", 3);
        
        return "checkout/checkout";
    }

    @PostMapping("/complete")
    public String completeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute("checkoutDto") CheckoutDto dto,
            RedirectAttributes redirectAttributes) {
        
        if (dto.getAgreeTerms() == null || !dto.getAgreeTerms()) {
            redirectAttributes.addFlashAttribute("errorMessage", "利用規約に同意してください。");
            return "redirect:/checkout";
        }
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        try {
            Order order = orderService.createOrder(member, dto);
            redirectAttributes.addFlashAttribute("orderNumber", order.getOrderNumber());
            redirectAttributes.addFlashAttribute("successMessage", "ご注文ありがとうございました！");
            return "redirect:/checkout/complete";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "注文処理中にエラーが発生しました: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/complete")
    public String orderComplete(Model model) {
        return "checkout/complete";
    }
}
