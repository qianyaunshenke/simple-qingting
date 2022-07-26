package com.devops.project.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.utils.text.Convert;
import com.devops.project.business.domain.KnowledgeCategory;
import com.devops.project.business.mapper.KnowledgeCategoryMapper;
import com.devops.project.business.service.IKnowledgeCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识分类Service业务层处理
 *
 * @author chengxiao
 * @date 2022-06-06
 */
@Service
public class KnowledgeCategoryServiceImpl extends ServiceImpl<KnowledgeCategoryMapper, KnowledgeCategory> implements IKnowledgeCategoryService {


    /**
     * 查询知识分类列表
     *
     * @param knowledgeCategory 知识分类
     * @return 知识分类
     */
    @Override
    public List<KnowledgeCategory> selectKnowledgeCategoryList(KnowledgeCategory knowledgeCategory) {
        return getBaseMapper().selectKnowledgeCategoryList(knowledgeCategory);
    }





    /**
     * 删除知识分类对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteKnowledgeCategoryByIds(String ids) {
        return getBaseMapper().deleteKnowledgeCategoryByIds(Convert.toStrArray(ids));
    }




        
}
