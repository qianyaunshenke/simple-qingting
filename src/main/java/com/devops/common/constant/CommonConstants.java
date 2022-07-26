package com.devops.common.constant;

import com.devops.framework.config.GlobalConfig;

import java.io.File;

public class CommonConstants {

    public static String DOWNLOADFILES_PATH = GlobalConfig.getResource() + File.separator +  "DownloadFiles" +File.separator;

    public static String DOWNLOADFILES_VPATH = "/DownloadFiles/";
}
