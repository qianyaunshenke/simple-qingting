package com.devops.project.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.project.business.domain.KnowledgeCategory;

import java.util.List;

/**
 * 知识分类Service接口
 *
 * @author chengxiao
 * @date 2022-06-06
 */
public interface IKnowledgeCategoryService extends IService<KnowledgeCategory>{

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
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteKnowledgeCategoryByIds(String ids);


    }
