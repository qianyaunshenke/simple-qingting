package com.devops.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ClassName      SmsConfig
 * Description
 * Author         yanghf
 * Date           2020/8/13 16:46
 * Version        1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "config.sms")
public class SmsConfig {
    private String url;
    private String password;
    private String userName;
    private String accessKeyId;
    private String accessKeySecret;
    private String regionId;
    private String signName;
    private String templateCode;
}
