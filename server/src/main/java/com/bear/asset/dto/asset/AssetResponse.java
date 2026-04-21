package com.bear.asset.dto.asset;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AssetResponse {

    private Long assetId;
    private Long userId;
    private String category;
    private String categoryName;
    private String subType;
    private String subTypeName;
    private String name;
    private String currency;
    private BigDecimal amount;
    private BigDecimal amountCny;
    private BigDecimal cost;
    private BigDecimal quantity;
    private String code;
    private Map<String, Object> extraJson;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
