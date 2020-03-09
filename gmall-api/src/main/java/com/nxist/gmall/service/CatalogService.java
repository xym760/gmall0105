package com.nxist.gmall.service;

import com.nxist.gmall.bean.PmsBaseCatalog1;
import com.nxist.gmall.bean.PmsBaseCatalog2;
import com.nxist.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/1/16 15:54
 */
public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
