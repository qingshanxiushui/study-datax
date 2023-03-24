package com.alibaba.datax.plugin.writer.httpclinicwriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil {
    public static Map<String,String> getProperties() throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream("./src/resources/config.properties");
        prop.load(input);
        Map<String,String> propertyMap = new HashMap<>();
        propertyMap.put("ipAddress",prop.getProperty("ipAddress"));
        propertyMap.put("port",prop.getProperty("port"));
        propertyMap.put("databaseName",prop.getProperty("databaseName"));
        propertyMap.put("userName",prop.getProperty("userName"));
        propertyMap.put("passWord",prop.getProperty("passWord"));
        System.out.println(propertyMap);
        return propertyMap;
    }
}
