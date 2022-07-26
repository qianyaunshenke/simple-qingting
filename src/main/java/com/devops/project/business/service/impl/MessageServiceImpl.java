package com.devops.project.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.devops.project.business.domain.Message;
import com.devops.project.business.mapper.MessageMapper;
import com.devops.project.business.service.IMessageService;
import com.devops.common.utils.text.Convert;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会话Service业务层处理
 *
 * @author chengxiao
 * @date 2022-06-29
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {


    /**
     * 查询会话列表
     *
     * @param message 会话
     * @return 会话
     */
    @Override
    public List<Message> selectMessageList(Message message) {
        return getBaseMapper().selectMessageList(message);
    }





    /**
     * 删除会话对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteMessageByIds(String ids) {
        return getBaseMapper().deleteMessageByIds(Convert.toStrArray(ids));
    }




        
}
