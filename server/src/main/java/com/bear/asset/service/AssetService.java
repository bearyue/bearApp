package com.bear.asset.service;

import com.bear.asset.dto.asset.*;

import java.util.List;

public interface AssetService {

    AssetResponse createAsset(Long userId, CreateAssetRequest request);

    AssetResponse updateAsset(Long userId, Long assetId, UpdateAssetRequest request);

    void deleteAsset(Long userId, Long assetId);

    AssetResponse getAsset(Long userId, Long assetId);

    List<AssetResponse> listAssets(Long userId, String category, String subType);

    NetWorthResponse getNetWorth(Long userId);

    List<AssetSummaryResponse> getAssetSummary(Long userId);
}
