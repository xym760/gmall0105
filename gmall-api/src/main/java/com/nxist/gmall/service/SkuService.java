package com.nxist.gmall.service;

import com.nxist.gmall.bean.PmsSkuInfo;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/3/11 22:07
 */
public interface SkuService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);
}
