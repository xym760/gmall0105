package com.nxist.gmall.service;

import com.nxist.gmall.bean.PmsProductInfo;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/3/9 15:39
 */
public interface SpuService {
    List<PmsProductInfo> spuList(String catalog3Id);
}
