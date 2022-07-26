package com.devops.project.business.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.devops.framework.aspectj.lang.annotation.Excel;
import com.devops.framework.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 违禁词对象 sys_stop_word
 *
 * @author chengxiao
 * @date 2022-06-15
 */
@TableName(value = "sys_stop_word")
public class SysStopWord extends BaseEntity{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /** 违禁词 */
    @Excel(name = "违禁词")
    @TableField(value = "content")
    private String content;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    @TableField(value = "status")
    private Integer status;

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }
    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }
    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus(){
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("content", getContent())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
