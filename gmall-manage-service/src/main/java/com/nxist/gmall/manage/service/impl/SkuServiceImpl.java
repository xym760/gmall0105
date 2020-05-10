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
import java.util.UUID;

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
    public PmsSkuInfo getSkuById(String skuId, String ip) {
        System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "进入了商品详情");
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
                System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "从缓存中获取商品详情");
                pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            } else {
                //如果redis缓存中没有，再查询MySQL
                System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "发现缓存中没有，申请访问数据库的分布式锁：" + "sku:" + skuId + ":lock");
                //为了防止缓存击穿，先设置分布式锁，达到一次只能有一个请求能访问
                //当该key不存在时才能set成功，set成功，表示拿到分布式锁，过期时间为10秒，即10秒内，如果该请求不释放锁，其它请求只能排队等待，设置过期时间是为了防止拿到这个锁的请求长时间不释放锁，导致其它请求都不能查询MySQL数据库
                String token = UUID.randomUUID().toString();//token用来标识自己的锁
                String ok = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10 * 1000);
                if (StringUtils.isNotBlank(ok) && ok.equals("OK")) {
                    //拿到分布式锁，可以访问MySQL数据库
                    System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "拿到分布式锁，可以访问MySQL数据库：" + "sku:" + skuId + ":lock");
                    pmsSkuInfo = getSkuByIdFromDb(skuId);
                    //为了进行测试，防止释放锁过快，加上睡眠时间
//                    Thread.sleep(5*1000);
                    if (pmsSkuInfo != null) {
                        //如果mysql中存在该数据，则mysql查询结果存入redis
                        jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                    } else {
                        //mysql数据库中不存在该sku
                        //为了防止缓存穿透，将null值或空字符串设置给redis，并设置过期时间为3分钟，即三分钟内用户访问这个不存在的key时，会直接从redis中获取值，不会对mysql数据库产生压力
                        jedis.setex("sku:" + skuId + ":info", 60 * 3, JSON.toJSONString(""));
                    }
                    //在访问到MySQL数据后，释放锁
                    System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "使用完毕，将锁归还：" + "sku:" + skuId + ":lock");
                    String lockToken = jedis.get("sku:" + skuId + ":lock");
                    if (StringUtils.isNotBlank(lockToken) && lockToken.equals(token)) {
                        //jedis.eval("lua");可以用lua脚本，在查询到key的同时删除该key，防止高并发下的意外发生，例如当判断完成后，准备删除时，分布式锁过期了，这时就会删除其它线程的锁，所以用lua实现原子操作
                        jedis.del("sku:" + skuId + ":lock");//用token确认删除的是自己的sku的锁
                    }
                } else {
                    //没有拿到分布式锁，进行自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                    System.out.println("ip为" + ip + "的用户:" + Thread.currentThread().getName() + "没有拿到分布式锁，进行自旋：" + "sku:" + skuId + ":lock");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return getSkuById(skuId, ip);
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

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfos;
    }
}
