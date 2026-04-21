package com.bear.asset.controller;

import com.bear.asset.common.Result;
import com.bear.asset.dto.asset.*;
import com.bear.asset.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    public Result<AssetResponse> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        return Result.success(assetService.createAsset(getCurrentUserId(), request));
    }

    @PutMapping("/{id}")
    public Result<AssetResponse> updateAsset(@PathVariable("id") Long id,
                                             @Valid @RequestBody UpdateAssetRequest request) {
        return Result.success(assetService.updateAsset(getCurrentUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAsset(@PathVariable("id") Long id) {
        assetService.deleteAsset(getCurrentUserId(), id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AssetResponse> getAsset(@PathVariable("id") Long id) {
        return Result.success(assetService.getAsset(getCurrentUserId(), id));
    }

    @GetMapping
    public Result<List<AssetResponse>> listAssets(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "subType", required = false) String subType) {
        return Result.success(assetService.listAssets(getCurrentUserId(), category, subType));
    }

    @GetMapping("/summary")
    public Result<List<AssetSummaryResponse>> getAssetSummary() {
        return Result.success(assetService.getAssetSummary(getCurrentUserId()));
    }

    @GetMapping("/net-worth")
    public Result<NetWorthResponse> getNetWorth() {
        return Result.success(assetService.getNetWorth(getCurrentUserId()));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getPrincipal();
    }
}
