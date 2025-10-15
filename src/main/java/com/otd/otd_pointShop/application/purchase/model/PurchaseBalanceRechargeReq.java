package com.otd.otd_pointShop.application.purchase.model;

import lombok.Data;

@Data
public class PurchaseBalanceRechargeReq {
    private Long userId;
    private Integer amount;
}
