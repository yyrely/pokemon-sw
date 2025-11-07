package com.chuncongcong.sw.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chuncongcong.framework.response.ApiListResponse;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.ItemVO;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.bean.vo.UserItemVO;
import com.chuncongcong.sw.biz.service.ItemBizService;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户物品管理")
@Slf4j
@RestController
@RequestMapping("/item/user")
public class ItemUserController {

    @Resource
    private ItemBizService itemBizService;

    @Operation(summary = "查询用户已收集的物品列表")
    @PostMapping("/collected")
    public ApiListResponse<ItemVO> listCollectedItems() {
        return ApiListResponse.success(itemBizService.listItemsWithCollectStatus(true));
    }

    @Operation(summary = "查询用户未收集的物品列表")
    @PostMapping("/uncollected")
    public ApiListResponse<ItemVO> listUnCollectedItems() {
        return ApiListResponse.success(itemBizService.listItemsWithCollectStatus(false));
    }

    @Operation(summary = "用户收集记录列表")
    @PostMapping("/log/list")
    public ApiListResponse<UserItemVO> userList(@RequestBody ItemParam param) {
        List<UserItemCollectDO> UserItemCollectDOList = itemBizService.userList(param);
        return ApiListResponse.success(BeanUtil.copyToList(UserItemCollectDOList, UserItemVO.class));
    }

    @Operation(summary = "用户物品收集新增更新")
    @PostMapping("/log/saveOrUpdate")
    public ApiResponse<Void> userSaveOrUpdate(@RequestBody UserItemParam param) {
        itemBizService.userSaveOrUpdate(param);
        return ApiResponse.success();
    }

    @Operation(summary = "用户物品收集删除")
    @PostMapping("/log/delete")
    public ApiResponse<Void> userDelete(@RequestBody UserItemParam param) {
        itemBizService.userDelete(param);
        return ApiResponse.success();
    }

    @Operation(summary = "用户物品追高排行")
    @PostMapping("/max/top")
    public ApiListResponse<TopVO> maxTop() {
        return ApiListResponse.success(itemBizService.maxTop());
    }

    @Operation(summary = "用户物品捡漏排行")
    @PostMapping("/min/top")
    public ApiListResponse<TopVO> minTop() {
        return ApiListResponse.success(itemBizService.minTop());
    }

}
