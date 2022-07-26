package com.devops.project.api;

import cn.hutool.core.util.URLUtil;
import com.devops.common.utils.*;
import com.devops.common.utils.uuid.UUID;
import com.devops.project.api.vo.ApiResult;
import com.devops.project.api.vo.VisitorLoginForm;
import com.devops.project.business.domain.Visitor;
import com.devops.project.business.service.IVisitorService;
import com.devops.project.system.domain.User;
import com.devops.project.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class SimpleQingTingApi {
    @Autowired
    private IUserService userService;
    @Autowired
    private IVisitorService visitorService;

    @PostMapping("/visitor_login")
    public Map<String, Object> visitor_login(VisitorLoginForm loginForm) {

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
//        User entKefuInfo = userService.selectUserByLoginName(loginForm.getEnt_id());
//        if (entKefuInfo == null || "1".equals(entKefuInfo.getStatus())) {
//            return ApiResult.error();
//        }
        User destKefuInfo = userService.selectUserByLoginName(loginForm.getTo_id());
        if (destKefuInfo == null) {
            return ApiResult.error("客服id不存在");
        }
        Visitor visitor = visitorService.getByVisitorId(visitor_id);
        Integer visitNum = 1;
        if (visitor != null) {

            //更新访客信息
            visitNum = visitor.getVisitNum() + 1;
            visitor.setToId(loginForm.getTo_id());
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
            visitor.setToId(loginForm.getTo_id());
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
        String noticeContent = String.format("%s访问%s次", visitor_name, visitNum);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("alloffline", true);
        resultMap.put("code", 200);
        Map<String, Object> kefuResult = new HashMap<>();
        kefuResult.put("username", destKefuInfo.getLoginName());
        kefuResult.put("avatar", destKefuInfo.getAvatar());
        resultMap.put("kefu", kefuResult);
        resultMap.put("msg", "ok");
        //对象驼峰属性下划线map
        resultMap.put("result", ObjectUtils.object2UnderlineMap(visitor));

        return resultMap;
    }

    @RequestMapping("/2/messages_page")
    public ApiResult messages_page(VisitorLoginForm loginForm) {
        return ApiResult.success();
    }

    @RequestMapping("/other/getTopQuestion")
    public ApiResult getTopQuestion(VisitorLoginForm loginForm) {
        return ApiResult.success();
    }

    @RequestMapping("/2/notices")
    public ApiResult notices(VisitorLoginForm loginForm) {
        return ApiResult.success();
    }

    public static void main(String[] args) {
        System.out.println(String.format("%s|%s", 12, 11111));
    }
}
