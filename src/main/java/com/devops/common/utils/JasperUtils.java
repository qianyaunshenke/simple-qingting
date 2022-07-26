package com.devops.common.utils;

import com.devops.framework.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
 * Jasper生命周期
 * 1、将.jrxml编译成.jasper文件(也可以用ireport直接生成》jasper文件)
 * 2、构建jasper数据源，变量与.jrxml中的field对应
 * 3、使用数据源填充报告模板
 * 4、导出报表
 */
@Slf4j
public class JasperUtils {

    /**
     * 导出pdf报表(针对javaBean)
     *
     * @param jasperReport 导出报表对象
     * @param parameter    自定义parameter
     */
    public static void export2PdfUseJavaBean(JasperReport jasperReport, List<?> dataSourceList, Map<String, Object> parameter, String exportFilePath) {
        //导出路径检查
        checkExportFile(exportFilePath);
        //设置pdf字体
        setJasperFontPath(jasperReport, "pdf");
        log.info("jasperReport dataSourceList size =====================" + dataSourceList.size());
        //获取数据源
        JRBeanCollectionDataSource jrDataSource = (JRBeanCollectionDataSource) getDataResourceUseJavaBean(dataSourceList);

        log.info("jasperReport jrDataSource size =====================" + jrDataSource.getRecordCount());
        //填充报表
        JasperPrint jasperPrint = fillReport(jasperReport, jrDataSource, parameter);
        //导出报表
        exportPdfReport(jasperPrint, exportFilePath);
    }

    /**
     * 导出pdf报表(针对javaBean)
     *
     * @param jasperReport 导出报表对象
     * @param parameter    自定义parameter
     */
    public static void export2WordUseJavaBean(JasperReport jasperReport, List<?> dataSourceList, Map<String, Object> parameter, String exportFilePath) {
        //导出路径检查
        checkExportFile(exportFilePath);
        //设置pdf字体
        setJasperFontPath(jasperReport, "doc");
        //获取数据源
        JRDataSource jrDataSource = getDataResourceUseJavaBean(dataSourceList);
        //填充报表
        JasperPrint jasperPrint = fillReport(jasperReport, jrDataSource, parameter);
        //导出报表
        exportWordReport(jasperPrint, exportFilePath);
    }

    /**
     * 导出pdf报表(针对javaBean)
     *
     * @param jasperReport 导出报表对象
     * @param parameter    自定义parameter
     */
    public static void export2XlsUseJavaBean(JasperReport jasperReport, List<?> dataSourceList, Map<String, Object> parameter, String exportFilePath) {
        //导出路径检查
        checkExportFile(exportFilePath);
        //设置pdf字体
        setJasperFontPath(jasperReport, "xls");
        //获取数据源
        JRDataSource jrDataSource = getDataResourceUseJavaBean(dataSourceList);
        //填充报表
        JasperPrint jasperPrint = fillReport(jasperReport, jrDataSource, parameter);
        //导出报表
        exportXlsReport(jasperPrint, exportFilePath);
    }

    /**
     * 导出pdf报表(针对javaBean)
     *
     * @param jasperFilePath jasper文件路径
     * @param parameter      自定义parameter
     */
    public static void export2PdfUseJavaBean(String jasperFilePath, List<?> dataSourceList, Map<String, Object> parameter, String exportFilePath) {
        //导出路径检查
        checkExportFile(exportFilePath);
        //装载报表
        JasperReport jasperReport = loadReport(jasperFilePath);
        //设置pdf字体
        setJasperFontPath(jasperReport, "pdf");
        //获取数据源
        JRDataSource jrDataSource = getDataResourceUseJavaBean(dataSourceList);
        //填充报表
        JasperPrint jasperPrint = fillReport(jasperReport, jrDataSource, parameter);
        //导出报表
        exportPdfReport(jasperPrint, exportFilePath);
    }

