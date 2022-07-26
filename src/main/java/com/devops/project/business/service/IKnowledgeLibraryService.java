package com.devops.project.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.project.business.domain.KnowledgeLibrary;

import java.util.List;

/**
 * 知识库Service接口
 *
 * @author chengxiao
 * @date 2022-06-06
 */
public interface IKnowledgeLibraryService extends IService<KnowledgeLibrary>{

    /**
     * 查询知识库列表
     *
     * @param knowledgeLibrary 知识库
     * @return 知识库集合
     */
    List<KnowledgeLibrary> selectKnowledgeLibraryList(KnowledgeLibrary knowledgeLibrary);



    /**
     * 批量删除知识库
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteKnowledgeLibraryByIds(String ids);


    }
