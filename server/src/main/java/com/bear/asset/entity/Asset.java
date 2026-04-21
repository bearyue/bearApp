package com.bear.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "asset", autoResultMap = true)
public class Asset {

    @TableId(value = "asset_id", type = IdType.AUTO)
    private Long assetId;

    @TableField("user_id")
    private Long userId;

    @TableField("category")
    private String category;

    @TableField("sub_type")
    private String subType;

    @TableField("name")
    private String name;

    @TableField("currency")
    private String currency;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("amount_cny")
    private BigDecimal amountCny;

    @TableField("cost")
    private BigDecimal cost;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("code")
    private String code;

    @TableField(value = "extra_json", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> extraJson;

    @TableField("note")
    private String note;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
