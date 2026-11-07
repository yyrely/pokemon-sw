package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.framework.interceptor.IgnoreLogin;
import com.chuncongcong.sw.bean.param.ItemCreateParam;
import com.chuncongcong.sw.bean.vo.ItemCreateVO;
import com.chuncongcong.sw.biz.service.ItemCreateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "物品管理")
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemCreateController {
    private final ItemCreateService itemCreateService;

    @Operation(summary = "新增物品及发售信息、可选子物品（验证码校验，无需登录）")
    @IgnoreLogin
    @PostMapping("/create")
    public ApiResponse<ItemCreateVO> create(@Valid @RequestBody ItemCreateParam param) {
        return ApiResponse.success(itemCreateService.create(param));
    }
}
