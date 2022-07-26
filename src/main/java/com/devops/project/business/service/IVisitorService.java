package com.devops.project.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.devops.project.business.domain.Visitor;

import java.util.List;

/**
 * 访客会话Service接口
 *
 * @author chengxiao
 * @date 2022-06-29
 */
public interface IVisitorService extends IService<Visitor>{

    /**
     * 查询访客会话列表
     *
     * @param visitor 访客会话
     * @return 访客会话集合
     */
    List<Visitor> selectVisitorList(Visitor visitor);



    /**
     * 批量删除访客会话
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteVisitorByIds(String ids);

    Visitor getByVisitorId(String visitorId);


    }
