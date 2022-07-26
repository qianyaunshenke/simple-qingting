package com.devops.framework.config;

import com.devops.framework.config.properties.WxMpProperties;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
public class WxMpConfiguration {

    @Resource
    private WxMpProperties properties;

    @Bean
    public WxMpService wxMpService() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(properties.getAppid());
        config.setSecret(properties.getSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());
        WxMpService service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(config);
        return service;
    }
}
