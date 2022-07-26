package com.devops.project.business.controller;

import com.devops.common.utils.security.ShiroUtils;
import com.devops.project.websocket.server.ChatWebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/business/kefuChat")
public class KefuChatController {

    private String prefix = "business/chat";


    @GetMapping()
    public String chat(ModelMap mmap) {
        mmap.put("userName", ShiroUtils.getLoginName());
        return prefix + "/kefuChat";
    }


    @RequestMapping("/onlineVisitors")
    @ResponseBody
    public Set<String> onlineVisitors(@RequestParam("currentUser") String currentUser) {
        ConcurrentHashMap<String, Session> map = ChatWebSocketServer.getSessionPools();
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        Set<String> nameset = new HashSet<String>();
        while (it.hasNext()) {
            String entry = it.next();
            if (!entry.equals(currentUser))
                nameset.add(entry);
        }
        return nameset;
    }

}
