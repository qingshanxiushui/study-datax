package com.datax.test.one;

import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * 参考 https://blog.csdn.net/qq_41457131/article/details/114376325
 */
public class DataXUtil {

    public static void exeDatax(String jsonPath) throws IOException, IOException {

        //获取datax.py文件的规范化绝对路径
        File dataXFile = new File("D:\\DataX\\datax\\bin\\datax.py");
        String dataXPath = dataXFile.getCanonicalPath();

        File jsonFile = new File(jsonPath);
        jsonPath = jsonFile.getCanonicalPath();
        System.out.println("------------------start----------------------");
        //name为文件json文件名
        String command = "python " + dataXPath + " " + jsonPath;
        System.out.println(command);
        //通过调用cmd执行python执行
        Process pr = Runtime.getRuntime().exec(command);
        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = null;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
        try {
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author: Mr.Black
     * @Description: 创建json文件中reader部分内容
     * @DateTime: 14:36 2021/2/5
     * @Params: [databaseType 数据类型, readerUsername 源数据库的用户名, readerPwd 源数据库的密码, querySql 查询sql, jdbcUrl 数据库连接url]
     * @Return:
     */
    public static String createReader(Integer databaseType, String readerUsername, String readerPwd, String querySql, String jdbcUrl) {
        String readerName = "";
        //数据库类型 （1-Oracle，2-MySql）
        if (databaseType == 1) {
            readerName = "oraclereader";
        } else if (databaseType == 2) {
            readerName = "mysqlreader";
        } else {
            throw new RuntimeException("不支持除Mysql 和 Oracle 外数据库类型!");
        }

        String readerJson = "{\n" +
                "    \"job\": {\n" +
                "        \"setting\": {\n" +
                "            \"speed\": {\n" +
                "                 \"channel\":1\n" +
                "            }\n" +
                "        },\n" +
                "        \"content\": [\n" +
                "            {\n" +
                "               \"reader\": {\n" +
                "                    \"name\": \"" + readerName + "\",\n" +
                "                    \"parameter\": {\n" +
                "                        \"username\": \"" + readerUsername + "\",\n" +
                "                        \"password\": \"" + readerPwd + "\",\n" +
                "                        \"connection\": [\n" +
                "                            {\n" +
                "                                \"querySql\": [\n" +
                "                                    \"" + querySql + "\"\n" +
                "                                ],\n" +
                "                                \"jdbcUrl\": [\n" +
                "                                    \"" + jdbcUrl + "\"\n" +
                "                                ]\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                },\n";

        return readerJson;
    }

    /**
     * @Author: Mr.Black
     * @Description: 创建json文件中writer部分内容
     * @DateTime: 14:41 2021/2/5
     * @Params: [databaseType 数据库类型, writerUsername 目标库用户名, writerPwd 目标库密码, jdbcUrl 目标库连接URL, table 目标表, 指定字段 sourceFields]
     * @Return:
     */
    public static String createWriter(Integer databaseType, String writerUsername, String writerPwd, String jdbcUrl, String table, List<String> targetFields) {

        String writerJson = "";

        String writerName = "";
        //数据库类型 （1-Oracle，2-MySql）
        if (databaseType == 1) {
            writerName = "oraclewriter";
        } else if (databaseType == 2) {
            writerName = "mysqlwriter";
        } else {
            throw new RuntimeException("不支持除Mysql 和 Oracle 外数据库类型!");
        }

        String writerJsonHead = "                \"writer\": {\n" +
                "                    \"name\": \"" + writerName + "\",\n" +
                "                    \"parameter\": {\n" +
                "                        \"username\": \"" + writerUsername + "\",\n" +
                "                        \"password\": \"" + writerPwd + "\",\n" +
                "                        \"column\": [\n";

        for (int i = 0; i < targetFields.size(); i++) {
            if (i == targetFields.size() - 1) {
                writerJsonHead += "                             \"" + targetFields.get(i) + "\"";
            } else {
                writerJsonHead += "                             \"" + targetFields.get(i) + "\",";
            }
        }

        String writerJsonFoot = " \n" +
                "                        ],\n" +
                "                        \"connection\": [\n" +
                "                            {\n" +
                "                                \"jdbcUrl\": \"" + jdbcUrl + "\",\n" +
                "                                \"table\": [\n" +
                "                                    \"" + table + "\"\n" +
                "                                ]\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        return writerJson = writerJsonHead + writerJsonFoot;

    }

    /**
     * @Author: Mr.Black
     * @Description: 创建datax执行的JSON脚本
     * @DateTime: 15:00 2021/2/5
     * @Params: [json]
     * @Return: JSON文件名全路名
     */
    public static String createJobJson(String json) {
        String jsonPath = "D:\\DataX\\datax\\job\\" + UUID.randomUUID().toString().replace("-", "")+".json";

        //将json写入文件中
        byte[] jsonBytes = json.getBytes();
        try {
            FileOutputStream fos = new FileOutputStream(jsonPath);
            fos.write(jsonBytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonPath;
    }
}
