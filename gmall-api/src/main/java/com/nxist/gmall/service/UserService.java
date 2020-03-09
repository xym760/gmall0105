package com.nxist.gmall.service;

import com.nxist.gmall.bean.UmsMember;
import com.nxist.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2019/12/24 18:34
 */
public interface UserService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
