package com.datax.test.four;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class dataxParamsTest {

    public static void main(String[] args) throws IOException, IOException {

        //获取datax.py文件的规范化绝对路径
        File dataXFile = new File("D:\\DataX\\datax\\bin\\datax.py");
        String dataXPath = dataXFile.getCanonicalPath();

        File jsonFile = new File("D:\\DataX\\datax\\job\\mysql2mysql_param.json");
        String jsonPath = jsonFile.getCanonicalPath();
        System.out.println("------------------start----------------------");
        //name为文件json文件名
        String create_time = "2022-12-28";
        String end_time = "2022-12-29";
        String command = "python " + dataXPath + " " + jsonPath + " -p \"-Dcreate_time='" + create_time +"' -Dend_time='" + end_time + "'\" ";
        //String command = "python " + dataXPath + " " + jsonPath + " -p \"-Dcreate_time='2022-12-28' -Dend_time='2022-12-29'\"";
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
}