    /**
     * 导出pdf报表(针对Map)
     *
     * @param jasperFilePath jasper文件路径
     * @param parameter      自定义parameter
     */
    public static void export2PdfUseMap(String jasperFilePath, List<Map<String, Object>> dataSourceList, Map<String, Object> parameter, String exportFilePath) {
        //导出路径检查
        checkExportFile(exportFilePath);
        //装载报表
        JasperReport jasperReport = loadReport(jasperFilePath);
        //设置pdf字体
        setJasperFontPath(jasperReport, "pdf");
        //获取数据源
        JRDataSource jrDataSource = getDataResourceUseMap(dataSourceList);
        //填充报表
        JasperPrint jasperPrint = fillReport(jasperReport, jrDataSource, parameter);
        //导出报表
        exportPdfReport(jasperPrint, exportFilePath);
    }

    /**
     * 加载jasperReport对象
     *
     * @param jasperFilePath jasper文件路径
     */
    public static JasperReport loadReport(String jasperFilePath) {
        log.info("start to load jasper report");
        log.info("check jasper file");

        //文件类型校验
        File file = new File(jasperFilePath);
        if (!file.exists()) {
            throw new RuntimeException("jasper file not exist");
        }

        //加载JasperReport对象
        JasperReport jasperReport = null;
        try {
            if (jasperFilePath.endsWith(".jrxml")) {
                jasperReport = JasperCompileManager.compileReport(jasperFilePath);
            } else if (jasperFilePath.endsWith(".jasper")) {
                jasperReport = (JasperReport) JRLoader.loadObject(file);
            }
        } catch (JRException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return jasperReport;
    }

    //从工程外部读取文件
    public static File getFileOutProject(String fileDir, String fileName) {
        String[] fileDirs = fileDir.split("/");
        StringBuilder outPathBuilder = new StringBuilder()
                .append(GlobalConfig.getResource()).append(File.separator);
        for (String dir : fileDirs) {
            outPathBuilder.append(dir).append(File.separator);
        }
        if (!StringUtils.isEmpty(fileName)) {
            outPathBuilder.append(fileName);
        }
        //目录不存在时,自动创建
        log.info("getFileOutProject:"+outPathBuilder.toString());
        File file = new File(outPathBuilder.toString());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.mkdirs();
            log.info("file create success");
        }

        return file;
    }

