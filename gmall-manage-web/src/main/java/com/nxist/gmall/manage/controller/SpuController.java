package com.nxist.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.nxist.gmall.bean.PmsProductImage;
import com.nxist.gmall.bean.PmsProductInfo;
import com.nxist.gmall.bean.PmsProductSaleAttr;
import com.nxist.gmall.manage.util.PmsUploadUtil;
import com.nxist.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/3/9 14:28
 */
@Controller
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId) {
        List<PmsProductImage> pmsProductImages=spuService.spuImageList(spuId);
        return pmsProductImages;
    }

    /**
     * 查询所有的销售属性，比如颜色、和版本，以及它们的值
     * @param spuId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrs=spuService.spuSaleAttrList(spuId);
        return pmsProductSaleAttrs;
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        //将图片或者音视频上传到分布式的文件存储系统
        //将图片的存储路径返回给页面
        String imgUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imgUrl);
        return imgUrl;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }

    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id) {
        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);
        return pmsProductInfos;
    }
}
