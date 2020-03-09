package com.nxist.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.nxist.gmall.bean.PmsBaseAttrInfo;
import com.nxist.gmall.bean.PmsBaseAttrValue;
import com.nxist.gmall.service.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2020/1/17 21:04
 */
@Controller
@CrossOrigin
public class AttrController {

    @Reference
    AttrService attrService;

    @RequestMapping("saveAttrInfo")
    @ResponseBody
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
        String success=attrService.saveAtrrInfo(pmsBaseAttrInfo);
        return "success";
    }

    @RequestMapping("attrInfoList")
    @ResponseBody
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.attrInfoList(catalog3Id);
        return pmsBaseAttrInfos;
    }

    @RequestMapping("getAttrValueList")
    @ResponseBody
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        List<PmsBaseAttrValue> pmsBaseAttrValues = attrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;
    }
}
