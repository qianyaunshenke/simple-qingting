package com.devops.common.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtils extends cn.hutool.core.util.ObjectUtil {

    public static <T> String fieldHump2Underline(T object) {
        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        String json = JSON.toJSONString(object, config);
        return json;
    }

    /**
     * 驼峰属性下划线map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> object2UnderlineMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(StrUtil.toUnderlineCase(field.getName()), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
