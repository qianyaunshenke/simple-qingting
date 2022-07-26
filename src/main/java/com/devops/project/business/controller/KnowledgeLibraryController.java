package com.devops.project.business.controller;

import com.devops.common.exception.BusinessException;
import com.devops.common.utils.DateUtils;
import com.devops.common.utils.poi.ExcelUtil;
import com.devops.common.utils.security.ShiroUtils;
import com.devops.framework.aspectj.lang.annotation.Log;
import com.devops.framework.aspectj.lang.enums.BusinessType;
import com.devops.framework.web.controller.BaseController;
import com.devops.framework.web.domain.AjaxResult;
import com.devops.framework.web.page.TableDataInfo;
import com.devops.project.business.domain.KnowledgeCategory;
import com.devops.project.business.domain.KnowledgeLibrary;
import com.devops.project.business.service.IKnowledgeCategoryService;
import com.devops.project.business.service.IKnowledgeLibraryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库Controller
 *
 * @author chengxiao
 * @date 2022-06-06
 */
@Controller
@RequestMapping("/business/library")
public class KnowledgeLibraryController extends BaseController {

    private String prefix = "business/library";

    @Autowired
    private IKnowledgeLibraryService knowledgeLibraryService;
    @Autowired
    private IKnowledgeCategoryService knowledgeCategoryService;

    @RequiresPermissions("business:library:view")
    @GetMapping()
    public String library() {
        return prefix + "/library";
    }

    /**
     * 查询知识库列表
     */
    @RequiresPermissions("business:library:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(KnowledgeLibrary knowledgeLibrary) {
        startPage();
        List<KnowledgeLibrary> list = knowledgeLibraryService.selectKnowledgeLibraryList(knowledgeLibrary);
        return getDataTable(list);
    }

    /**
     * 导出知识库列表
     */
    @RequiresPermissions("business:library:export")
    @Log(title = "知识库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(KnowledgeLibrary knowledgeLibrary) {
        List<KnowledgeLibrary> list = knowledgeLibraryService.selectKnowledgeLibraryList(knowledgeLibrary);
        ExcelUtil<KnowledgeLibrary> util = new ExcelUtil<KnowledgeLibrary>(KnowledgeLibrary.class);
        return util.exportExcel(list, "library");
    }

    /**
     * 新增知识库
     */

    @GetMapping("/add")
    @RequiresPermissions("business:library:add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存知识库
     */
    @RequiresPermissions("business:library:add")
    @Log(title = "知识库", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(KnowledgeLibrary knowledgeLibrary) {
        if (knowledgeLibrary.getCategoryId() == null) throw new BusinessException("类别不能为空");
        knowledgeLibrary.setCreateTime(DateUtils.getNowDate());
        knowledgeLibrary.setDeptId(Long.valueOf(ShiroUtils.getDeptId()));
        return toAjax(knowledgeLibraryService.save(knowledgeLibrary));
    }

    /**
     * 修改知识库
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("business:library:edit")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        KnowledgeLibrary knowledgeLibrary = knowledgeLibraryService.getById(id);
        KnowledgeCategory knowledgeCategory = knowledgeCategoryService.getById(knowledgeLibrary.getCategoryId());
        mmap.put("pid", knowledgeCategory.getPid());
        mmap.put("knowledgeLibrary", knowledgeLibrary);
        return prefix + "/edit";
    }

    /**
     * 修改保存知识库
     */
    @RequiresPermissions("business:library:edit")
    @Log(title = "知识库", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(KnowledgeLibrary knowledgeLibrary) {
        if (knowledgeLibrary.getCategoryId() == null) throw new BusinessException("类别不能为空");
        knowledgeLibrary.setUpdateTime(DateUtils.getNowDate());
        return toAjax(knowledgeLibraryService.updateById(knowledgeLibrary));
    }

    /**
     * 删除知识库
     */
    @RequiresPermissions("business:library:remove")
    @Log(title = "知识库", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(knowledgeLibraryService.deleteKnowledgeLibraryByIds(ids));
    }
}
