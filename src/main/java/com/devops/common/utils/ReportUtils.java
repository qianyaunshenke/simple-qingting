package com.devops.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.devops.common.constant.CommonConstants;
import com.devops.common.exception.BusinessException;
import com.devops.common.utils.JasperUtils;
import com.devops.framework.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class ReportUtils {

    public static String export(Map<String, Object> parameter) {
        try {
            String   userId = (String) parameter.get("userId");
            String   type = (String) parameter.get("type");
            String   reportName = (String) parameter.get("reportName");
            String   jasperFileName = (String) parameter.get("jasperFileName");
            List<Object>   dataSourceList = (List<Object>) parameter.get("data");
            if (CollectionUtils.isEmpty(dataSourceList)) {
                throw new BusinessException("查询结果为空");
            }


            //加载主报表
            String jasperFilePath = JasperUtils.getJasperFilePath(jasperFileName);
            JasperReport jasperReport = JasperUtils.loadReport(jasperFilePath);

            String exportFilePath =null;
            if("pdf".equals(type)){
                //导出报表
                exportFilePath = getExportFilePath(reportName, "pdf",userId);
                JasperUtils.export2PdfUseJavaBean(jasperReport, dataSourceList, parameter, exportFilePath);
            }else{
                //导出报表
                exportFilePath = getExportFilePath(reportName, "xlsx",userId);
                JasperUtils.export2XlsUseJavaBean(jasperReport, dataSourceList, parameter, exportFilePath);
            }

            return getDownloadUrl(exportFilePath);
        } catch (BusinessException e) {
            throw e;
        }catch (Exception e) {
            log.error("导出报表失败", e);
        }
        return null;
    }


    //获取通用导出保存的实际路径
    private static String getExportFilePath(String reportName,String suffix, String userId) {
        return GlobalConfig.getResource() + File.separator +  "DownloadFiles" +File.separator
                + "report" + File.separator
                + userId + File.separator
                + "temp" + File.separator
                + reportName + suffix;
    }

    //获取报表下载路径
    protected static String getDownloadUrl(String exportFilePath) {
        //将绝对路径替换成相对路径
        exportFilePath = exportFilePath
                .replace(CommonConstants.DOWNLOADFILES_PATH, CommonConstants.DOWNLOADFILES_VPATH)
                .replace("\\", "/");
        //添加服务器信息
        exportFilePath = CommonConstants.DOWNLOADFILES_VPATH + exportFilePath;
        return exportFilePath;
    }

}
