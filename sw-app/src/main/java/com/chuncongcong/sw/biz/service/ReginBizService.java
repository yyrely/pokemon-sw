package com.chuncongcong.sw.biz.service;

import com.chuncongcong.sw.bean.param.RegionParam;
import com.chuncongcong.sw.bean.vo.ReginItemCountVO;
import com.chuncongcong.sw.bean.vo.RegionVO;

import java.util.List;

public interface ReginBizService {

    RegionVO info(RegionParam param);

    List<RegionVO> listAndCollect();

    ReginItemCountVO collectCount(RegionParam param);

}
