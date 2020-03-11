package com.nxist.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.nxist.gmall.bean.PmsSkuAttrValue;
import com.nxist.gmall.bean.PmsSkuImage;
import com.nxist.gmall.bean.PmsSkuInfo;
import com.nxist.gmall.bean.PmsSkuSaleAttrValue;
import com.nxist.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.nxist.gmall.manage.mapper.PmsSkuImageMapper;
import com.nxist.gmall.manage.mapper.PmsSkuInfoMapper;
import com.nxist.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.nxist.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/3/11 22:09
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {

        //插入skuInfo，即商品的基本信息
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();
        //插入平台属性关联，即把sku（具体版本的华为P30）的平台属性和平台属性值存入数据库
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);//设置该平台属性所属的sku
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //插入销售属性关联，即将sku（具体版本的华为P30）的销售属性存入数据库
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        //插入图片信息，即插入sku的图片到数据库
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
    }
}
