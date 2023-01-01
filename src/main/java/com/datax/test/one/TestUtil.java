package com.datax.test.one;

import com.datax.test.one.DataXUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * 参考 https://blog.csdn.net/qq_41457131/article/details/114376325
 */
public class TestUtil {
    public static void main(String[] args) throws IOException {
        //dataxMethodOne();
        DataXUtil.exeDatax("D:\\DataX\\datax\\job\\mysql2stream.json");

    }

    private static void dataxMethodOne() throws IOException {
        //数据库类型 （1-Oracle，2-MySql）
        String reader = DataXUtil.createReader(2, "root", "123456", "select name,age,sex,create_time from user where sex = 'female' and create_time > '2022-12-28 00:00:00' and create_time < '2022-12-29 00:00:00'", "jdbc:mysql://127.0.0.1:3306/test2?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");

        List<String> list = new ArrayList<>();
        list.add("name");
        list.add("age");
        list.add("sex");
        list.add("create_time");

        //reader中的查询字段顺序要与list中相对应,包括数据类型
        //数据库类型 （1-Oracle，2-MySql）
        String writer = DataXUtil.createWriter(2, "root", "123456", "jdbc:mysql://127.0.0.1:3306/test1?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC","user",list);

        String jsonPath = DataXUtil.createJobJson(reader + writer);

        System.out.println(jsonPath);

        DataXUtil.exeDatax(jsonPath);
    }
}
