package com.healsweets.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDto {

    @NotBlank(message = "お問い合わせ種別を選択してください")
    private String category;

    @NotBlank(message = "件名を入力してください")
    private String subject;

    @NotBlank(message = "お問い合わせ内容を入力してください")
    private String message;

    private String email;
}
