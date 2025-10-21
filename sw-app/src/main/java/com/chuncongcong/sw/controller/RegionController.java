package com.chuncongcong.sw.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chuncongcong.framework.response.ApiListResponse;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.RegionParam;
import com.chuncongcong.sw.bean.vo.ReginItemCountVO;
import com.chuncongcong.sw.bean.vo.RegionVO;
import com.chuncongcong.sw.biz.service.ReginBizService;
import com.chuncongcong.sw.entity.RegionDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "地区管理")
@Slf4j
@RestController
@RequestMapping("/region")
public class RegionController {

    @Resource
    private ReginBizService reginBizService;

    @Operation(summary = "根据ID查询地区")
    @PostMapping("/info")
    public ApiResponse<RegionVO> info(@RequestBody RegionParam param) {
        return ApiResponse.success(reginBizService.info(param));
    }

    @Operation(summary = "查询所有地区列表")
    @PostMapping("/list")
    public ApiListResponse<RegionVO> list() {
        return ApiListResponse.success(reginBizService.listAndCollect());
    }

    @Operation(summary = "地区物品收集统计")
    @PostMapping("/collect/count")
    public ApiResponse<ReginItemCountVO> collectCount(@RequestBody RegionParam param) {
        return ApiResponse.success(reginBizService.collectCount(param));
    }

}
