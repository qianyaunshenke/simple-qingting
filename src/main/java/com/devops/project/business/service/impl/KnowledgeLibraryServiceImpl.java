package com.devops.project.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.project.business.domain.KnowledgeLibrary;
import com.devops.project.business.mapper.KnowledgeLibraryMapper;
import com.devops.project.business.service.IKnowledgeLibraryService;
import com.devops.common.utils.text.Convert;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识库Service业务层处理
 *
 * @author chengxiao
 * @date 2022-06-06
 */
@Service
public class KnowledgeLibraryServiceImpl extends ServiceImpl<KnowledgeLibraryMapper, KnowledgeLibrary> implements IKnowledgeLibraryService {


    /**
     * 查询知识库列表
     *
     * @param knowledgeLibrary 知识库
     * @return 知识库
     */
    @Override
    public List<KnowledgeLibrary> selectKnowledgeLibraryList(KnowledgeLibrary knowledgeLibrary) {
        return getBaseMapper().selectKnowledgeLibraryList(knowledgeLibrary);
    }





    /**
     * 删除知识库对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteKnowledgeLibraryByIds(String ids) {
        return getBaseMapper().deleteKnowledgeLibraryByIds(Convert.toStrArray(ids));
    }




        
}
