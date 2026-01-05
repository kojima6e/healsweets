package com.healsweets.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingService {

    public static final BigDecimal STANDARD_SHIPPING = BigDecimal.valueOf(500);
    public static final BigDecimal EXPRESS_SHIPPING = BigDecimal.valueOf(1000);

    public BigDecimal calculateShipping(String deliveryOption) {
        return "express".equals(deliveryOption) ? EXPRESS_SHIPPING : STANDARD_SHIPPING;
    }

    public BigDecimal getStandardShipping() {
        return STANDARD_SHIPPING;
    }
}

