package com.devops.project.system.controller;

import com.devops.common.constant.UserConstants;
import com.devops.common.utils.poi.ExcelUtil;
import com.devops.project.system.domain.DictData;
import com.devops.project.system.domain.DictType;
import com.devops.project.system.service.IDictDataService;
import com.devops.project.system.service.IDictTypeService;
import com.devops.framework.aspectj.lang.annotation.Log;
import com.devops.framework.aspectj.lang.enums.BusinessType;
import com.devops.framework.web.domain.AjaxResult;
import com.devops.framework.web.domain.Ztree;
import com.devops.framework.web.page.TableDataInfo;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.devops.framework.web.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * 图片验证码（支持算术形式）
 * 
 * @author devops
 */
@Controller
@RequestMapping("/captcha")
public class CaptchaController extends BaseController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 验证码生成
     */
    @GetMapping(value = "/captchaImage")
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response)
    {
        ServletOutputStream out = null;
        try
        {
            HttpSession session = request.getSession();
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");

            String type = request.getParameter("type");
            String capStr = null;
            String code = null;
            BufferedImage bi = null;
            if ("math".equals(type))
            {
                String capText = captchaProducerMath.createText();
                capStr = capText.substring(0, capText.lastIndexOf("@"));
                code = capText.substring(capText.lastIndexOf("@") + 1);
                bi = captchaProducerMath.createImage(capStr);
            }
            else if ("char".equals(type))
            {
                capStr = code = captchaProducer.createText();
                bi = captchaProducer.createImage(capStr);
            }
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY, code);
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 数据字典信息
     *
     * @author devops
     */
    @Controller
    @RequestMapping("/system/dict/data")
    public static class DictDataController extends BaseController
    {
        private String prefix = "system/dict/data";

        @Autowired
        private IDictDataService dictDataService;

        @RequiresPermissions("system:dict:view")
        @GetMapping()
        public String dictData()
        {
            return prefix + "/data";
        }

        @PostMapping("/list")
        @RequiresPermissions("system:dict:list")
        @ResponseBody
        public TableDataInfo list(DictData dictData)
        {
            startPage();
            List<DictData> list = dictDataService.selectDictDataList(dictData);
            return getDataTable(list);
        }

        @Log(title = "字典数据", businessType = BusinessType.EXPORT)
        @RequiresPermissions("system:dict:export")
        @PostMapping("/export")
        @ResponseBody
        public AjaxResult export(DictData dictData)
        {
            List<DictData> list = dictDataService.selectDictDataList(dictData);
            ExcelUtil<DictData> util = new ExcelUtil<DictData>(DictData.class);
            return util.exportExcel(list, "字典数据");
        }

        /**
         * 新增字典类型
         */
        @GetMapping("/add/{dictType}")
        public String add(@PathVariable("dictType") String dictType, ModelMap mmap)
        {
            mmap.put("dictType", dictType);
            return prefix + "/add";
        }

        /**
         * 新增保存字典类型
         */
        @Log(title = "字典数据", businessType = BusinessType.INSERT)
        @RequiresPermissions("system:dict:add")
        @PostMapping("/add")
        @ResponseBody
        public AjaxResult addSave(@Validated DictData dict)
        {
            return toAjax(dictDataService.insertDictData(dict));
        }

        /**
         * 修改字典类型
         */
        @GetMapping("/edit/{dictCode}")
        public String edit(@PathVariable("dictCode") Long dictCode, ModelMap mmap)
        {
            mmap.put("dict", dictDataService.selectDictDataById(dictCode));
            return prefix + "/edit";
        }

        /**
         * 修改保存字典类型
         */
        @Log(title = "字典数据", businessType = BusinessType.UPDATE)
        @RequiresPermissions("system:dict:edit")
        @PostMapping("/edit")
        @ResponseBody
        public AjaxResult editSave(@Validated DictData dict)
        {
            return toAjax(dictDataService.updateDictData(dict));
        }

        @Log(title = "字典数据", businessType = BusinessType.DELETE)
        @RequiresPermissions("system:dict:remove")
        @PostMapping("/remove")
        @ResponseBody
        public AjaxResult remove(String ids)
        {
            dictDataService.deleteDictDataByIds(ids);
            return success();
        }
    }

    /**
     * 数据字典信息
     *
     * @author devops
     */
    @Controller
    @RequestMapping("/system/dict")
    public static class DictTypeController extends BaseController
    {
        private String prefix = "system/dict/type";

        @Autowired
        private IDictTypeService dictTypeService;

        @RequiresPermissions("system:dict:view")
        @GetMapping()
        public String dictType()
        {
            return prefix + "/type";
        }

        @PostMapping("/list")
        @RequiresPermissions("system:dict:list")
        @ResponseBody
        public TableDataInfo list(DictType dictType)
        {
            startPage();
            List<DictType> list = dictTypeService.selectDictTypeList(dictType);
            return getDataTable(list);
        }

        @Log(title = "字典类型", businessType = BusinessType.EXPORT)
        @RequiresPermissions("system:dict:export")
        @PostMapping("/export")
        @ResponseBody
        public AjaxResult export(DictType dictType)
        {

            List<DictType> list = dictTypeService.selectDictTypeList(dictType);
            ExcelUtil<DictType> util = new ExcelUtil<DictType>(DictType.class);
            return util.exportExcel(list, "字典类型");
        }

        /**
         * 新增字典类型
         */
        @GetMapping("/add")
        public String add()
        {
            return prefix + "/add";
        }

        /**
         * 新增保存字典类型
         */
        @Log(title = "字典类型", businessType = BusinessType.INSERT)
        @RequiresPermissions("system:dict:add")
        @PostMapping("/add")
        @ResponseBody
        public AjaxResult addSave(@Validated DictType dict)
        {
            if (UserConstants.DICT_TYPE_NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict)))
            {
                return error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
            }
            return toAjax(dictTypeService.insertDictType(dict));
        }

        /**
         * 修改字典类型
         */
        @GetMapping("/edit/{dictId}")
        public String edit(@PathVariable("dictId") Long dictId, ModelMap mmap)
        {
            mmap.put("dict", dictTypeService.selectDictTypeById(dictId));
            return prefix + "/edit";
        }

        /**
         * 修改保存字典类型
         */
        @Log(title = "字典类型", businessType = BusinessType.UPDATE)
        @RequiresPermissions("system:dict:edit")
        @PostMapping("/edit")
        @ResponseBody
        public AjaxResult editSave(@Validated DictType dict)
        {
            if (UserConstants.DICT_TYPE_NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict)))
            {
                return error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
            }
            return toAjax(dictTypeService.updateDictType(dict));
        }

        @Log(title = "字典类型", businessType = BusinessType.DELETE)
        @RequiresPermissions("system:dict:remove")
        @PostMapping("/remove")
        @ResponseBody
        public AjaxResult remove(String ids)
        {
            dictTypeService.deleteDictTypeByIds(ids);
            return success();
        }

        /**
         * 刷新字典缓存
         */
        @RequiresPermissions("system:dict:remove")
        @Log(title = "字典类型", businessType = BusinessType.CLEAN)
        @GetMapping("/refreshCache")
        @ResponseBody
        public AjaxResult refreshCache()
        {
            dictTypeService.resetDictCache();
            return success();
        }

        /**
         * 查询字典详细
         */
        @RequiresPermissions("system:dict:list")
        @GetMapping("/detail/{dictId}")
        public String detail(@PathVariable("dictId") Long dictId, ModelMap mmap)
        {
            mmap.put("dict", dictTypeService.selectDictTypeById(dictId));
            mmap.put("dictList", dictTypeService.selectDictTypeAll());
            return "system/dict/data/data";
        }

        /**
         * 校验字典类型
         */
        @PostMapping("/checkDictTypeUnique")
        @ResponseBody
        public String checkDictTypeUnique(DictType dictType)
        {
            return dictTypeService.checkDictTypeUnique(dictType);
        }

        /**
         * 选择字典树
         */
        @GetMapping("/selectDictTree/{columnId}/{dictType}")
        public String selectDeptTree(@PathVariable("columnId") Long columnId, @PathVariable("dictType") String dictType,
                ModelMap mmap)
        {
            mmap.put("columnId", columnId);
            mmap.put("dict", dictTypeService.selectDictTypeByType(dictType));
            return prefix + "/tree";
        }

        /**
         * 加载字典列表树
         */
        @GetMapping("/treeData")
        @ResponseBody
        public List<Ztree> treeData()
        {
            List<Ztree> ztrees = dictTypeService.selectDictTree(new DictType());
            return ztrees;
        }
    }
}