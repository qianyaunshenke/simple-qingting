package com.devops.project.business.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.devops.framework.aspectj.lang.annotation.Excel;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 访客会话对象 visitor
 *
 * @author chengxiao
 * @date 2022-06-29
 */
@TableName(value = "visitor")
public class Visitor {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /** 昵称 */
    @Excel(name = "昵称")
    @TableField(value = "name")
    private String name;

    /** 真实名称 */
    @Excel(name = "真实名称")
    @TableField(value = "real_name")
    private String realName;

    /** 头像 */
    @Excel(name = "头像")
    @TableField(value = "avator")
    private String avator;

    /** 来源ip */
    @Excel(name = "来源ip")
    @TableField(value = "source_ip")
    private String sourceIp;

    /** 客服id */
    @Excel(name = "客服id")
    @TableField(value = "to_id")
    private String toId;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(value = "created_at")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(value = "updated_at")
    private Date updatedAt;

    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "删除时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(value = "deleted_at")
    private Date deletedAt;

    /** 访客id */
    @Excel(name = "访客id")
    @TableField(value = "visitor_id")
    private String visitorId;

    /** 状态 */
    @Excel(name = "状态")
    @TableField(value = "status")
    private Integer status;

    /** 参考信息 */
    @Excel(name = "参考信息")
    @TableField(value = "refer")
    private String refer;

    /** 城市 */
    @Excel(name = "城市")
    @TableField(value = "city")
    private String city;

    /** 客户端ip */
    @Excel(name = "客户端ip")
    @TableField(value = "client_ip")
    private String clientIp;

    /** 扩展信息 */
    @Excel(name = "扩展信息")
    @TableField(value = "extra")
    private String extra;

    /** 企业id */
    @Excel(name = "企业id")
    @TableField(value = "ent_id")
    private Integer entId;

    /** 访问次数 */
    @Excel(name = "访问次数")
    @TableField(value = "visit_num")
    private Integer visitNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getEntId() {
        return entId;
    }

    public void setEntId(Integer entId) {
        this.entId = entId;
    }

    public Integer getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(Integer visitNum) {
        this.visitNum = visitNum;
    }
}
