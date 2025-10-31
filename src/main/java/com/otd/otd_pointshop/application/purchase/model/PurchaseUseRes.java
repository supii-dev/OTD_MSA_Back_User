package com.otd.otd_pointshop.application.purchase.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseUseRes {
    private Long purchaseId;
    private boolean isUsed;
    private LocalDateTime usedAt;
}
