package com.healsweets.controller;

import com.healsweets.dto.ContactDto;
import com.healsweets.dto.MemberUpdateDto;
import com.healsweets.entity.Member;
import com.healsweets.service.ContactService;
import com.healsweets.service.MemberService;
import com.healsweets.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import com.healsweets.entity.Order;
import com.healsweets.entity.OrderStatus;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final OrderService orderService;
    private final ContactService contactService;

    @GetMapping
    public String memberPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false, defaultValue = "info") String section,
            Model model) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        model.addAttribute("member", member);
        model.addAttribute("currentSection", section);
        model.addAttribute("orders", orderService.findByMember(member));
        model.addAttribute("contactDto", new ContactDto());
        
        return "member/mypage";
    }

    @PostMapping("/update")
    public String updateMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute MemberUpdateDto dto,
            RedirectAttributes redirectAttributes) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        memberService.updateMember(member.getId(), dto);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を更新しました。");
        
        return "redirect:/member?section=info";
    }

    @PostMapping("/allergies")
    public String updateAllergies(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) List<String> allergies,
            RedirectAttributes redirectAttributes) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        // allergiesがnullの場合は空のArrayListを渡す（可変リストが必要）
        memberService.updateAllergies(member.getId(), allergies != null ? allergies : new java.util.ArrayList<>());
        redirectAttributes.addFlashAttribute("successMessage", "アレルギー情報を更新しました。");
        
        return "redirect:/member?section=allergy";
    }

    @PostMapping("/contact")
    public String submitContact(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("contactDto") ContactDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            Member member = memberService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
            model.addAttribute("member", member);
            model.addAttribute("currentSection", "contact");
            return "member/mypage";
        }
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        contactService.createContact(member, dto);
        redirectAttributes.addFlashAttribute("successMessage", "お問い合わせを送信しました。");
        
        return "redirect:/member?section=contact";
    }

    @PostMapping("/withdraw")
    public String withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String reason,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        // 退会処理（カートアイテム削除 + 物理削除）
        memberService.deleteMember(member.getId());
        
        // セッション無効化とログアウト
        new SecurityContextLogoutHandler().logout(request, response, 
                SecurityContextHolder.getContext().getAuthentication());
        
        return "redirect:/login?withdraw=true";
    }

    /**
     * 注文キャンセル処理
     * ステータスが「処理中」または「確認済み」の場合のみキャンセル可能
     */
    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        Member member = memberService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("会員情報が見つかりません。"));
        
        Order order = orderService.findById(id)
                .orElseThrow(() -> new RuntimeException("注文が見つかりません。"));
        
        // 注文がログインユーザーのものか確認
        if (!order.getMember().getId().equals(member.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "この注文をキャンセルする権限がありません。");
            return "redirect:/member?section=orders";
        }
        
        // キャンセル可能なステータスか確認（処理中または確認済みのみ）
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            redirectAttributes.addFlashAttribute("errorMessage", "この注文はキャンセルできません。");
            return "redirect:/member?section=orders";
        }
        
        // キャンセル実行
        orderService.updateStatus(id, OrderStatus.CANCELLED);
        redirectAttributes.addFlashAttribute("successMessage", "注文をキャンセルしました。");
        
        return "redirect:/member?section=orders";
    }
}
