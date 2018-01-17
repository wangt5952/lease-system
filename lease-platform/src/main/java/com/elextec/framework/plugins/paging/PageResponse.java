package com.elextec.framework.plugins.paging;

import java.util.List;

/**
 * 分页查询结果类.
 * Created by wangtao on 2017/10/19.
 */
public class PageResponse<T> {
    /** 当前页码（第一页为1）. */
    private int currPage;
    /** 每页显示记录条数. */
    private int pageSize;
    /** 总记录数. */
    private int total;
    /** 记录明细. */
    private List<T> rows;

    /*
        以下是Getter和Setter方法.
     */
    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
