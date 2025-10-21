package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.UserDO;

public interface UserService extends IService<UserDO> {

    UserDO getByOpenId(String openId);
}
