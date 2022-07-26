package com.devops.project.business.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.devops.project.business.service.IQingtingQiaoService;
import com.devops.project.business.vo.QingtingQiaoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class QingtingQiaoServiceImpl implements IQingtingQiaoService {

    @Value(value = "${qingtingqiao.host}")
    private String host;
    @Value(value = "${qingtingqiao.secret}")
    private String secret;

    @Override
    public String getToken(Long userId) {
        String password = new Md5Hash(userId + secret).toHex();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userID", userId);
        paramsMap.put("password", password);
        String token = null;
        try {
            String respStr = HttpUtil.post(host + "/innercheck", paramsMap);
            QingtingQiaoResult<JSONObject> resultData = JSONUtil.toBean(respStr, QingtingQiaoResult.class);
            token = resultData.getResult().get("token", String.class);
        } catch (Exception e) {
            log.error("getToken error", e);
            e.printStackTrace();
        }
        return token;
    }

    @Override
    public void syncStopword() {
        String respStr = HttpUtil.get(host + "/servclusterstopwordsync");
        log.info("同步敏感词响应内容：" + respStr);
    }

    @Override
    public void syncBotCorpus() {
        String respStr = HttpUtil.get(host + "/servclusterbotcorpussync");
        log.info("同步机器人语料响应内容：" + respStr);
    }

    /**
     * 自定义短信内容发送
     * @param phone
     * @param content
     * @return
     */
    @Override
    public boolean sendSms(String phone, String content) {
        try {

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("UserName", "zshyhy");
            paramMap.put("UserPass", "zshy@1qa2ws");
            paramMap.put("Mobile", phone);
            paramMap.put("Content", content);
            String result = HttpUtil.post("http://115.28.112.245:8082/SendMT/SendMessage", paramMap, 30000);
            log.info("sendSms 返回内容：{}", result);
            String[] split = result.split(",");
            if (split.length > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("sendSms发生异常错误", e);
            e.printStackTrace();
        }
        return false;
    }
}
