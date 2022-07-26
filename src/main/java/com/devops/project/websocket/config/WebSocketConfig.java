package com.devops.project.websocket.config;

import com.devops.project.business.service.IVisitorService;
import com.devops.project.websocket.server.VisitorSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebScoket配置处理器
 */
@Configuration
public class WebSocketConfig {
    /**
     * ServerEndpointExporter 作用
     * <p>
     * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    public void setVisitorService(IVisitorService visitorService) {
        VisitorSocketServer.visitorService = visitorService;
    }

}
