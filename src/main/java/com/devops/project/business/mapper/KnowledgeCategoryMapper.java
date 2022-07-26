package com.devops.project.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.project.business.domain.KnowledgeCategory;

import java.util.List;


/**
 * 知识分类Mapper接口
 *
 * @author chengxiao
 * @date 2022-06-06
 */
public interface KnowledgeCategoryMapper extends BaseMapper<KnowledgeCategory> {

    /**
     * 查询知识分类列表
     *
     * @param knowledgeCategory 知识分类
     * @return 知识分类集合
     */
    List<KnowledgeCategory> selectKnowledgeCategoryList(KnowledgeCategory knowledgeCategory);



    /**
     * 批量删除知识分类
     *
     * @param categoryIds 需要删除的数据ID
     * @return 结果
     */
    int deleteKnowledgeCategoryByIds(String[] categoryIds);
    }
