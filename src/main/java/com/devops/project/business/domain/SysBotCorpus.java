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
 * 机器人语料对象 sys_bot_corpus
 *
 * @author chengxiao
 * @date 2022-06-20
 */
@TableName(value = "sys_bot_corpus")
public class SysBotCorpus extends BaseEntity{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /** 列表,标签 */
    @Excel(name = "列表,标签")
    @TableField(value = "category")
    private String category;

    /** 问 */
    @Excel(name = "问")
    @TableField(value = "ask")
    private String ask;

    /** 答 */
    @Excel(name = "答")
    @TableField(value = "answer")
    private String answer;

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
    public void setCategory(String category){
        this.category = category;
    }

    public String getCategory(){
        return category;
    }
    public void setAsk(String ask){
        this.ask = ask;
    }

    public String getAsk(){
        return ask;
    }
    public void setAnswer(String answer){
        this.answer = answer;
    }

    public String getAnswer(){
        return answer;
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
            .append("category", getCategory())
            .append("ask", getAsk())
            .append("answer", getAnswer())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
