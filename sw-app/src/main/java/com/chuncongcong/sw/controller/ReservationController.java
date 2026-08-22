package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.response.ApiListResponse;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.sw.bean.param.ReservationConvertParam;
import com.chuncongcong.sw.bean.param.ReservationParam;
import com.chuncongcong.sw.bean.vo.ReservationVO;
import com.chuncongcong.sw.biz.service.ReservationBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "预定管理")
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationBizService reservationBizService;

    @Operation(summary = "我的预定列表")
    @PostMapping("/list")
    public ApiListResponse<ReservationVO> list() {
        return ApiListResponse.success(reservationBizService.list());
    }

    @Operation(summary = "新增或编辑预定")
    @PostMapping("/saveOrUpdate")
    public ApiResponse<Void> saveOrUpdate(@RequestBody ReservationParam param) {
        reservationBizService.saveOrUpdate(param);
        return ApiResponse.success();
    }

    @Operation(summary = "删除预定")
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody ReservationParam param) {
        reservationBizService.delete(param == null ? null : param.getId());
        return ApiResponse.success();
    }

    @Operation(summary = "预定转收藏")
    @PostMapping("/convert")
    public ApiResponse<Void> convert(@RequestBody ReservationConvertParam param) {
        reservationBizService.convertToCollect(param);
        return ApiResponse.success();
    }
}
