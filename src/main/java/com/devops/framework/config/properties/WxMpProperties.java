package com.devops.framework.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "wx.pub")
public class WxMpProperties {
    /**
     * 设置微信公众号的appid
     */
    private String appid;

    /**
     * 设置微信公众号的Secret
     */
    private String secret;

    /**
     * 设置微信公众号消息服务器配置的token
     */
    private String token;

    /**
     * 设置微信公众号消息服务器配置的EncodingAESKey
     */
    private String aesKey;


}
