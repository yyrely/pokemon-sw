package com.chuncongcong.sw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuncongcong.sw.entity.ReservationDO;
import com.chuncongcong.sw.mapper.ReservationMapper;
import com.chuncongcong.sw.service.ReservationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, ReservationDO> implements ReservationService {

    @Override
    public List<ReservationDO> listActiveByUserId(Long userId) {
        return lambdaQuery()
                .eq(ReservationDO::getUserId, userId)
                .eq(ReservationDO::getStatus, 1)
                .orderByAsc(ReservationDO::getArrivalDate)
                .orderByDesc(ReservationDO::getCreateTime)
                .list();
    }
}
