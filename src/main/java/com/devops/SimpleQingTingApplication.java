package com.devops;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

/**
 * 启动程序
 * 
 * @author devops
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ServletComponentScan
public class SimpleQingTingApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(SimpleQingTingApplication.class, args);
        System.out.println("####--- simple-qingting server 启动成功   ---###");
    }
    // #prometheus+grafana+springboot2监控集成配置
    @Bean
    MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
        return meterRegistry1 -> {
            meterRegistry.config()
                    .commonTags("application", "Tenantapp");
        };
    }
    //#prometheus+grafana+springboot2监控集成配置
}