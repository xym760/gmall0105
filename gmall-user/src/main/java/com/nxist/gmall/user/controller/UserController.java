package com.nxist.gmall.user.controller;

import com.nxist.gmall.bean.UmsMember;
import com.nxist.gmall.bean.UmsMemberReceiveAddress;
import com.nxist.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2019/12/24 18:34
 */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("getReceiveAddressByMemberId")
    @ResponseBody
    private List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId){
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses=userService.getReceiveAddressByMemberId(memberId);
        return umsMemberReceiveAddresses;
    }

    @RequestMapping("getAllUser")
    @ResponseBody
    private List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers=userService.getAllUser();
        return umsMembers;
    }

    @RequestMapping("index")
    @ResponseBody
    public String index(){
        return"hello user";
    }
}
