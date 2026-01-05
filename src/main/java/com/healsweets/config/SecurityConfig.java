package com.healsweets.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 公開ページ
                .requestMatchers("/", "/index", "/products/**", "/search").permitAll()
                .requestMatchers("/auth/**", "/login", "/register", "/admin/login").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // 管理者専用ページ
                .requestMatchers("/admin/**").authenticated()
                // 認証が必要なページ
                .requestMatchers("/member/**", "/cart/**", "/checkout/**", "/orders/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    String referer = request.getHeader("Referer");
                    if (referer != null && referer.contains("/admin")) {
                        response.sendRedirect("/admin/login?logout=true");
                    } else {
                        response.sendRedirect("/login?logout=true");
                    }
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    String requestURI = request.getRequestURI();
                    // /admin配下へのアクセスは管理者ログインページへリダイレクト
                    if (requestURI != null && requestURI.startsWith("/admin") && !requestURI.equals("/admin/login")) {
                        // 元のURLを保存してログイン後にリダイレクト
                        request.getSession().setAttribute("SPRING_SECURITY_SAVED_REQUEST", requestURI);
                        response.sendRedirect("/admin/login");
                    } else {
                        response.sendRedirect("/login");
                    }
                })
            );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String referer = request.getHeader("Referer");
            String adminLogin = request.getParameter("adminLogin");
            
            // セッションに保存されたURLがあればそこへリダイレクト
            Object savedRequest = request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            if (savedRequest != null) {
                String savedUrl = savedRequest.toString();
                request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
                response.sendRedirect(savedUrl);
                return;
            }
            
            // 管理者ログインページからのログインの場合
            if ((referer != null && referer.contains("/admin/login")) || "true".equals(adminLogin)) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/member");
            }
        };
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, 
                                               jakarta.servlet.http.HttpServletResponse response, 
                                               org.springframework.security.core.AuthenticationException exception) 
                                               throws java.io.IOException {
                String referer = request.getHeader("Referer");
                // 管理者ログインページからのログイン失敗
                if (referer != null && referer.contains("/admin/login")) {
                    response.sendRedirect("/admin/login?error=true");
                } else {
                    response.sendRedirect("/login?error=true");
                }
            }
        };
    }
}
