package com.healsweets.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAdminDto {

    private Long id;

    @NotBlank(message = "商品名を入力してください")
    @Size(max = 200, message = "商品名は200文字以内で入力してください")
    private String name;

    @Size(max = 2000, message = "商品説明は2000文字以内で入力してください")
    private String description;

    @NotNull(message = "価格を入力してください")
    @DecimalMin(value = "0", message = "価格は0円以上で入力してください")
    private BigDecimal price;

    // 複数カテゴリー対応
    @Builder.Default
    private List<String> categories = new ArrayList<>();

    @Size(max = 500, message = "画像URLは500文字以内で入力してください")
    private String imageUrl;

    @Size(max = 100, message = "内容量は100文字以内で入力してください")
    private String quantity;

    @Size(max = 50, message = "賞味期限は50文字以内で入力してください")
    private String expiryDays;

    @Size(max = 200, message = "保存方法は200文字以内で入力してください")
    private String storageMethod;

    @Builder.Default
    private List<String> allergens = new ArrayList<>();

    @Builder.Default
    private List<String> features = new ArrayList<>();

    @NotNull(message = "在庫数を入力してください")
    @Min(value = 0, message = "在庫数は0以上で入力してください")
    private Integer stock;

    @Builder.Default
    private Boolean active = true;
}
