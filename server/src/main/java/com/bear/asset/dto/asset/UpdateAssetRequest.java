package com.bear.asset.dto.asset;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class UpdateAssetRequest {

    private String category;

    private String subType;

    @Size(max = 200, message = "资产名称不能超过200字符")
    private String name;

    private String currency;

    private BigDecimal amount;

    private BigDecimal cost;

    private BigDecimal quantity;

    @Size(max = 20, message = "代码不能超过20字符")
    private String code;

    private Map<String, Object> extraJson;

    private String note;
}
