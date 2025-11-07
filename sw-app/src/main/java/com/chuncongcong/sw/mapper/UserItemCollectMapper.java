package com.chuncongcong.sw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserItemCollectMapper extends BaseMapper<UserItemCollectDO> {

    List<TopVO> maxTop(@Param("itemSize") Integer itemSize);

    List<TopVO> minTop(@Param("itemSize") Integer itemSize);
}
