package com.devops.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * 
 * @author devops
 */
@Component
@ConfigurationProperties(prefix = "zshy")
public class GlobalConfig
{
    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 版权年份 */
    private String copyrightYear;

    /** 实例演示开关 */
    private boolean demoEnabled;

    /** 上传路径 */
    private static String resource;

    /** 获取地址开关 */
    private static boolean addressEnabled;
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getCopyrightYear()
    {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear)
    {
        this.copyrightYear = copyrightYear;
    }

    public boolean isDemoEnabled()
    {
        return demoEnabled;
    }

    public void setDemoEnabled(boolean demoEnabled)
    {
        this.demoEnabled = demoEnabled;
    }

    public static String getResource()
    {
        return resource;
    }

    public void setResource(String resource)
    {
        GlobalConfig.resource = resource;
    }

    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled)
    {
        GlobalConfig.addressEnabled = addressEnabled;
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath()
    {
        return getResource() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath()
    {
        return getResource() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getResource() + "/upload";
    }
}