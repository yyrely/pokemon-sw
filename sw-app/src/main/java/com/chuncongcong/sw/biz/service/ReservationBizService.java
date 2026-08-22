package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.param.ReservationConvertParam;
import com.chuncongcong.sw.bean.param.ReservationParam;
import com.chuncongcong.sw.bean.vo.ReservationVO;

import java.util.List;

public interface ReservationBizService {

    List<ReservationVO> list();

    void saveOrUpdate(ReservationParam param);

    void delete(Long id);

    void convertToCollect(ReservationConvertParam param);
}
