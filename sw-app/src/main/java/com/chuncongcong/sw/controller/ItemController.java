package com.chuncongcong.sw.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chuncongcong.framework.response.ApiListResponse;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.ItemInfoVO;
import com.chuncongcong.sw.bean.vo.ItemVO;
import com.chuncongcong.sw.bean.vo.UserItemVO;
import com.chuncongcong.sw.biz.service.ItemBizService;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.UserItemDO;
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

    @Operation(summary = "物品详情")
    @PostMapping("/info")
    public ApiResponse<ItemVO> info(@RequestBody ItemParam param) {
        ItemDO itemDO = itemBizService.info(param);
        return ApiResponse.success(BeanUtil.copyProperties(itemDO, ItemVO.class));
    }

    @Operation(summary = "查询物品列表（已收集优先）")
    @PostMapping("/list/user-first")
    public ApiListResponse<ItemVO> listUserFirst(@RequestBody ItemParam param) {
        return ApiListResponse.success(itemBizService.listUserFirst(param));
    }

    @Operation(summary = "查询用户已收集的物品列表")
    @PostMapping("/user/collected")
    public ApiListResponse<ItemVO> listCollectedItems() {
        return ApiListResponse.success(itemBizService.listItemsWithCollectStatus(true));
    }

    @Operation(summary = "查询用户未收集的物品列表")
    @PostMapping("/user/uncollected")
    public ApiListResponse<ItemVO> listUnCollectedItems() {
        return ApiListResponse.success(itemBizService.listItemsWithCollectStatus(false));
    }

    @Operation(summary = "用户收集记录列表")
    @PostMapping("/user/log/list")
    public ApiListResponse<UserItemVO> userList(@RequestBody ItemParam param) {
        List<UserItemDO> userItemDOList = itemBizService.userList(param);
        return ApiListResponse.success(BeanUtil.copyToList(userItemDOList, UserItemVO.class));
    }

    @Operation(summary = "用户物品收集新增更新")
    @PostMapping("/user/log/saveOrUpdate")
    public ApiResponse<Void> userSaveOrUpdate(@RequestBody UserItemParam param) {
        itemBizService.userSaveOrUpdate(param);
        return ApiResponse.success();
    }

    @Operation(summary = "用户物品收集删除")
    @PostMapping("/user/log/delete")
    public ApiResponse<Void> userDelete(@RequestBody UserItemParam param) {
        itemBizService.userDelete(param);
        return ApiResponse.success();
    }

    @Operation(summary = "物品信息列表")
    @PostMapping("/info/list")
    public ApiListResponse<ItemInfoVO> infoList(@RequestBody ItemParam param) {
        return ApiListResponse.success(itemBizService.infoList(param));
    }

}
