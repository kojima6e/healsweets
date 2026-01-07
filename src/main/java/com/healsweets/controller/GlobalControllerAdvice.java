package com.healsweets.controller;

import com.healsweets.entity.Member;
import com.healsweets.service.CartService;
import com.healsweets.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CartService cartService;
    private final MemberService memberService;

    /**
     * 全ページでカート内の商品数を取得
     */
    @ModelAttribute("cartItemCount")
    public int getCartItemCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            return memberService.findByEmail(email)
                    .map(member -> cartService.getCartItemCount(member))
                    .orElse(0);
        }
        
        return 0;
    }
}
