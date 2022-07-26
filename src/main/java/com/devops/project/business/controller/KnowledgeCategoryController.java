package com.devops.project.business.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.devops.common.utils.DateUtils;
import com.devops.common.utils.poi.ExcelUtil;
import com.devops.common.utils.security.ShiroUtils;
import com.devops.framework.aspectj.lang.annotation.Log;
import com.devops.framework.aspectj.lang.enums.BusinessType;
import com.devops.framework.web.controller.BaseController;
import com.devops.framework.web.domain.AjaxResult;
import com.devops.framework.web.page.TableDataInfo;
import com.devops.project.business.domain.KnowledgeCategory;
import com.devops.project.business.service.IKnowledgeCategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识分类Controller
 *
 * @author chengxiao
 * @date 2022-06-06
 */
@Controller
@RequestMapping("/business/category")
public class KnowledgeCategoryController extends BaseController {

    private String prefix = "business/category";

    @Autowired
    private IKnowledgeCategoryService knowledgeCategoryService;

    @RequiresPermissions("business:category:view")
    @GetMapping()
    public String category() {
        return prefix + "/categoryLv1";
    }

    /**
     * 查询知识分类列表
     */
    @RequiresPermissions("business:category:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(KnowledgeCategory knowledgeCategory) {
        startPage();
        knowledgeCategory.setPid(0L);
        List<KnowledgeCategory> list = knowledgeCategoryService.selectKnowledgeCategoryList(knowledgeCategory);
        return getDataTable(list);
    }

    /**
     * 导出知识分类列表
     */
    @RequiresPermissions("business:category:export")
    @Log(title = "知识分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(KnowledgeCategory knowledgeCategory) {
        knowledgeCategory.setPid(0L);
        List<KnowledgeCategory> list = knowledgeCategoryService.selectKnowledgeCategoryList(knowledgeCategory);
        ExcelUtil<KnowledgeCategory> util = new ExcelUtil<KnowledgeCategory>(KnowledgeCategory.class);
        return util.exportExcel(list, "category");
    }

    /**
     * 新增知识分类
     */

