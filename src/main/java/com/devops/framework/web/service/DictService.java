package com.devops.framework.web.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import com.devops.project.system.domain.Dept;
import com.devops.project.system.domain.DictData;
import com.devops.project.system.service.IDeptService;
import com.devops.project.system.service.IDictDataService;
import com.devops.project.system.service.IDictTypeService;
import me.chanjar.weixin.common.util.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * QYSK首创 html调用 thymeleaf 实现字典读取
 *
 * @author devops
 */
@Service("dict")
public class DictService {
    @Autowired
    private IDictTypeService dictTypeService;

    @Autowired
    private IDictDataService dictDataService;
    @Autowired
    private IDeptService deptService;

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param dictType 字典类型
     * @return 参数键值
     */
    public List<DictData> getType(String dictType) {
        return dictTypeService.selectDictDataByType(dictType);
    }

    /**
     * 根据字典类型查询字典数据信息
     *
     * @param scheduleType 调度周期
     * @return 参数键值
     */
    public List<DictData> getTimeList(Integer scheduleType) {
        int start=0,end =0;
        String unit=null;
        switch (scheduleType){
            case 1 :
               // 按季度
                start =DateUtil.quarterEnum(new Date()).getValue();
                end =4;
                unit ="季度";
                break;
            case 2:
                //按月
                start=DateUtil.thisMonth();
                end =12;
                unit="月份";
                break;
            case 3:
                start=DateUtil.weekOfYear(new Date());
                DateTime endOfYear = DateUtil.endOfYear(new Date());
                end =getWeekOfYear(endOfYear);
                unit="周";
                break;
        }

        List<DictData> scheduleTimes=new ArrayList<>();
        for (int i = start; i <=end ; i++) {
            DictData dict =new DictData();
            dict.setDictLabel(i+unit);
            dict.setDictValue(i+unit);
            scheduleTimes.add(dict);
        }
        return scheduleTimes;
    }

    public int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    public String getLabel(String dictType, String dictValue) {
        return dictDataService.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据部门id 获取公司名
     * @param deptId
     * @return
     */
    public String getDeptName(Long deptId) {
        Dept dept = deptService.selectDeptById(deptId);
        return dept == null ? "" : dept.getDeptName();
    }
}
