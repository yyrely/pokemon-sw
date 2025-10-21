package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.interceptor.IgnoreLogin;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.UserParam;
import com.chuncongcong.sw.bean.vo.WxLoginVO;
import com.chuncongcong.sw.biz.service.UserBizService;
import com.chuncongcong.sw.bean.param.WxLoginParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserBizService userBizService;

    @IgnoreLogin
    @Operation(summary = "微信登录")
    @PostMapping("/wx/login")
    public ApiResponse<WxLoginVO> wxLogin(@RequestBody @Validated WxLoginParam param) {
        return ApiResponse.success(userBizService.wxLogin(param));
    }

    @Operation(summary = "更新")
    @PostMapping("/update")
    public ApiResponse<Void> update(@RequestBody @Validated UserParam param) {
        userBizService.update(param);
        return ApiResponse.success();
    }
}


