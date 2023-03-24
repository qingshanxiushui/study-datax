package com.alibaba.datax.plugin.writer.httpwriter;
import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.plugin.TaskPluginCollector;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpWriter extends Writer{

    public static class Job extends Writer.Job{
        private static final Logger LOG = LoggerFactory.getLogger(Job.class);
        private Configuration originConfig = null;
        @Override
        public void init() {
            this.originConfig = this.getPluginJobConf();
        }

        @Override
        public void prepare() {}

        @Override
        public List<Configuration> split(int mandatoryNumber) {
            LOG.debug("split() begin...");
            List<Configuration> writerSplitConfigs = new ArrayList<Configuration>();
            Configuration splitedConfig = this.originConfig.clone();
            writerSplitConfigs.add(splitedConfig);
            LOG.debug("split() ok and end...");
            return writerSplitConfigs;
        }

        @Override
        public void post() {

        }

        @Override
        public void destroy() {}


    }

    public static class Task extends Writer.Task {
        private static final Logger LOG = LoggerFactory.getLogger(Task.class);
        private Configuration writerSliceConfig;
        private String host;
        private int port;
        private String path;
        private int connectTimeout;
        private int readTimeout;
        private String body;
        private String token;
        private HttpURLConnection conn;
        @Override
        public void init() {
            this.writerSliceConfig = this.getPluginJobConf();
            this.host = writerSliceConfig.getString(Key.HOST);
            this.port = writerSliceConfig.getInt(Key.PORT);
            this.path = writerSliceConfig.getString(Key.PATH);
            this.connectTimeout = writerSliceConfig.getInt(Key.CONNECTTIMEOUT,Constant.CONNECT_TIMEOUT);
            this.readTimeout = writerSliceConfig.getInt(Key.READTIMEOUT,Constant.READ_TIMEOUT);
            this.token = writerSliceConfig.getString(Key.TOKEN);
            try{
                String lasturl = "http://" + this.host + ":" + this.port + this.path;
                LOG.info(lasturl);
                URL url = new URL(lasturl);
                this.conn = (HttpURLConnection) url.openConnection();
                //请求头
                this.conn.setRequestProperty("Accept-Charset", "utf-8");
                this.conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                this.conn.setDoOutput(true);
                this.conn.setDoInput(true);
                this.conn.setRequestMethod(Constant.POST_REQUEST);//POST必须全大写
                this.conn.setConnectTimeout(connectTimeout);
                this.conn.setReadTimeout(readTimeout);
                this.conn.setRequestProperty("Cookie", "SSID=" + token);
                this.conn.connect();
            }catch (Exception e){
                String message = String.format("与http服务器建立连接失败 : [%s]",
                        "message:host =" + host + ",port =" + port);
                LOG.error(message);
                throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_LOGIN, message, e);
            }}

        @Override
        public void prepare() {}

        @Override
        public void startWrite(RecordReceiver lineReceiver) {
            LOG.debug("start write http source ...");
            try {
                this.body =  generatePostRequestJson(lineReceiver,this.writerSliceConfig,this.getTaskPluginCollector());
                //输入流
                //解决中文乱码
                OutputStreamWriter os = new OutputStreamWriter(this.conn.getOutputStream(), "UTF-8");
                os.write(this.body);
                os.flush();
                os.close();
                int code = this.conn.getResponseCode();//获得响应码
                LOG.debug("http返回响应码:",code);
                if (code == 200) {//响应成功，获得响应的数据
                    LOG.debug(" write http success ...");
                }else{
                    LOG.debug("write http fail  ...");
                }
            }catch(IOException e){
                String message = String.format("获得getOutputStream()异常");
                LOG.error(message);
                throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_GET_OUTPUT_STREAM, message, e);
            }catch (Exception e){
                if (e instanceof DataXException) {
                    throw (DataXException) e;
                }
                String message = String.format("http startWrite异常");
                LOG.error(message);
                throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_HTTP_WRITE, message, e);
            }
            LOG.debug("end write http source ...");
        }

        private String generatePostRequestJson(RecordReceiver lineReceiver, Configuration writerSliceConfig, TaskPluginCollector taskPluginCollector) {
            WriteParamsDto writeParamsDto = new WriteParamsDto();
            Record record = null;
            while ((record = lineReceiver.getFromReader()) != null) {
                WriteRecordDto writeRecordDto = new WriteRecordDto();
                int recordLength = record.getColumnNumber();
                if (0 != recordLength) {
                    Column column;
                    for (int i = 0; i < recordLength; i++) {
                        column = record.getColumn(i);
                        if (null != column.getRawData()) {
                            writeRecordDto.addColumn(new WriteColumnDto(column.asString()));
                        }
                    }
                }
                if(writeRecordDto.getColumnSize()>0){
                    writeParamsDto.addRecord(writeRecordDto);
                }
            }
            return JSON.toJSONString(writeParamsDto);
        }

        @Override
        public void post() {}

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
