package com.devops.project.business.controller;

import com.devops.common.utils.poi.ExcelUtil;
import com.devops.framework.aspectj.lang.annotation.Log;
import com.devops.framework.aspectj.lang.enums.BusinessType;
import com.devops.framework.web.controller.BaseController;
import com.devops.framework.web.domain.AjaxResult;
import com.devops.framework.web.page.TableDataInfo;
import com.devops.project.business.domain.Visitor;
import com.devops.project.business.service.IVisitorService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 访客会话Controller
 *
 * @author chengxiao
 * @date 2022-06-24
 */
@Controller
@RequestMapping("/business/visitor")
public class VisitorController extends BaseController {

    private String prefix = "business/visitor";

    @Autowired
    private IVisitorService visitorService;

    @RequiresPermissions("business:visitor:view")
    @GetMapping()
    public String visitor() {
        return prefix + "/visitor";
    }

    /**
     * 查询访客会话列表
     */
    @RequiresPermissions("business:visitor:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Visitor visitor) {
        startPage();
        List<Visitor> list = visitorService.selectVisitorList(visitor);
        return getDataTable(list);
    }

    /**
     * 导出访客会话列表
     */
    @RequiresPermissions("business:visitor:export")
    @Log(title = "访客会话", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Visitor visitor) {
        List<Visitor> list = visitorService.selectVisitorList(visitor);
        ExcelUtil<Visitor> util = new ExcelUtil<Visitor>(Visitor.class);
        return util.exportExcel(list, "visitor");
    }

    /**
     * 新增访客会话
     */

    @GetMapping("/add")
    @RequiresPermissions("business:visitor:add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存访客会话
     */
    @RequiresPermissions("business:visitor:add")
    @Log(title = "访客会话", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Visitor visitor) {
        return toAjax(visitorService.save(visitor));
    }

    /**
     * 修改访客会话
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("business:visitor:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        Visitor visitor = visitorService.getById(id);
        mmap.put("visitor", visitor);
        return prefix + "/edit";
    }

    /**
     * 修改保存访客会话
     */
    @RequiresPermissions("business:visitor:edit")
    @Log(title = "访客会话", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Visitor visitor) {
        return toAjax(visitorService.updateById(visitor));
    }

    /**
     * 删除访客会话
     */
    @RequiresPermissions("business:visitor:remove")
    @Log(title = "访客会话", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(visitorService.deleteVisitorByIds(ids));
    }

    /**
     * 访客会话详细
     */
    @GetMapping("/msgDetail/{visitorId}")
    public String msgDetail(@PathVariable("visitorId") String visitorId, ModelMap mmap) {
        mmap.put("visitorId", visitorId);
        return "business/message/message";
    }
}
