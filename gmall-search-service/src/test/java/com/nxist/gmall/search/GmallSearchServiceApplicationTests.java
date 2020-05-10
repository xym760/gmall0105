package com.nxist.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.nxist.gmall.bean.PmsSearchSkuInfo;
import com.nxist.gmall.bean.PmsSkuInfo;
import com.nxist.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class GmallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;//查询mysql数据

    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() throws IOException {

        //查询mysql数据
        List<PmsSkuInfo> pmsSkuInfoList = new ArrayList<>();
        pmsSkuInfoList = skuService.getAllSku("61");
        //转化为es的数据结构
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }
        // 导入es
        jestClient.execute(null);
    }

}
