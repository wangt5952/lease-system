package com.elextec.framework.plugins.paging;

import com.elextec.framework.common.request.DataPermissionRequest;

/**
 * 分页请求参数类.
 * Created by wangtao on 2017/10/19.
 */
public class PageRequest extends DataPermissionRequest {

    /** SV-UID. */
    private static final long serialVersionUID = 198900007718417487L;

    private static final Integer DEF_CURR_PAGE = 1;
    private static final Integer DEF_PAGE_SIZE = 10;

    /** 当前页码（第一页为1）. */
    private Integer currPage;
    /** 每页显示记录条数. */
    private Integer pageSize;
    /** 总记录数. */
    private Integer total;
    /** 当页首记录下标. */
    private Integer pageBegin;
    /** 是否需要进行分页. */
    private String needPaging;

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
        if (null == currPage || 0 >= currPage){
            this.pageBegin = 0;
        } else {
            this.pageBegin =  (currPage - 1) * pageSize;
        }
        return pageBegin;
    }

    /*
        以下为Getter和Setter方法.
     */
    public Integer getCurrPage() {
        if (null == currPage) {
            this.currPage = DEF_CURR_PAGE;
        }
        return currPage;
    }

    public void setCurrPage(Integer currPage) {
        if (null == currPage) {
            this.currPage = DEF_CURR_PAGE;
        } else {
            this.currPage = currPage;
        }
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            this.pageSize = DEF_PAGE_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (null == pageSize) {
            this.pageSize = DEF_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setPageBegin(Integer pageBegin) {
        this.pageBegin = pageBegin;
    }

    public void setPageBegin() {
        if (0 >= this.currPage){
            this.pageBegin =  0;
        } else {
            this.pageBegin = (this.currPage - 1) * this.pageSize;
        }
    }

    public String getNeedPaging() {
        return needPaging;
    }

    public void setNeedPaging(String needPaging) {
        this.needPaging = needPaging;
    }
}
