package com.bear.asset.dto.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CreateAssetRequest {

    @NotBlank(message = "资产类别不能为空")
    private String category;

    @NotBlank(message = "资产子类型不能为空")
    private String subType;

    @NotBlank(message = "资产名称不能为空")
    @Size(max = 200, message = "资产名称不能超过200字符")
    private String name;

    private String currency = "CNY";

    private BigDecimal amount;

    private BigDecimal cost;

    private BigDecimal quantity;

    @Size(max = 20, message = "代码不能超过20字符")
    private String code;

    private Map<String, Object> extraJson;

    private String note;
}
