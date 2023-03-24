package com.alibaba.datax.plugin.reader.httpreader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import org.mortbay.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordSender;
import com.alibaba.datax.common.spi.Reader;
import com.alibaba.datax.common.util.Configuration;

public class HttpReader extends Reader {

    public static class Job extends Reader.Job{
        private static final Logger LOG = LoggerFactory.getLogger(Job.class);
        private Configuration originConfig = null;
        @Override
        public void init() {
            this.originConfig = this.getPluginJobConf();
        }

        @Override
        public void prepare() {}

        @Override
        public List<Configuration> split(int adviceNumber) {
            LOG.debug("split() begin...");
            List<Configuration> readerSplitConfigs = new ArrayList<Configuration>();
            Configuration splitedConfig = this.originConfig.clone();
            readerSplitConfigs.add(splitedConfig);
            LOG.debug("split() ok and end...");
            return readerSplitConfigs;
        }

        @Override
        public void post() {
        }

        @Override
        public void destroy() {}

    }

    public static class Task extends Reader.Task{
        private static Logger LOG = LoggerFactory.getLogger(Task.class);
        private Configuration readerSliceConfig;
        private String host;
        private int port;
        private String path;
        private String urlParam;
        private String requestMethod;
        private int connectTimeout;
        private int readTimeout;
        private String body;
        private String token;
        private HttpURLConnection conn;

        @Override
        public void init() {
            this.readerSliceConfig = this.getPluginJobConf();
            this.host = readerSliceConfig.getString(Key.HOST);
            this.port = readerSliceConfig.getInt(Key.PORT);
            this.path = readerSliceConfig.getString(Key.PATH);
            this.urlParam = readerSliceConfig.getString(Key.URLPARAM);
            this.requestMethod = readerSliceConfig.getString(Key.REQUESTMETHOD);
            this.connectTimeout = readerSliceConfig.getInt(Key.CONNECTTIMEOUT,Constant.CONNECT_TIMEOUT);
            this.readTimeout = readerSliceConfig.getInt(Key.READTIMEOUT,Constant.READ_TIMEOUT);
            this.body = readerSliceConfig.getString(Key.BODY);
            this.token = readerSliceConfig.getString(Key.TOKEN);
            try{
                String lasturl = "http://" + this.host + ":" + this.port + this.path + new String(this.urlParam.getBytes("iso8859-1"),"utf-8");
                LOG.info(lasturl);
                URL url = new URL(lasturl);
                this.conn = (HttpURLConnection) url.openConnection();
                //请求头
                this.conn.setRequestProperty("Accept-Charset", "utf-8");
                this.conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                this.conn.setDoOutput(true);
                this.conn.setDoInput(true);
                this.conn.setRequestMethod(requestMethod);//GET和POST必须全大写
                this.conn.setConnectTimeout(connectTimeout);
                this.conn.setReadTimeout(readTimeout);
                this.conn.setRequestProperty("Cookie", "SSID=" + token);
                this.conn.connect();
            }catch (Exception e){
                String message = String.format("与http服务器建立连接失败 : [%s]",
                        "message:host =" + host + ",port =" + port);
                LOG.error(message);
                throw DataXException.asDataXException(HttpReaderErrorCode.FAIL_LOGIN, message, e);
            }}

        @Override
        public void prepare(){}

        @Override
        public void startRead(RecordSender recordSender) throws IOException {
            Record record = recordSender.createRecord();
            StringBuffer responseBuffer = new StringBuffer("");
            String responseData = "response false";
            try {
                LOG.debug("start read http source ...");

                if(this.requestMethod.equals(Constant.POST_REQUEST)){
                    //输入流
                    //解决中文乱码
                    OutputStreamWriter os = new OutputStreamWriter(this.conn.getOutputStream(), "UTF-8");
                    os.write(this.body);
                    os.flush();
                    os.close();
                }
                int code = this.conn.getResponseCode();//获得响应码
                LOG.debug("http返回响应码:",code);
                if (code == 200) {//响应成功，获得响应的数据
                    //解决中文乱码
                    BufferedReader reader = new BufferedReader(new InputStreamReader(this.conn.getInputStream(), "UTF-8"));
                    String lines;
                    responseBuffer = new StringBuffer("");
                    while ((lines = reader.readLine()) != null) {
                        responseBuffer.append(lines);
                        responseBuffer.append("\r\n");
                    }
                    responseData = responseBuffer.toString();
                    LOG.debug("http返回响应数据:",responseData);
                }
                //写入record
                Column columnGenerated = null;
                columnGenerated = new StringColumn(responseData);
                record.addColumn(columnGenerated);
                recordSender.sendToWriter(record);
                recordSender.flush();
                LOG.debug("end read http source ...");
            }catch(IOException e){
                String message = String.format("获得getOutputStream()异常");
                LOG.error(message);
                throw DataXException.asDataXException(HttpReaderErrorCode.FAIL_GET_OUTPUT_STREAM, message, e);
            }catch (Exception e){
                if (e instanceof DataXException) {
                    throw (DataXException) e;
                }
                String message = String.format("http startRead异常");
                LOG.error(message);
                // 每一种转换失败都是脏数据处理,包括数字格式 & 日期格式
                this.getTaskPluginCollector().collectDirtyRecord(record, e.getMessage());
            }
        }

        @Override
        public void post(){}

        @Override
        public void destroy() {
            try {
                this.conn.disconnect();
            } catch (Exception e) {
                String message = String.format(
                        "关闭与http服务器连接失败: [%s] host=%s, port=%s",
                        e.getMessage(), this.host, this.port);
                LOG.error(message, e);
            }
        }


    }
}
