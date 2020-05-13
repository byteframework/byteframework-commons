package com.byteframework.commons.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 2152283685277340982L;


    protected Long id;
    //分页每页的记录数
    @JSONField(serialize = false)
    protected int pageSize = 10;
    //分页当前查询页序号，从1开始
    @JSONField(serialize = false)
    protected int pageNo = 1;
    //创建时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected Date createDate;
    //创建人
    @JSONField(serialize = false)
    protected String createBy;
    //修改时间
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected Date updateDate;
    //修改人
    // @JSONField(serialize = false)
    protected String updateBy;

    //查询时使用
    @JSONField(serialize = false)
    protected Date beginTime;
    @JSONField(serialize = false)
    protected Date endTime;

    @JSONField(serialize = false)
    protected Object loginUser;

    public Object getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(Object loginUser) {
        this.loginUser = loginUser;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 根据当前页序号及每页记录数计算查询偏移量
     *
     * @return
     */
    @JSONField(serialize = false)
    public int getOffset() {
        return (this.pageNo - 1) * this.pageSize;
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
