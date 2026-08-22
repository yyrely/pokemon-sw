package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.ReservationDO;

import java.util.List;

public interface ReservationService extends IService<ReservationDO> {

    List<ReservationDO> listActiveByUserId(Long userId);
}
