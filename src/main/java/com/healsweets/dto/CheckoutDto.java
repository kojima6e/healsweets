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
public class CheckoutDto {

    // 配送先情報
    @NotBlank(message = "お名前を入力してください")
    private String shippingName;

    @NotBlank(message = "郵便番号を入力してください")
    private String shippingPostalCode;

    @NotBlank(message = "都道府県を選択してください")
    private String shippingPrefecture;

    @NotBlank(message = "市区町村を入力してください")
    private String shippingCity;

    @NotBlank(message = "番地を入力してください")
    private String shippingAddress1;

    private String shippingAddress2;

    @NotBlank(message = "電話番号を入力してください")
    private String shippingPhone;

    // 配送オプション
    private String deliveryOption = "standard";

    // 支払い方法
    @NotBlank(message = "お支払い方法を選択してください")
    private String paymentMethod;

    // クレジットカード情報（実際の実装では暗号化が必要）
    private String cardNumber;
    private String cardName;
    private String cardExpiry;
    private String cardCvc;

    // クーポン
    private String couponCode;

    // 利用規約同意
    private Boolean agreeTerms;

    // 既存住所を使用するか
    private Boolean useExistingAddress;
    private Long selectedAddressId;
}
