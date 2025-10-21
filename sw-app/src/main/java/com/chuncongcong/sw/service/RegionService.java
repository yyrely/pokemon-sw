package com.chuncongcong.sw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chuncongcong.sw.entity.RegionDO;

import java.util.List;

public interface RegionService extends IService<RegionDO> {

    /**
     * 根据世代查询地区列表
     *
     * @param generation 世代
     * @return 地区列表
     */
    List<RegionDO> listByGeneration(String generation);

    /**
     * 根据编码查询地区
     *
     * @param code 编码
     * @return 地区信息
     */
    RegionDO getByCode(String code);

    List<RegionDO> listOrderBySort();

}
