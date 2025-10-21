package com.chuncongcong.framework.response;

import lombok.Data;

import java.util.List;

@Data
public class PageData<T> {
    private long total;
    private int pageNum;
    private int pageSize;
    private List<T> list;

    public PageData(long total, int pageNum, int pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
    }
}

