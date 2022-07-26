package com.devops.test.mytest1;

import com.alibaba.fastjson.JSON;
import com.devops.project.system.service.IUserService;
import com.devops.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MyTest1 extends BaseTest {

    @Autowired
    private IUserService userService;

    @Test
    public void testSelectUserById() {
        System.out.println(JSON.toJSONString(userService.selectUserById(111L)));

    }

}
