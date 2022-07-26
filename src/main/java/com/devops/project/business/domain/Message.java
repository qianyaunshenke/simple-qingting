package com.devops.project.business.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.devops.framework.aspectj.lang.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 会话对象 message
 *
 * @author chengxiao
 * @date 2022-06-29
 */
@TableName(value = "message")
public class Message {
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /** 客服 */
    @Excel(name = "客服")
    @TableField(value = "kefu_id")
    private String kefuId;

    /** 访客 */
    @Excel(name = "访客")
    @TableField(value = "visitor_id")
    private String visitorId;

    /** 内容 */
    @Excel(name = "内容")
    @TableField(value = "content")
    private String content;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(value = "created_at")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(value = "updated_at")
    private Date updatedAt;

    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "删除时间", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField(value = "deleted_at")
    private Date deletedAt;

    /** 会话类型 */
    @Excel(name = "会话类型")
    @TableField(value = "mes_type")
    private String mesType;

    /** 状态 */
    @Excel(name = "状态")
    @TableField(value = "status")
    private String status;

    /** 企业id */
    @Excel(name = "企业id")
    @TableField(value = "ent_id")
    private Long entId;

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }
    public void setKefuId(String kefuId){
        this.kefuId = kefuId;
    }

    public String getKefuId(){
        return kefuId;
    }
    public void setVisitorId(String visitorId){
        this.visitorId = visitorId;
    }

    public String getVisitorId(){
        return visitorId;
    }
    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }
    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }

    public Date getCreatedAt(){
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt){
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt(){
        return updatedAt;
    }
    public void setDeletedAt(Date deletedAt){
        this.deletedAt = deletedAt;
    }

    public Date getDeletedAt(){
        return deletedAt;
    }
    public void setMesType(String mesType){
        this.mesType = mesType;
    }

    public String getMesType(){
        return mesType;
    }
    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
    public void setEntId(Long entId){
        this.entId = entId;
    }

    public Long getEntId(){
        return entId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("kefuId", getKefuId())
            .append("visitorId", getVisitorId())
            .append("content", getContent())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("deletedAt", getDeletedAt())
            .append("mesType", getMesType())
            .append("status", getStatus())
            .append("entId", getEntId())
            .toString();
    }
}