    //获取jasper文件路径(包括子报表)
    public static String getJasperFilePath(String fileName) {
        //优先读取外部文件
        File file = getFileOutProject("jasper", fileName);
        log.info("####getFileOutProject####:"+file);
        if (!file.exists()) {
            //从工程内部读取文件,并拷贝到外部
            ClassPathResource classPathResource = new ClassPathResource("jasper/" + fileName);
            InputStream in = null;
            OutputStream out = null;
            try {
                in = classPathResource.getInputStream();
                file = new File(
                        new StringBuilder()
                                .append(GlobalConfig.getResource()).append(File.separator)
                                .append("jasper").append(File.separator)
                                .append(fileName).toString()
                );
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                out = new FileOutputStream(file);
                IOUtils.copy(in, out);
            } catch (IOException e) {
                throw new RuntimeException("jasper file not exist");
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
        return file.getAbsolutePath();
    }

    //读取字体文件路径
    private static String getJasperFontPath(String fontName) {
        //优先读取外部文件
        File file = getFileOutProject("jasper/font", fontName);
        //文件不存在
        if (!file.exists()) {
            //从工程内部读取文件,并拷贝到外部
            ClassPathResource classPathResource = new ClassPathResource("jasper/font/" + fontName);
            InputStream in = null;
            OutputStream out = null;
            try {
                in = classPathResource.getInputStream();
                file = new File(
                        new StringBuilder()
                                .append(GlobalConfig.getResource()).append(File.separator)
                                .append("jasper").append(File.separator)
                                .append("font").append(File.separator)
                                .append(fontName).toString()
                );
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                out = new FileOutputStream(file);
                IOUtils.copy(in, out);
            } catch (IOException e) {
                throw new RuntimeException("jasper file not exist");
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
        return file.getAbsolutePath();
    }

    //设置字体
    public static void setJasperFontPath(JasperReport jasperReport, String exportType) {
        JRStyle[] jrStyles = jasperReport.getStyles();
        if (jrStyles != null && jrStyles.length > 0) {
            for (JRStyle jrStyle : jrStyles) {
                //获取字体文件
                if ("pdf".equalsIgnoreCase(exportType)) {
                    String fontPath = "";
                    if ("font".equals(jrStyle.getName())) {
                        fontPath = getJasperFontPath("simsun.ttc");
                    } else if ("barcode".equals(jrStyle.getName())) {
                        fontPath = getJasperFontPath("IDAutomationHC39M.ttf");
                    } else if ("black".equals(jrStyle.getName())) {
                        fontPath = getJasperFontPath("simhei.ttf");
                    }
                    //设置字体
                    if (fontPath.endsWith(".ttc")) {
                        jrStyle.setFontName(fontPath + ",1");
                        jrStyle.setPdfFontName(fontPath + ",1");
                    } else {
                        jrStyle.setFontName(fontPath);
                        jrStyle.setPdfFontName(fontPath);
                    }
                } else {
                    jrStyle.setFontName("宋体");
                }
            }
        }
    }

    /**
     * 返回数据资源(针对javaBean)
     *
     * @param dataSourceList javaBean集合
     */
    public static JRDataSource getDataResourceUseJavaBean(List<?> dataSourceList) {
        if (dataSourceList == null || dataSourceList.size() == 0) {
            return new JREmptyDataSource();
        }
        return new JRBeanCollectionDataSource(dataSourceList);
    }

    /**
     * 返回数据资源(针对Map)
     *
     * @param dataSourceList javaBean集合
     */
    public static JRDataSource getDataResourceUseMap(List<Map<String, Object>> dataSourceList) {
        if (dataSourceList == null || dataSourceList.size() == 0) {
            return new JREmptyDataSource();
        }
        List<Map<String, ?>> resourceList = new ArrayList<>(dataSourceList);
        return new JRMapCollectionDataSource(resourceList);
    }

    /**
     * 填充报表
     */
    public static JasperPrint fillReport(JasperReport jasperReport, JRDataSource jrDataSource, Map<String, Object> parameter) {
        //增加仿真器解决大数据导致的内存溢出问题
        //生成一个自带的特殊压缩算法的临时压缩文件包在硬盘中
        JRAbstractLRUVirtualizer virtualizer = new JRGzipVirtualizer(2);
        parameter.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

        //填充报表
        JasperPrint jasperPrint = null;
        try {
            log.info("start to fill report");
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, jrDataSource);
            log.info("fill report success !");
        } catch (JRException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return jasperPrint;
    }

    /**
     * 导出pdf格式报表
     *
     * @param jasperPrint    填充后的报表对象
     * @param exportFilePath 导出路径
     */
    public static void exportPdfReport(JasperPrint jasperPrint, String exportFilePath) {
        log.info("start to export report...");
        log.info("exportFilePath :" + exportFilePath);

        //设置导出参数
        JRAbstractExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportFilePath));

        //导出报表
        exportReport(exporter);
    }

    /**
     * 导出word格式报表
     *
     * @param jasperPrint    填充后的报表对象
     * @param exportFilePath 导出路径
     */
    public static void exportWordReport(JasperPrint jasperPrint, String exportFilePath) {
        log.info("start to export report...");
        log.info("exportFilePath :" + exportFilePath);

        //设置导出参数
        JRAbstractExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportFilePath));

        //导出报表
        exportReport(exporter);
    }

    /**
     * 导出xls格式报表
     *
     * @param jasperPrint    填充后的报表对象
     * @param exportFilePath 导出路径
     */
    public static void exportXlsReport(JasperPrint jasperPrint, String exportFilePath) {
        log.info("start to export report...");
        log.info("exportFilePath :" + exportFilePath);

        //设置导出参数
        JRAbstractExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(exportFilePath));

        //导出报表
        exportReport(exporter);
    }

    /**
     * 导出报表
     *
     * @param exporter
     */
    private static void exportReport(JRAbstractExporter exporter) {
        //导出报表
        log.info("exportReport!");

        try {
            exporter.exportReport();
            log.info("export success!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("export failed!" + e.getMessage());
        }
    }

    //校验文件地址
    private static void checkExportFile(String exportFilePath) {
        if (StringUtils.isEmpty(exportFilePath)) {
            throw new RuntimeException("filePath can not be empty!");
        }
        //自动创建父级目录
        File file = new File(exportFilePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && parentFile.mkdirs()) {
            log.info("file create success!" + file.getAbsolutePath());
        }
    }
}
