package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chuncongcong.framework.util.JwtUtil;
import com.chuncongcong.framework.util.UsernameGenerator;
import com.chuncongcong.sw.bean.param.UserParam;
import com.chuncongcong.sw.bean.param.WxLoginParam;
import com.chuncongcong.sw.bean.vo.UserVO;
import com.chuncongcong.sw.bean.vo.WxLoginVO;
import com.chuncongcong.sw.biz.service.UserBizService;
import com.chuncongcong.sw.client.WxService;
import com.chuncongcong.sw.client.dto.WxSessionDTO;
import com.chuncongcong.sw.entity.UserDO;
import com.chuncongcong.sw.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserBizServiceImpl implements UserBizService {

    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.appSecret}")
    private String appSecret;

    @Resource
    private UserService userService;
    @Resource
    private WxService wxService;

    @Override
    public WxLoginVO wxLogin(WxLoginParam param) {
//        String code = param.getCode();
//        WxSessionDTO wxSession = wxService.getWxSession(appId, appSecret, code);
//        String openId = wxSession.getOpenid();
        String openId = "oJxhc13dsiYUttKvwF5GHrdorYiI";

        UserDO user = userService.getByOpenId(openId);
        if (Objects.isNull( user)) {
            user = new UserDO();
            user.setUsername(UsernameGenerator.generateUsername("训练家"));
            user.setOpenId(openId);
            userService.save(user);
        }
        String token = JwtUtil.generateToken(user.getId());

        WxLoginVO wxLoginVO = new WxLoginVO();
        wxLoginVO.setToken(token);
        wxLoginVO.setUser(BeanUtil.copyProperties(user, UserVO.class));

        return wxLoginVO;
    }

    @Override
    public void update(UserParam param) {
        UserDO userDO = userService.getById(param.getId());
        BeanUtil.copyProperties(param, userDO);
        userService.updateById(userDO);
    }
}
