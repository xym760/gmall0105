package com.nxist.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.nxist.gmall.bean.PmsProductSaleAttr;
import com.nxist.gmall.bean.PmsSkuInfo;
import com.nxist.gmall.service.SkuService;
import com.nxist.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/3/12 23:47
 */
@Controller
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map) {
        //sku对象
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(skuId);
        map.put("skuInfo", pmsSkuInfo);
        //销售属性列表
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());//得到两行销售属性
        map.put("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);
        return "item";
    }

    @RequestMapping("index")
    public String index(ModelMap modelMap) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("循环数据" + i);
        }
        modelMap.put("list", list);
        modelMap.put("hello", "hello thymeleaf !!");
        modelMap.put("check", "0");
        return "index";
    }
}
