package com.chuncongcong.sw.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chuncongcong.framework.bean.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@TableName("sw_user")
@EqualsAndHashCode(callSuper = true)
public class UserDO extends BaseDO {

    private String username;

    private String password;

    private Integer gender;

    private String mobile;

    private String openId;

    private String headImgUrl;
}
