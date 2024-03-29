package com.alibaba.datax.plugin.writer.httpclinicwriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PostgreUtil {

    private final Connection connect;
    private final String userName;
    private final String passWord;
    private final String ipAddress;
    private final String databaseName;
    private final String port;

    //构造方法
    public PostgreUtil(String userName, String passWord, String ipAddress, String databaseName, String port) {
        this.userName = userName;
        this.passWord = passWord;
        this.ipAddress = ipAddress;
        this.databaseName = databaseName;
        this.port = port;
        this.connect = this.Connect();
    }

    //建立链接
    private Connection Connect() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://" + this.ipAddress + ":" + this.port + "/" + this.databaseName,
                            this.userName, this.passWord);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
    }

    //关流操作
    public void close() {
        Connection c = this.connect;
        try {
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //查询
    public Map<String,HashMap<String, Object>> Select(String sql) {
        //1、与数据库建立链接
        Connection c = this.connect;
        //2、创建操作对象
        Statement stmt = null;
        //3、创建返回最终查询的数据集合
        //List<HashMap<String, Object>> list = new ArrayList<>();
        Map<String,HashMap<String, Object>> mapCache = new HashMap<>();
        try {
            //2.1、初始化操作对象
            stmt = c.createStatement();
            //4、执行需要执行的sql语句
            ResultSet rs = stmt.executeQuery(sql);
            //3.1开始封装返回的对象
            ResultSetMetaData metaData = rs.getMetaData();//获取全部列名
            int columnCount = metaData.getColumnCount();//列的数量
            //5、读取数据
            while (rs.next()) {
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    //getColumnName获取列名
                    String name = metaData.getColumnName(i);
                    //获取对应的元素
                    Object object = rs.getObject(i);
                    map.put(name, object);
                }
                //list.add(map);
                mapCache.put(map.get("emr_type").toString(),map);
            }
            //6、关流操作
            rs.close();
            stmt.close();
            //c.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return mapCache;
    }

}
