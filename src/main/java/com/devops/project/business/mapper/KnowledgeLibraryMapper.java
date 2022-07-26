package com.devops.project.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.project.business.domain.KnowledgeLibrary;

import java.util.List;


/**
 * 知识库Mapper接口
 *
 * @author chengxiao
 * @date 2022-06-06
 */
public interface KnowledgeLibraryMapper extends BaseMapper<KnowledgeLibrary> {

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
    int deleteKnowledgeLibraryByIds(String[] ids);
    }
