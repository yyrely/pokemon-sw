package com.chuncongcong.sw.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.framework.util.JwtUtil;
import com.chuncongcong.framework.util.UsernameGenerator;
import com.chuncongcong.sw.bean.enums.CollectModeEnum;
import com.chuncongcong.sw.bean.param.UserParam;
import com.chuncongcong.sw.bean.param.WxLoginParam;
import com.chuncongcong.sw.bean.vo.UserVO;
import com.chuncongcong.sw.bean.vo.WxLoginVO;
import com.chuncongcong.sw.biz.service.PokedexScaleSyncService;
import com.chuncongcong.sw.biz.service.UserBizService;
import com.chuncongcong.sw.client.WxService;
import com.chuncongcong.sw.client.dto.WxSessionDTO;
import com.chuncongcong.sw.entity.UserDO;
import com.chuncongcong.sw.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.chuncongcong.framework.exception.ServiceErrorEnum.PARAM_ERROR;

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
    @Resource
    private PokedexScaleSyncService pokedexScaleSyncService;

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
            user.setCollectMode(CollectModeEnum.SCALE_WORLD.getCode());
            user.setOnboardingVersion(0);
            user.setPokemonCollectSyncStatus(0);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCollectMode(UserParam param) {
        if (Objects.isNull(param) || !CollectModeEnum.valid(param.getCollectMode())) {
            throw ServiceException.instance(PARAM_ERROR);
        }
        UserDO userDO = userService.getById(ContextHolder.getUserId());
        if (CollectModeEnum.POKEDEX.getCode().equals(param.getCollectMode())
                && !Objects.equals(PokedexScaleSyncServiceImpl.SYNC_STATUS_SYNCED, userDO.getPokemonCollectSyncStatus())) {
            pokedexScaleSyncService.syncUserScaleCollects(userDO.getId());
            userDO = userService.getById(userDO.getId());
        }
        userDO.setCollectMode(param.getCollectMode());
        userService.updateById(userDO);
    }

    @Override
    public void completeOnboarding(UserParam param) {
        UserDO userDO = userService.getById(ContextHolder.getUserId());
        Integer version = Objects.nonNull(param) && Objects.nonNull(param.getOnboardingVersion())
                ? param.getOnboardingVersion()
                : 1;
        userDO.setOnboardingVersion(version);
        userService.updateById(userDO);
    }

    @Override
    public UserDO getById(Long userId) {
        return userService.getById(userId);
    }
}
