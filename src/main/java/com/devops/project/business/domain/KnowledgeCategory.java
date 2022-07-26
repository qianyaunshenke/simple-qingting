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
 * 知识分类对象 knowledge_category
 *
 * @author chengxiao
 * @date 2022-06-06
 */
@TableName(value = "knowledge_category")
public class KnowledgeCategory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "category_id",type = IdType.AUTO)
    private Long categoryId;

    /** 父id */
    @Excel(name = "父id")
    @TableField(value = "pid")
    private Long pid;

    /** 类别 */
    @Excel(name = "类别")
    @TableField(value = "category_name")
    private String categoryName;

    /** 排序 */
    @Excel(name = "排序")
    @TableField(value = "sort")
    private Long sort;
    /** 部门ID */
    @Excel(name = "部门ID")
    @TableField(value = "dept_id")
    private Long deptId;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setCategoryId(Long categoryId){
        this.categoryId = categoryId;
    }

    public Long getCategoryId(){
        return categoryId;
    }
    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName(){
        return categoryName;
    }
    public void setSort(Long sort){
        this.sort = sort;
    }

    public Long getSort(){
        return sort;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("sort", getSort())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
