package com.chuncongcong.sw.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chuncongcong.framework.response.ApiListResponse;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.vo.ItemInfoVO;
import com.chuncongcong.sw.bean.vo.ItemVO;
import com.chuncongcong.sw.biz.service.ItemBizService;
import com.chuncongcong.sw.entity.ItemDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "物品管理")
@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

    @Resource
    private ItemBizService itemBizService;

    @Operation(summary = "物品列表")
    @PostMapping("/list")
    public ApiListResponse<ItemVO> list(@RequestBody ItemParam param) {
        List<ItemDO> itemDOList = itemBizService.list(param);
        return ApiListResponse.success(BeanUtil.copyToList(itemDOList, ItemVO.class));
    }

    @Operation(summary = "物品详情(包含收藏信息)")
    @PostMapping("/info/has/collect")
    public ApiResponse<ItemVO> infoHasCollect(@RequestBody ItemParam param) {
        ItemVO itemVO = itemBizService.infoHasCollect(param);
        return ApiResponse.success(itemVO);
    }

    @Operation(summary = "查询物品列表（已收集优先）")
    @PostMapping("/list/user-first")
    public ApiListResponse<ItemVO> listUserFirst(@RequestBody ItemParam param) {
        return ApiListResponse.success(itemBizService.listUserFirst(param));
    }

    @Operation(summary = "物品资讯列表")
    @PostMapping("/info/list")
    public ApiListResponse<ItemInfoVO> infoList(@RequestBody ItemParam param) {
        return ApiListResponse.success(itemBizService.infoList(param));
    }

}
