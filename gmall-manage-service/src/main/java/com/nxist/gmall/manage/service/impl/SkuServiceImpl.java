package com.nxist.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.nxist.gmall.bean.PmsSkuAttrValue;
import com.nxist.gmall.bean.PmsSkuImage;
import com.nxist.gmall.bean.PmsSkuInfo;
import com.nxist.gmall.bean.PmsSkuSaleAttrValue;
import com.nxist.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.nxist.gmall.manage.mapper.PmsSkuImageMapper;
import com.nxist.gmall.manage.mapper.PmsSkuInfoMapper;
import com.nxist.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.nxist.gmall.service.SkuService;
import com.nxist.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

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
    @Autowired
    RedisUtil redisUtil;

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

    public PmsSkuInfo getSkuByIdFromDb(String skuId) {
        //sku商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        //sku的图片集合
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        Jedis jedis = null;
        try {
            //连接缓存
            jedis = redisUtil.getJedis();
            //先查询redis缓存中是否有该数据
            String skuKey = "sku:" + skuId + ":info";
            String skuJson = jedis.get(skuKey);
            if (StringUtils.isNotBlank(skuJson)) {
                //如果redis缓存中有该数据，则直接取出返回
                pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            } else {
                //如果redis缓存中没有，再查询MySQL
                //为了防止缓存击穿，先设置分布式锁，达到一次只能有一个请求能访问
                //当该key不存在时才能set成功，set成功，表示拿到分布式锁，过期时间为10秒，即10秒内，如果该请求不释放锁，其它请求只能排队等待，设置过期时间是为了防止拿到这个锁的请求长时间不释放锁，导致其它请求都不能查询MySQL数据库
                String ok = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 10);
                if (StringUtils.isNotBlank(ok) && ok.equals("OK")) {
                    //拿到分布式锁，可以访问MySQL数据库
                    pmsSkuInfo = getSkuByIdFromDb(skuId);
                    if (pmsSkuInfo != null) {
                        //如果mysql中存在该数据，则mysql查询结果存入redis
                        jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                    } else {
                        //mysql数据库中不存在该sku
                        //为了防止缓存穿透，将null值或空字符串设置给redis，并设置过期时间为3分钟，即三分钟内用户访问这个不存在的key时，会直接从redis中获取值，不会对mysql数据库产生压力
                        jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));
                    }
                } else {
                    //没有拿到分布式锁，进行自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return getSkuById(skuId);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
        return pmsSkuInfos;
    }
}
