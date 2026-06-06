package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.response.ApiListResponse;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.ExchangeSearchParam;
import com.chuncongcong.sw.bean.param.ItemParam;
import com.chuncongcong.sw.bean.param.UserItemParam;
import com.chuncongcong.sw.bean.vo.ExchangeItemVO;
import com.chuncongcong.sw.bean.vo.ExchangeUserCardVO;
import com.chuncongcong.sw.bean.vo.ItemTopVO;
import com.chuncongcong.sw.bean.vo.ItemVO;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.bean.vo.UserItemVO;
import com.chuncongcong.sw.biz.service.ItemBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User item")
@Slf4j
@RestController
@RequestMapping("/item/user")
public class ItemUserController {

    @Resource
    private ItemBizService itemBizService;

    @Operation(summary = "List collected items")
    @PostMapping("/collected")
    public ApiListResponse<ItemVO> listCollectedItems() {
        return ApiListResponse.success(itemBizService.listItemsWithCollectStatus(true));
    }

    @Operation(summary = "List uncollected items")
    @PostMapping("/uncollected")
    public ApiListResponse<ItemVO> listUnCollectedItems() {
        return ApiListResponse.success(itemBizService.listItemsWithCollectStatus(false));
    }

    @Operation(summary = "List user collection records")
    @PostMapping("/log/list")
    public ApiListResponse<UserItemVO> userList(@RequestBody ItemParam param) {
        return ApiListResponse.success(itemBizService.userList(param));
    }

    @Operation(summary = "Save or update user collection record")
    @PostMapping("/log/saveOrUpdate")
    public ApiResponse<Void> userSaveOrUpdate(@RequestBody UserItemParam param) {
        itemBizService.userSaveOrUpdate(param);
        return ApiResponse.success();
    }

    @Operation(summary = "Delete user collection record")
    @PostMapping("/log/delete")
    public ApiResponse<Void> userDelete(@RequestBody UserItemParam param) {
        itemBizService.userDelete(param);
        return ApiResponse.success();
    }

    @Operation(summary = "Toggle exchange status")
    @PostMapping("/log/exchange")
    public ApiResponse<Void> userExchange(@RequestBody UserItemParam param) {
        itemBizService.userExchange(param);
        return ApiResponse.success();
    }

    @Operation(summary = "List exchange user cards")
    @PostMapping("/exchange/cards")
    public ApiListResponse<ExchangeUserCardVO> exchangeCards(@RequestBody ExchangeSearchParam param) {
        return ApiListResponse.success(itemBizService.exchangeCards(param));
    }

    @Operation(summary = "List all exchange items from one user")
    @PostMapping("/exchange/user/items")
    public ApiListResponse<ExchangeItemVO> exchangeUserItems(@RequestBody ExchangeSearchParam param) {
        return ApiListResponse.success(itemBizService.exchangeUserItems(param));
    }

    @Operation(summary = "List my collected records for exchange management")
    @PostMapping("/exchange/my/items")
    public ApiListResponse<ExchangeItemVO> myExchangeItems() {
        return ApiListResponse.success(itemBizService.myExchangeItems());
    }

    @Operation(summary = "Collect all")
    @PostMapping("/collect/all")
    public ApiResponse<Void> collectAll() {
        itemBizService.collectAll();
        return ApiResponse.success();
    }

    @Operation(summary = "Max top")
    @PostMapping("/max/top")
    public ApiListResponse<TopVO> maxTop() {
        return ApiListResponse.success(itemBizService.maxTop());
    }

    @Operation(summary = "Min top")
    @PostMapping("/min/top")
    public ApiListResponse<TopVO> minTop() {
        return ApiListResponse.success(itemBizService.minTop());
    }

    @Operation(summary = "Min hold")
    @PostMapping("/min/hold")
    public ApiListResponse<ItemTopVO> minHold() {
        return ApiListResponse.success(itemBizService.minHold());
    }

    @Operation(summary = "Max reprint")
    @PostMapping("/max/reprint")
    public ApiListResponse<ItemTopVO> maxReprint() {
        return ApiListResponse.success(itemBizService.maxReprint());
    }
}
