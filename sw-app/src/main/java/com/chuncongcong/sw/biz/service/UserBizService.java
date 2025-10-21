package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.param.UserParam;
import com.chuncongcong.sw.bean.param.WxLoginParam;
import com.chuncongcong.sw.bean.vo.WxLoginVO;

public interface UserBizService {

    WxLoginVO wxLogin(WxLoginParam param);

    void update(UserParam param);
}
