package com.chuncongcong.sw.client.dto;

import lombok.Data;

@Data
public class WxSessionDTO {

    private String openid;

    private String session_key;

    private String unionid;

    private Integer errcode;

    private String errmsg;
}
