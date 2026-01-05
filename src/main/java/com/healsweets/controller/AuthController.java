package com.healsweets.controller;

import com.healsweets.dto.MemberRegistrationDto;
import com.healsweets.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("memberDto", new MemberRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("memberDto") MemberRegistrationDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // パスワード一致チェック
        if (!dto.isPasswordMatching()) {
            result.rejectValue("passwordConfirm", "error.passwordConfirm", "パスワードが一致しません");
        }

        // メールアドレス重複チェック
        if (memberService.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "error.email", "このメールアドレスは既に登録されています");
        }

        // 利用規約同意チェック
        if (dto.getAgreeTerms() == null || !dto.getAgreeTerms()) {
            result.rejectValue("agreeTerms", "error.agreeTerms", "利用規約に同意してください");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            memberService.register(dto);
            redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。ログインしてください。");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "登録中にエラーが発生しました: " + e.getMessage());
            return "auth/register";
        }
    }
}
