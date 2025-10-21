package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.UserDO;
import com.chuncongcong.sw.mapper.UserMapper;
import com.chuncongcong.sw.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserDO getByOpenId(String openId) {
        return this.lambdaQuery().eq(UserDO::getOpenId, openId).one();
    }
}
