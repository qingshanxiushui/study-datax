package com.datax.test.four;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class dataxParamsOnlineTest {

    public static void main(String[] args) throws IOException, IOException {

        //获取datax.py文件的规范化绝对路径
        File dataXFile = new File("D:\\DataX\\datax\\bin\\datax.py");
        String dataXPath = dataXFile.getCanonicalPath();
        File jsonFile = new File("D:\\DataX\\datax\\job\\clinic4_mysql2http_param_online.json");
        String jsonPath = jsonFile.getCanonicalPath();
        System.out.println("------------------start----------------------");
        //name为文件json文件名
        String start_time = "2022-01-01 00:00:00";
        String end_time = "2032-01-01 00:00:00";
        String card_no = "874635233656";
        String clinic_code = "ZY1232132131";
        String command = "python " + dataXPath + " " + jsonPath
                + " -p \"-Dstart_time=\\\"'" + start_time + "'\\\""
                +" -Dend_time=\\\"'" + end_time + "'\\\""
                +" -Dcard_no='" + card_no + "'"
                +" -Dclinic_code='" + clinic_code + "'"
                +"\"";


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
            System.out.println("waitFor"+pr.waitFor());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if(pr!=null){
                pr.destroy();
            }
        }
    }
}
