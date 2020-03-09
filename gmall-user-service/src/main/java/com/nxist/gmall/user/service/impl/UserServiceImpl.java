package com.nxist.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.nxist.gmall.bean.UmsMember;
import com.nxist.gmall.bean.UmsMemberReceiveAddress;
import com.nxist.gmall.service.UserService;
import com.nxist.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.nxist.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2019/12/24 18:35
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMemberList = userMapper.selectAll();//userMapper.selectAllUser();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
//        Example example = new Example(UmsMemberReceiveAddress.class);
//        example.createCriteria().andEqualTo("memberId", memberId);
//        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);
        return umsMemberReceiveAddresses;
    }
}
