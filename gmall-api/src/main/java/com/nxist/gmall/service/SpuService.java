package com.nxist.gmall.service;

import com.nxist.gmall.bean.PmsProductImage;
import com.nxist.gmall.bean.PmsProductInfo;
import com.nxist.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/3/9 15:39
 */
public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> spuSaleAttrList(String supId);

    List<PmsProductImage> spuImageList(String spuId);

    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId);
}
