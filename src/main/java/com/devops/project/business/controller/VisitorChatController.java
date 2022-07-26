package com.devops.project.business.controller;

import cn.hutool.core.util.URLUtil;
import com.devops.common.utils.AddressUtils;
import com.devops.common.utils.IpUtils;
import com.devops.common.utils.ServletUtils;
import com.devops.common.utils.StringUtils;
import com.devops.common.utils.uuid.UUID;
import com.devops.project.api.vo.VisitorLoginForm;
import com.devops.project.business.domain.Visitor;
import com.devops.project.business.service.IVisitorService;
import com.devops.project.system.domain.User;
import com.devops.project.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/demo/visitorChat")
public class VisitorChatController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IVisitorService visitorService;

    private String prefix = "business/chat";


    @GetMapping()
    public String chat(VisitorLoginForm loginForm, ModelMap mmap) {
        Visitor visitor = getVisitor(loginForm);
        mmap.put("visitorName", visitor.getName());
        mmap.put("visitorId", visitor.getVisitorId());
        mmap.put("kefuId", visitor.getToId());


        return prefix + "/visitorChat";
    }

    @RequestMapping("/kefuInfo/{kefuId}")
    @ResponseBody
    public String onlineVisitors(@PathVariable("kefuId") String kefuId) {
        User user = userService.selectUserByLoginName(kefuId);
        return user.getLoginName();
    }


    public Visitor getVisitor(VisitorLoginForm loginForm) {
        String visitor_id = loginForm.getVisitor_id();
        String avator = loginForm.getAvator();
        String city_address = loginForm.getCity_address();
        String visitor_name = loginForm.getVisitor_name();
        if (StringUtils.isEmpty(loginForm.getVisitor_id())) {
            //生成visitorId
            visitor_id = UUID.randomUUID().toString();
        }
//        visitor_id = String.format("%s|%s", loginForm.getDept_id(), visitor_id);
        if (StringUtils.isEmpty(avator)) {
            avator = ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")) ? "/static/images/phone.png" : "/static/images/computer.png";
        } else {
            avator = URLUtil.decode(avator);
        }
        String clientIp = IpUtils.getIpAddr(ServletUtils.getRequest());
        String address = AddressUtils.getRealAddressByIP(clientIp);
        if (StringUtils.isEmpty(visitor_name)) {
            visitor_name = address;
        } else {
            visitor_name = URLUtil.decode(visitor_name);
        }
        User destKefuInfo = userService.selectUserByLoginName(loginForm.getTo_id());
        if (destKefuInfo == null) {
            destKefuInfo = userService.selectUserByLoginName("admin");
        }
        Visitor visitor = visitorService.getByVisitorId(visitor_id);
        Integer visitNum = 1;
        if (visitor != null) {

            //更新访客信息
            visitNum = visitor.getVisitNum() + 1;
            visitor.setToId(destKefuInfo.getLoginName());
            visitor.setClientIp(clientIp);
            visitor.setUpdatedAt(new Date());
            visitor.setVisitNum(visitNum);
            visitorService.updateById(visitor);
        } else {
            //创建访客信息
            visitor = new Visitor();
            visitor.setVisitorId(visitor_id);
            visitor.setName(visitor_name);
            visitor.setAvator(avator);
            visitor.setToId(destKefuInfo.getLoginName());
            visitor.setSourceIp(clientIp);
            visitor.setStatus(1);
            visitor.setClientIp(clientIp);
            visitor.setRefer(loginForm.getRefer());
            visitor.setCity(city_address);
            visitor.setEntId(loginForm.getEnt_id());
            visitor.setVisitNum(visitNum);
            visitor.setCreatedAt(new Date());
            visitorService.save(visitor);
        }
        return visitor;
    }

}
