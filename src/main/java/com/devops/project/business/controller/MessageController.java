package com.devops.project.business.controller;

import com.devops.common.utils.poi.ExcelUtil;
import com.devops.framework.aspectj.lang.annotation.Log;
import com.devops.framework.aspectj.lang.enums.BusinessType;
import com.devops.framework.web.controller.BaseController;
import com.devops.framework.web.domain.AjaxResult;
import com.devops.framework.web.page.TableDataInfo;
import com.devops.project.business.domain.Message;
import com.devops.project.business.service.IMessageService;
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
@RequestMapping("/business/message")
public class MessageController extends BaseController {

    private String prefix = "business/message";

    @Autowired
    private IMessageService messageService;

    @RequiresPermissions("business:message:view")
    @GetMapping()
    public String message() {
        return prefix + "/message";
    }

    /**
     * 查询访客会话列表
     */
    @RequiresPermissions("business:message:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Message message) {
        startPage();
        List<Message> list = messageService.selectMessageList(message);
        return getDataTable(list);
    }

    /**
     * 导出访客会话列表
     */
    @RequiresPermissions("business:message:export")
    @Log(title = "访客会话", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Message message) {
        List<Message> list = messageService.selectMessageList(message);
        ExcelUtil<Message> util = new ExcelUtil<Message>(Message.class);
        return util.exportExcel(list, "message");
    }

    /**
     * 新增访客会话
     */

    @GetMapping("/add")
    @RequiresPermissions("business:message:add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存访客会话
     */
    @RequiresPermissions("business:message:add")
    @Log(title = "访客会话", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Message message) {
        return toAjax(messageService.save(message));
    }

    /**
     * 修改访客会话
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("business:message:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        Message message = messageService.getById(id);
        mmap.put("message", message);
        return prefix + "/edit";
    }

    /**
     * 修改保存访客会话
     */
    @RequiresPermissions("business:message:edit")
    @Log(title = "访客会话", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Message message) {
        return toAjax(messageService.updateById(message));
    }

    /**
     * 删除访客会话
     */
    @RequiresPermissions("business:message:remove")
    @Log(title = "访客会话", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(messageService.deleteMessageByIds(ids));
    }
}
