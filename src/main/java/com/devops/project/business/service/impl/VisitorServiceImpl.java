package com.devops.project.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.common.utils.text.Convert;
import com.devops.project.business.domain.Visitor;
import com.devops.project.business.mapper.VisitorMapper;
import com.devops.project.business.service.IVisitorService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 访客会话Service业务层处理
 *
 * @author chengxiao
 * @date 2022-06-29
 */
@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements IVisitorService {


    /**
     * 查询访客会话列表
     *
     * @param visitor 访客会话
     * @return 访客会话
     */
    @Override
    public List<Visitor> selectVisitorList(Visitor visitor) {
        return getBaseMapper().selectVisitorList(visitor);
    }

    /**
     * 删除访客会话对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteVisitorByIds(String ids) {
        return getBaseMapper().deleteVisitorByIds(Convert.toStrArray(ids));
    }

    @Override
    public Visitor getByVisitorId(String visitorId) {

        List<Visitor> visitorList = getBaseMapper().selectList(new QueryWrapper<Visitor>().eq("visitor_id", visitorId));
        if (!CollectionUtils.isEmpty(visitorList)) {
            return visitorList.get(0);
        } else {
            return null;
        }
    }

}
