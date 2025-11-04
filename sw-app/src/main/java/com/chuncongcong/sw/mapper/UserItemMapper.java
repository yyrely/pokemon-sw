package com.chuncongcong.sw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserItemMapper extends BaseMapper<UserItemDO> {

    List<TopVO> maxTop(@Param("itemSize") Integer itemSize);

    List<TopVO> minTop(@Param("itemSize") Integer itemSize);
}
