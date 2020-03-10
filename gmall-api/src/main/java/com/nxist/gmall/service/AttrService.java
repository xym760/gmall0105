package com.nxist.gmall.service;

import com.nxist.gmall.bean.PmsBaseAttrInfo;
import com.nxist.gmall.bean.PmsBaseAttrValue;
import com.nxist.gmall.bean.PmsBaseSaleAttr;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/1/17 23:10
 */
public interface AttrService {
    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAtrrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();
}
