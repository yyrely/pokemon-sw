package com.chuncongcong.sw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chuncongcong.sw.bean.vo.ExchangeItemVO;
import com.chuncongcong.sw.bean.vo.ExchangeUserCardVO;
import com.chuncongcong.sw.bean.vo.ItemTopVO;
import com.chuncongcong.sw.bean.vo.TopVO;
import com.chuncongcong.sw.entity.UserItemCollectDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserItemCollectMapper extends BaseMapper<UserItemCollectDO> {

    List<TopVO> maxTop(@Param("itemSize") Integer itemSize);

    List<TopVO> minTop(@Param("itemSize") Integer itemSize);

    List<ItemTopVO> minHold();

    List<ItemTopVO> maxReprint();

    List<ExchangeUserCardVO> listExchangeUsers(@Param("keyword") String keyword,
                                               @Param("offset") Integer offset,
                                               @Param("pageSize") Integer pageSize,
                                               @Param("random") Boolean random);

    List<ExchangeItemVO> listExchangeItemsByUserIds(@Param("userIds") List<Long> userIds,
                                                    @Param("keyword") String keyword);

    List<ExchangeItemVO> listExchangeItemsByUserId(@Param("userId") Long userId,
                                                   @Param("offset") Integer offset,
                                                   @Param("pageSize") Integer pageSize);

    List<ExchangeItemVO> listMyExchangeItems(@Param("userId") Long userId);

}
