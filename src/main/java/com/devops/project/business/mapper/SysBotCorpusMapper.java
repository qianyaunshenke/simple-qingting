package com.devops.project.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.devops.project.business.domain.SysBotCorpus;

import java.util.List;


/**
 * 机器人语料Mapper接口
 *
 * @author chengxiao
 * @date 2022-06-20
 */
public interface SysBotCorpusMapper extends BaseMapper<SysBotCorpus> {

    /**
     * 查询机器人语料列表
     *
     * @param sysBotCorpus 机器人语料
     * @return 机器人语料集合
     */
    List<SysBotCorpus> selectSysBotCorpusList(SysBotCorpus sysBotCorpus);



    /**
     * 批量删除机器人语料
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteSysBotCorpusByIds(String[] ids);
    }