    @GetMapping("/add")
    @RequiresPermissions("business:category:add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存知识分类
     */
    @RequiresPermissions("business:category:add")
    @Log(title = "知识分类", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(KnowledgeCategory knowledgeCategory) {
        knowledgeCategory.setCreateBy(ShiroUtils.getLoginName());
        knowledgeCategory.setCreateTime(DateUtils.getNowDate());
        knowledgeCategory.setDeptId(Long.valueOf(ShiroUtils.getDeptId()));
        return toAjax(knowledgeCategoryService.save(knowledgeCategory));
    }

    /**
     * 修改知识分类
     */
    @GetMapping("/edit/{categoryId}")
    @RequiresPermissions("business:category:edit")
    public String edit(@PathVariable("categoryId") Long categoryId, ModelMap mmap) {
        KnowledgeCategory knowledgeCategory = knowledgeCategoryService.getById(categoryId);
        mmap.put("knowledgeCategory", knowledgeCategory);
        return prefix + "/edit";
    }

    /**
     * 修改保存知识分类
     */
    @RequiresPermissions("business:category:edit")
    @Log(title = "知识分类", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(KnowledgeCategory knowledgeCategory) {
        knowledgeCategory.setUpdateTime(DateUtils.getNowDate());
        return toAjax(knowledgeCategoryService.updateById(knowledgeCategory));
    }

    /**
     * 删除知识分类
     */
    @RequiresPermissions("business:category:remove")
    @Log(title = "知识分类", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(knowledgeCategoryService.deleteKnowledgeCategoryByIds(ids));
    }

    /**
     * 查询字典详细
     */
    @GetMapping("/detail/{categoryId}")
    public String detail(@PathVariable("categoryId") Long categoryId, ModelMap mmap) {
        mmap.put("category", knowledgeCategoryService.getById(categoryId));
        mmap.put("categoryList", knowledgeCategoryService.list(new QueryWrapper<KnowledgeCategory>().eq("pid", 0)));
        return prefix + "/lv2/categoryLv2";
    }

    @PostMapping("/lv2/list")
    @ResponseBody
    public TableDataInfo lv2List(KnowledgeCategory knowledgeCategory) {
        startPage();
        List<KnowledgeCategory> list = knowledgeCategoryService.selectKnowledgeCategoryList(knowledgeCategory);
        return getDataTable(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PostMapping("/lv2/export")
    @ResponseBody
    public AjaxResult lv2Export(KnowledgeCategory knowledgeCategory) {
        List<KnowledgeCategory> list = knowledgeCategoryService.selectKnowledgeCategoryList(knowledgeCategory);
        ExcelUtil<KnowledgeCategory> util = new ExcelUtil<KnowledgeCategory>(KnowledgeCategory.class);
        return util.exportExcel(list, "KnowledgeCategory");//字典数据
    }

    /**
     * 新增二级类别
     */
    @GetMapping("/lv2/add/{pid}")
    public String lv2Add(@PathVariable("pid") String pid, ModelMap mmap) {
        mmap.put("pid", pid);
        mmap.put("pCategoryName", knowledgeCategoryService.getById(pid).getCategoryName());

        return prefix + "/lv2/add";
    }

    /**
     * 新增保存二级类别
     */
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping("/lv2/add")
    @ResponseBody
    public AjaxResult lv2AddSave(@Validated KnowledgeCategory knowledgeCategory) {
        knowledgeCategory.setCreateBy(ShiroUtils.getLoginName());
        knowledgeCategory.setDeptId(Long.valueOf(ShiroUtils.getDeptId()));
        knowledgeCategory.setCreateTime(DateUtils.getNowDate());
        return toAjax(knowledgeCategoryService.save(knowledgeCategory));
    }

    /**
     * 修改二级类别
     */
    @GetMapping("/lv2/edit/{categoryId}")
    public String lv2Edit(@PathVariable("categoryId") Long categoryId, ModelMap mmap) {
        KnowledgeCategory knowledgeCategory = knowledgeCategoryService.getById(categoryId);
        mmap.put("knowledgeCategory", knowledgeCategory);
        mmap.put("pCategoryName", knowledgeCategoryService.getById(knowledgeCategory.getPid()).getCategoryName());

        return prefix + "/lv2/edit";
    }

    /**
     * 修改保存二级类别
     */
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @RequiresPermissions("business:category:edit")
    @PostMapping("/lv2/edit")
    @ResponseBody
    public AjaxResult lv2EditSave(@Validated KnowledgeCategory knowledgeCategory) {
        knowledgeCategory.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(knowledgeCategoryService.updateById(knowledgeCategory));
    }

    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @RequiresPermissions("business:category:remove")
    @PostMapping("/lv2/remove")
    @ResponseBody
    public AjaxResult lv2Remove(String ids) {
        return toAjax(knowledgeCategoryService.deleteKnowledgeCategoryByIds(ids));
    }

    @GetMapping("/getCategoryData")
    @ResponseBody
    public String getCategoryData() {

        //1.获取二级科目
        List<KnowledgeCategory> categoryLv2List = knowledgeCategoryService.list(new QueryWrapper<KnowledgeCategory>().ne("pid", 0));
        //2.获取一级科目
        List<KnowledgeCategory> categoryLv1List = knowledgeCategoryService.list(new QueryWrapper<KnowledgeCategory>().eq("pid", 0));

        List<Map<String, Object>> categoryMapList = new ArrayList<>();
        for (KnowledgeCategory category : categoryLv1List) {
            Long categoryId = category.getCategoryId();
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("n", category.getCategoryName());
            categoryMap.put("v", categoryId);
            List<Map<String, Object>> categoryLv1MapList = getCategoryLv2MapList(categoryId, categoryLv2List);
            categoryMap.put("s", categoryLv1MapList);
            categoryMapList.add(categoryMap);
        }
        return JSONUtil.toJsonStr(categoryMapList);
    }

    private List<Map<String, Object>> getCategoryLv2MapList(Long categoryId, List<KnowledgeCategory> categoryLv2List) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (KnowledgeCategory category : categoryLv2List) {
            if (categoryId.longValue() == category.getPid().longValue()) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("n", category.getCategoryName());
                categoryMap.put("v", category.getCategoryId());
                list.add(categoryMap);
            }
        }
        return list;
    }
}
