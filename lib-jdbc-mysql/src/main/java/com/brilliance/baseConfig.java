package com.brilliance;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;


public class baseConfig {
    protected static Logger logger = LoggerFactory.getLogger(baseConfig.class);
    protected static Properties props = null;
    static {
	try {
	    props = PropertiesLoaderUtils.loadAllProperties("application.properties");	   
	} catch (IOException e) {
	    logger.error(e.getMessage());
	}
    }

    public static int NumberPerPage = new Integer(props.getProperty("NumberPerPage"));// 默认每页记录数量
    public static String DATETIME_FORMAT = props.getProperty("DATETIME_FORMAT");// 日期格式
 

}
