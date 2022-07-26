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
 * 知识库对象 knowledge_library
 *
 * @author chengxiao
 * @date 2022-06-06
 */
@TableName(value = "knowledge_library")
public class KnowledgeLibrary extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /** 分类 */
    @Excel(name = "分类")
    @TableField(value = "category_id")
    private Long categoryId;

    /** 标题 */
    @Excel(name = "标题")
    @TableField(value = "title")
    private String title;

    /** 关键词多个逗号分隔 */
    @Excel(name = "关键词多个逗号分隔")
    @TableField(value = "key_word")
    private String keyWord;

    /** 内容详情 */
    @Excel(name = "内容详情")
    @TableField(value = "content")
    private String content;

    /** 删除状态0正常1已删除 */
    @Excel(name = "删除状态0正常1已删除")
    @TableField(value = "del_status")
    private Long delStatus;
    /** 部门ID */
    @Excel(name = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }
    public void setCategoryId(Long categoryId){
        this.categoryId = categoryId;
    }

    public Long getCategoryId(){
        return categoryId;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }

    public String getKeyWord(){
        return keyWord;
    }
    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return content;
    }
    public void setDelStatus(Long delStatus){
        this.delStatus = delStatus;
    }

    public Long getDelStatus(){
        return delStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("categoryId", getCategoryId())
            .append("title", getTitle())
            .append("keyWord", getKeyWord())
            .append("content", getContent())
            .append("delStatus", getDelStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
