package com.healsweets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRegistrationDto {

    @NotBlank(message = "姓を入力してください")
    private String lastName;

    @NotBlank(message = "名を入力してください")
    private String firstName;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;

    @NotBlank(message = "確認用パスワードを入力してください")
    private String passwordConfirm;

    @NotBlank(message = "電話番号を入力してください")
    private String phone;

    private LocalDate birthDate;

    @NotBlank(message = "郵便番号を入力してください")
    private String postalCode;

    @NotBlank(message = "都道府県を選択してください")
    private String prefecture;

    @NotBlank(message = "市区町村を入力してください")
    private String city;

    @NotBlank(message = "番地を入力してください")
    private String address1;

    private String address2;

    private List<String> allergies;

    private Boolean newsletter;

    private Boolean agreeTerms;

    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}
