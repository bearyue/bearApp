package com.bear.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bear.asset.common.BusinessException;
import com.bear.asset.common.ResultCode;
import com.bear.asset.common.enums.AssetCategory;
import com.bear.asset.common.enums.AssetSubType;
import com.bear.asset.common.enums.Currency;
import com.bear.asset.dto.asset.*;
import com.bear.asset.entity.Asset;
import com.bear.asset.entity.AssetChangeLog;
import com.bear.asset.mapper.AssetChangeLogMapper;
import com.bear.asset.mapper.AssetMapper;
import com.bear.asset.service.AssetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetMapper assetMapper;
    private final AssetChangeLogMapper changeLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AssetResponse createAsset(Long userId, CreateAssetRequest request) {
        validateCategory(request.getCategory());
        validateSubType(request.getSubType());
        validateCurrency(request.getCurrency());

        Asset asset = new Asset();
        asset.setUserId(userId);
        asset.setCategory(request.getCategory());
        asset.setSubType(request.getSubType());
        asset.setName(request.getName());
        asset.setCurrency(request.getCurrency() != null ? request.getCurrency() : "CNY");
        asset.setAmount(request.getAmount() != null ? request.getAmount() : BigDecimal.ZERO);
        asset.setAmountCny(calculateAmountCny(asset.getAmount(), asset.getCurrency()));
        asset.setCost(request.getCost());
        asset.setQuantity(request.getQuantity());
        asset.setCode(request.getCode());
        asset.setExtraJson(request.getExtraJson());
        asset.setNote(request.getNote());

        assetMapper.insert(asset);
        logChange(asset.getAssetId(), userId, "CREATE", null, assetToMap(asset));

        return toAssetResponse(asset);
    }

    @Override
    @Transactional
    public AssetResponse updateAsset(Long userId, Long assetId, UpdateAssetRequest request) {
        Asset asset = getAssetAndValidateOwner(userId, assetId);
        Map<String, Object> oldValue = assetToMap(asset);

        if (request.getCategory() != null) {
            validateCategory(request.getCategory());
            asset.setCategory(request.getCategory());
        }
        if (request.getSubType() != null) {
            validateSubType(request.getSubType());
            asset.setSubType(request.getSubType());
        }
        if (request.getName() != null) {
            asset.setName(request.getName());
        }
        if (request.getCurrency() != null) {
            validateCurrency(request.getCurrency());
            asset.setCurrency(request.getCurrency());
        }
        if (request.getAmount() != null) {
            asset.setAmount(request.getAmount());
        }
        // Recalculate CNY amount whenever amount or currency changes
        asset.setAmountCny(calculateAmountCny(asset.getAmount(), asset.getCurrency()));

        if (request.getCost() != null) {
            asset.setCost(request.getCost());
        }
        if (request.getQuantity() != null) {
            asset.setQuantity(request.getQuantity());
        }
        if (request.getCode() != null) {
            asset.setCode(request.getCode());
        }
        if (request.getExtraJson() != null) {
            asset.setExtraJson(request.getExtraJson());
        }
        if (request.getNote() != null) {
            asset.setNote(request.getNote());
        }
        asset.setUpdatedAt(LocalDateTime.now());

        assetMapper.updateById(asset);
        logChange(assetId, userId, "UPDATE", oldValue, assetToMap(asset));

        return toAssetResponse(asset);
    }

    @Override
    @Transactional
    public void deleteAsset(Long userId, Long assetId) {
        Asset asset = getAssetAndValidateOwner(userId, assetId);
        logChange(assetId, userId, "DELETE", assetToMap(asset), null);
        assetMapper.deleteById(assetId);
    }

    @Override
    public AssetResponse getAsset(Long userId, Long assetId) {
        Asset asset = getAssetAndValidateOwner(userId, assetId);
        return toAssetResponse(asset);
    }

    @Override
    public List<AssetResponse> listAssets(Long userId, String category, String subType) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<Asset>()
                .eq(Asset::getUserId, userId)
                .eq(category != null, Asset::getCategory, category)
                .eq(subType != null, Asset::getSubType, subType)
                .orderByDesc(Asset::getUpdatedAt);

        return assetMapper.selectList(wrapper).stream()
                .map(this::toAssetResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NetWorthResponse getNetWorth(Long userId) {
        List<AssetSummaryResponse> breakdown = getAssetSummary(userId);

        BigDecimal totalAsset = BigDecimal.ZERO;
        BigDecimal totalLiability = BigDecimal.ZERO;

        for (AssetSummaryResponse summary : breakdown) {
            if (AssetCategory.LIABILITY.name().equals(summary.getCategory())) {
                totalLiability = totalLiability.add(summary.getTotalAmountCny());
            } else {
                totalAsset = totalAsset.add(summary.getTotalAmountCny());
            }
        }

        return NetWorthResponse.builder()
                .totalAsset(totalAsset)
                .totalLiability(totalLiability)
                .netWorth(totalAsset.subtract(totalLiability))
                .categoryBreakdown(breakdown)
                .build();
    }

    @Override
    public List<AssetSummaryResponse> getAssetSummary(Long userId) {
        List<Asset> assets = assetMapper.selectList(
                new LambdaQueryWrapper<Asset>().eq(Asset::getUserId, userId)
        );

        Map<String, List<Asset>> grouped = assets.stream()
                .collect(Collectors.groupingBy(Asset::getCategory));

        List<AssetSummaryResponse> result = new ArrayList<>();
        for (AssetCategory cat : AssetCategory.values()) {
            List<Asset> catAssets = grouped.getOrDefault(cat.name(), Collections.emptyList());
            if (!catAssets.isEmpty()) {
                BigDecimal total = catAssets.stream()
                        .map(a -> a.getAmountCny() != null ? a.getAmountCny() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                result.add(AssetSummaryResponse.builder()
                        .category(cat.name())
                        .categoryName(cat.getDisplayName())
                        .count(catAssets.size())
                        .totalAmountCny(total)
                        .build());
            }
        }
        return result;
    }

    // --- private helpers ---

    private Asset getAssetAndValidateOwner(Long userId, Long assetId) {
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "资产不存在");
        }
        if (!asset.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权操作此资产");
        }
        return asset;
    }

    private void validateCategory(String category) {
        try {
            AssetCategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的资产类别: " + category);
        }
    }

    private void validateSubType(String subType) {
        try {
            AssetSubType.valueOf(subType);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的资产子类型: " + subType);
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null) return;
        try {
            Currency.valueOf(currency);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "无效的币种: " + currency);
        }
    }

    /**
     * Simple CNY conversion. For non-CNY currencies, uses a hardcoded fallback rate.
     * A real implementation would query the exchange_rate table.
     */
    private BigDecimal calculateAmountCny(BigDecimal amount, String currency) {
        if (amount == null) return BigDecimal.ZERO;
        if ("CNY".equals(currency)) return amount;

        BigDecimal rate = switch (currency) {
            case "USD" -> new BigDecimal("7.25");
            case "HKD" -> new BigDecimal("0.93");
            default -> BigDecimal.ONE;
        };
        return amount.multiply(rate);
    }

    private void logChange(Long assetId, Long userId, String changeType,
                           Map<String, Object> oldValue, Map<String, Object> newValue) {
        AssetChangeLog logEntry = new AssetChangeLog();
        logEntry.setAssetId(assetId);
        logEntry.setUserId(userId);
        logEntry.setChangeType(changeType);
        logEntry.setOldValue(oldValue);
        logEntry.setNewValue(newValue);
        changeLogMapper.insert(logEntry);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> assetToMap(Asset asset) {
        return objectMapper.convertValue(asset, Map.class);
    }

    private AssetResponse toAssetResponse(Asset asset) {
        String categoryName = null;
        try {
            categoryName = AssetCategory.valueOf(asset.getCategory()).getDisplayName();
        } catch (IllegalArgumentException ignored) {
        }

        String subTypeName = null;
        try {
            subTypeName = AssetSubType.valueOf(asset.getSubType()).getDisplayName();
        } catch (IllegalArgumentException ignored) {
        }

        return AssetResponse.builder()
                .assetId(asset.getAssetId())
                .userId(asset.getUserId())
                .category(asset.getCategory())
                .categoryName(categoryName)
                .subType(asset.getSubType())
                .subTypeName(subTypeName)
                .name(asset.getName())
                .currency(asset.getCurrency())
                .amount(asset.getAmount())
                .amountCny(asset.getAmountCny())
                .cost(asset.getCost())
                .quantity(asset.getQuantity())
                .code(asset.getCode())
                .extraJson(asset.getExtraJson())
                .note(asset.getNote())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
