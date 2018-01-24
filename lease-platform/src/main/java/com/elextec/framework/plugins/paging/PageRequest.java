package com.elextec.framework.plugins.paging;

/**
 * 分页请求参数类.
 * Created by wangtao on 2017/10/19.
 */
public class PageRequest {
    /** 当前页码（第一页为1）. */
    private int currPage;
    /** 每页显示记录条数. */
    private int pageSize;
    /** 总记录数. */
    private int total;

    /**
     * 返回当前页首条记录下标.
     * @param currPage 当前页码
     * @param rows 每页显示记录数
     * @return 当前页首条记录的下标
     */
    public int getPageBegin(int currPage, int rows) {
        if (0 >= currPage){
            return 0;
        } else {
            return (currPage - 1) * rows;
        }
    }

    /**
     * 返回当前页首条记录下标（内部参数）.
     * @return 当前页首条记录的下标
     */
    public int getPageBegin() {
        if (0 >= this.currPage){
            return 0;
        } else {
            return (this.currPage - 1) * this.pageSize;
        }
    }

    /*
        以下为Getter和Setter方法.
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
}
