package com.nxist.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.nxist.gmall.bean.PmsProductImage;
import com.nxist.gmall.bean.PmsProductInfo;
import com.nxist.gmall.bean.PmsProductSaleAttr;
import com.nxist.gmall.bean.PmsProductSaleAttrValue;
import com.nxist.gmall.manage.mapper.PmsProductImageMapper;
import com.nxist.gmall.manage.mapper.PmsProductInfoMapper;
import com.nxist.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.nxist.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.nxist.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/3/9 15:42
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    PmsProductImageMapper pmsProductImageMapper;
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo=new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfos;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        // 保存商品信息
        pmsProductInfoMapper.insertSelective(pmsProductInfo);

        // 生成商品主键
        String productId = pmsProductInfo.getId();

        // 保存商品图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for (PmsProductImage pmsProductImage : spuImageList) {
            pmsProductImage.setProductId(productId);
            pmsProductImageMapper.insertSelective(pmsProductImage);
        }

        // 保存销售属性信息
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
            pmsProductSaleAttr.setProductId(productId);
            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

            // 保存销售属性值
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                pmsProductSaleAttrValue.setProductId(productId);
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {//例如传入华为P30
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);//查找到销售属性有颜色和版本两个属性
        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());//销售属性id用的是系统的字典表中id，不是销售属性表的主键
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> pmsProductImages = pmsProductImageMapper.select(pmsProductImage);
        return pmsProductImages;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String productId,String skuId) {
        /**
         * SELECT
         * 	*
         * FROM
         * 	pms_product_sale_attr sa,
         * 	pms_product_sale_attr_value sav
         * WHERE
         * 	sa.sale_attr_id = sav.sale_attr_id
         * 	AND sa.product_id = sav.product_id
         * 	AND sa.product_id = 70;
         */
//        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
//        pmsProductSaleAttr.setProductId(productId);
//        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);//在P30的spu中得到productId为70的销售属性集合，集合中有颜色和版本两个属性
//        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {//遍历并给这两行属性赋值
//            String saleAttrId = productSaleAttr.getSaleAttrId();
//            PmsProductSaleAttrValue pmsProductSaleAttrValue=new PmsProductSaleAttrValue();
//            pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);//通过设置saleAttrId可以查询出具体的某一类销售属性，如P30的颜色的所有销售属性
//            pmsProductSaleAttrValue.setProductId(productId);//通过设置productId可以查询出华为P30所有的销售属性
//            //得到某一类销售属性的集合，如P30颜色下的天空之境、极光色、珠光贝母、赤茶橘四条记录
//            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
//            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
//        }
        /**
         * SELECT
         * 	sa.*,
         * 	sav.*,
         * IF
         * 	( ssav.sku_id, 1, 0 ) AS isChecked
         * FROM
         * 	pms_product_sale_attr sa
         * 	INNER JOIN pms_product_sale_attr_value sav ON sa.product_id = sav.product_id
         * 	AND sa.sale_attr_id = sav.sale_attr_id
         * 	AND sa.product_id = 70
         * 	LEFT JOIN pms_sku_sale_attr_value ssav ON sav.id = ssav.sale_attr_value_id
         * 	AND ssav.sku_id = 109
         */
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(productId,skuId);
        return pmsProductSaleAttrs;
    }
}
