package com.devops.project.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.utils.text.Convert;
import com.devops.project.business.domain.SysStopWord;
import com.devops.project.business.mapper.SysStopWordMapper;
import com.devops.project.business.service.ISysStopWordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 违禁词Service业务层处理
 *
 * @author chengxiao
 * @date 2022-06-15
 */
@Service
public class SysStopWordServiceImpl extends ServiceImpl<SysStopWordMapper, SysStopWord> implements ISysStopWordService {


    /**
     * 查询违禁词列表
     *
     * @param sysStopWord 违禁词
     * @return 违禁词
     */
    @Override
    public List<SysStopWord> selectSysStopWordList(SysStopWord sysStopWord) {
        return getBaseMapper().selectSysStopWordList(sysStopWord);
    }





    /**
     * 删除违禁词对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSysStopWordByIds(String ids) {
        return getBaseMapper().deleteSysStopWordByIds(Convert.toStrArray(ids));
    }




        
}
