package com.devops.project.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.project.business.domain.SysBotCorpus;
import com.devops.project.business.mapper.SysBotCorpusMapper;
import com.devops.project.business.service.ISysBotCorpusService;
import com.devops.common.utils.text.Convert;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 机器人语料Service业务层处理
 *
 * @author chengxiao
 * @date 2022-06-20
 */
@Service
public class SysBotCorpusServiceImpl extends ServiceImpl<SysBotCorpusMapper, SysBotCorpus> implements ISysBotCorpusService {


    /**
     * 查询机器人语料列表
     *
     * @param sysBotCorpus 机器人语料
     * @return 机器人语料
     */
    @Override
    public List<SysBotCorpus> selectSysBotCorpusList(SysBotCorpus sysBotCorpus) {
        return getBaseMapper().selectSysBotCorpusList(sysBotCorpus);
    }





    /**
     * 删除机器人语料对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSysBotCorpusByIds(String ids) {
        return getBaseMapper().deleteSysBotCorpusByIds(Convert.toStrArray(ids));
    }




        
}
