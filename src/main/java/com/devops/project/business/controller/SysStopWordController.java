package com.devops.project.business.controller;

import com.devops.common.utils.DateUtils;
import com.devops.common.utils.poi.ExcelUtil;
import com.devops.framework.aspectj.lang.annotation.Log;
import com.devops.framework.aspectj.lang.enums.BusinessType;
import com.devops.framework.web.controller.BaseController;
import com.devops.framework.web.domain.AjaxResult;
import com.devops.framework.web.page.TableDataInfo;
import com.devops.project.business.domain.SysStopWord;
import com.devops.project.business.service.IQingtingQiaoService;
import com.devops.project.business.service.ISysStopWordService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 违禁词Controller
 *
 * @author chengxiao
 * @date 2022-06-15
 */
@Controller
@RequestMapping("/business/word")
public class SysStopWordController extends BaseController {

    private String prefix = "business/word";

    @Autowired
    private ISysStopWordService sysStopWordService;
    @Autowired
    private IQingtingQiaoService qingtingQiaoService;

    @RequiresPermissions("business:word:view")
    @GetMapping()
    public String word() {
        return prefix + "/word";
    }

    /**
     * 查询违禁词列表
     */
    @RequiresPermissions("business:word:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysStopWord sysStopWord) {
        startPage();
        List<SysStopWord> list = sysStopWordService.selectSysStopWordList(sysStopWord);
        return getDataTable(list);
    }

    /**
     * 导出违禁词列表
     */
    @RequiresPermissions("business:word:export")
    @Log(title = "违禁词", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysStopWord sysStopWord) {
        List<SysStopWord> list = sysStopWordService.selectSysStopWordList(sysStopWord);
        ExcelUtil<SysStopWord> util = new ExcelUtil<SysStopWord>(SysStopWord.class);
        return util.exportExcel(list, "word");
    }

    /**
     * 新增违禁词
     */

    @GetMapping("/add")
    @RequiresPermissions("business:word:add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存违禁词
     */
    @RequiresPermissions("business:word:add")
    @Log(title = "违禁词", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysStopWord sysStopWord) {
        sysStopWord.setCreateTime(DateUtils.getNowDate());
        return toAjax(sysStopWordService.save(sysStopWord));
    }

    /**
     * 修改违禁词
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("business:word:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        SysStopWord sysStopWord = sysStopWordService.getById(id);
        mmap.put("sysStopWord", sysStopWord);
        return prefix + "/edit";
    }

    /**
     * 修改保存违禁词
     */
    @RequiresPermissions("business:word:edit")
    @Log(title = "违禁词", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysStopWord sysStopWord) {
        sysStopWord.setUpdateTime(DateUtils.getNowDate());
        return toAjax(sysStopWordService.updateById(sysStopWord));
    }

    /**
     * 删除违禁词
     */
    @RequiresPermissions("business:word:remove")
    @Log(title = "违禁词", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(sysStopWordService.deleteSysStopWordByIds(ids));
    }

    @RequiresPermissions("business:word:add")
    @GetMapping("/syncStopword")
    @ResponseBody
    public AjaxResult syncStopword() {
        qingtingQiaoService.syncStopword();
        return AjaxResult.success();
    }
}
