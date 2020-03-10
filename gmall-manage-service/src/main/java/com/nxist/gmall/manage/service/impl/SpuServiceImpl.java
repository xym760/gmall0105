package com.nxist.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.nxist.gmall.bean.PmsProductInfo;
import com.nxist.gmall.manage.mapper.PmsProductInfoMapper;
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

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo=new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfos;
    }
}
