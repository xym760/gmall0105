package com.nxist.gmall.user.mapper;

import com.nxist.gmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author YuanmaoXu
 * @date 2019/12/24 18:38
 */
public interface UserMapper extends Mapper<UmsMember> {
    List<UmsMember> selectAllUser();
}
