package com.devops.project.websocket.server;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseWebSocket {
    /**
     * 解析websocket url 参数
     * @param queryString
     * @return
     */
    protected Map<String, String> parseParams(String queryString) {

        Map<String, String> resultMap = new HashMap<>();

        String[] splitArr = queryString.split("&");
        for (String str : splitArr) {
            String[] strArr = str.split("=");
            resultMap.put(strArr[0], strArr[1]);
        }
        return resultMap;
    }
}
