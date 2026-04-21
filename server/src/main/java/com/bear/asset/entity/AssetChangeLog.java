package com.bear.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "asset_change_log", autoResultMap = true)
public class AssetChangeLog {

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @TableField("asset_id")
    private Long assetId;

    @TableField("user_id")
    private Long userId;

    @TableField("change_type")
    private String changeType;

    @TableField(value = "old_value", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> oldValue;

    @TableField(value = "new_value", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> newValue;

    @TableField("changed_at")
    private LocalDateTime changedAt;
}
