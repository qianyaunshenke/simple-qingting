package com.devops.project.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.project.business.domain.SysStopWord;

import java.util.List;

/**
 * 违禁词Service接口
 *
 * @author chengxiao
 * @date 2022-06-15
 */
public interface ISysStopWordService extends IService<SysStopWord>{

    /**
     * 查询违禁词列表
     *
     * @param sysStopWord 违禁词
     * @return 违禁词集合
     */
    List<SysStopWord> selectSysStopWordList(SysStopWord sysStopWord);



    /**
     * 批量删除违禁词
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteSysStopWordByIds(String ids);


    }
