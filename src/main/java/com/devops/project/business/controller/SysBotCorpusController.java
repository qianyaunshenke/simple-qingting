package com.devops.project.business.controller;

import com.devops.common.exception.BusinessException;
import com.devops.common.utils.DateUtils;
import com.devops.common.utils.StringUtils;
import com.devops.common.utils.poi.ExcelUtil;
import com.devops.framework.aspectj.lang.annotation.Log;
import com.devops.framework.aspectj.lang.enums.BusinessType;
import com.devops.framework.web.controller.BaseController;
import com.devops.framework.web.domain.AjaxResult;
import com.devops.framework.web.page.TableDataInfo;
import com.devops.project.business.domain.SysBotCorpus;
import com.devops.project.business.service.IQingtingQiaoService;
import com.devops.project.business.service.ISysBotCorpusService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机器人语料Controller
 *
 * @author chengxiao
 * @date 2022-06-20
 */
@Controller
@RequestMapping("/business/corpus")
public class SysBotCorpusController extends BaseController {

    private String prefix = "business/corpus";

    @Autowired
    private ISysBotCorpusService sysBotCorpusService;
    @Autowired
    private IQingtingQiaoService qingtingQiaoService;

    @RequiresPermissions("business:corpus:view")
    @GetMapping()
    public String corpus() {
        return prefix + "/corpus";
    }

    /**
     * 查询机器人语料列表
     */
    @RequiresPermissions("business:corpus:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysBotCorpus sysBotCorpus) {
        startPage();
        List<SysBotCorpus> list = sysBotCorpusService.selectSysBotCorpusList(sysBotCorpus);
        return getDataTable(list);
    }

    /**
     * 导出机器人语料列表
     */
    @RequiresPermissions("business:corpus:export")
    @Log(title = "机器人语料", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysBotCorpus sysBotCorpus) {
        List<SysBotCorpus> list = sysBotCorpusService.selectSysBotCorpusList(sysBotCorpus);
        ExcelUtil<SysBotCorpus> util = new ExcelUtil<SysBotCorpus>(SysBotCorpus.class);
        return util.exportExcel(list, "corpus");
    }

    /**
     * 新增机器人语料
     */

    @GetMapping("/add")
    @RequiresPermissions("business:corpus:add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存机器人语料
     */
    @RequiresPermissions("business:corpus:add")
    @Log(title = "机器人语料", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysBotCorpus sysBotCorpus) {
        sysBotCorpus.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(sysBotCorpus.getAnswer())) {
            throw new BusinessException("答案不能为空");
        }
        return toAjax(sysBotCorpusService.save(sysBotCorpus));
    }

    /**
     * 修改机器人语料
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("business:corpus:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        SysBotCorpus sysBotCorpus = sysBotCorpusService.getById(id);
        mmap.put("sysBotCorpus", sysBotCorpus);
        return prefix + "/edit";
    }

    /**
     * 修改保存机器人语料
     */
    @RequiresPermissions("business:corpus:edit")
    @Log(title = "机器人语料", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysBotCorpus sysBotCorpus) {
        sysBotCorpus.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(sysBotCorpus.getAnswer())) {
            throw new BusinessException("答案不能为空");
        }
        return toAjax(sysBotCorpusService.updateById(sysBotCorpus));
    }

    /**
     * 删除机器人语料
     */
    @RequiresPermissions("business:corpus:remove")
    @Log(title = "机器人语料", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(sysBotCorpusService.deleteSysBotCorpusByIds(ids));
    }

    @RequiresPermissions("business:corpus:add")
    @GetMapping("/syncBotCorpus")
    @ResponseBody
    public AjaxResult syncBotCorpus() {
        qingtingQiaoService.syncBotCorpus();
        return AjaxResult.success();
    }
}
