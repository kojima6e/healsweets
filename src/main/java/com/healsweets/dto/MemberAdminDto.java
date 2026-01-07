package com.healsweets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAdminDto {

    private Long id;

    @NotBlank(message = "姓を入力してください")
    @Size(max = 50, message = "姓は50文字以内で入力してください")
    private String lastName;

    @NotBlank(message = "名を入力してください")
    @Size(max = 50, message = "名は50文字以内で入力してください")
    private String firstName;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "正しいメールアドレスの形式で入力してください")
    private String email;

    @Size(max = 20, message = "電話番号は20文字以内で入力してください")
    private String phone;

    private LocalDate birthDate;

    @Size(max = 10, message = "郵便番号は10文字以内で入力してください")
    private String postalCode;

    @Size(max = 50, message = "都道府県は50文字以内で入力してください")
    private String prefecture;

    @Size(max = 100, message = "市区町村は100文字以内で入力してください")
    private String city;

    @Size(max = 200, message = "住所1は200文字以内で入力してください")
    private String address1;

    @Size(max = 200, message = "住所2は200文字以内で入力してください")
    private String address2;

    @Builder.Default
    private List<String> allergies = new ArrayList<>();

    @Builder.Default
    private Boolean newsletter = false;

    @Builder.Default
    private Boolean enabled = true;

    private String role;
}
