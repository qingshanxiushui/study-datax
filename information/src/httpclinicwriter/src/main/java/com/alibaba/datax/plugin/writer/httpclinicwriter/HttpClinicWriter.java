package com.alibaba.datax.plugin.writer.httpclinicwriter;
import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.plugin.TaskPluginCollector;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClinicWriter extends Writer{
    public static class Job extends Writer.Job{
        private static final Logger LOG = LoggerFactory.getLogger(Job.class);
        private Configuration originConfig = null;

        private  String dbIpAddress;
        private  int dbPort;
        private  String dbDatabaseName;
        private  String dbUserName;
        private  String dbPassWord;
        private  String dataType;
        @Override
        public void init() {
            this.originConfig = this.getPluginJobConf();
            this.dataType = originConfig.getString(Key.DATATYPE,"");
            this.dbIpAddress = originConfig.getString(Key.DBIPADDRESS,"");
            this.dbPort = originConfig.getInt(Key.DBPORT,0);
            this.dbDatabaseName = originConfig.getString(Key.DBDATABASENAME,"");
            this.dbUserName = originConfig.getString(Key.DBUSERNAME,"");
            this.dbPassWord = originConfig.getString(Key.DBPASSWORD,"");
            accessPostgre();
        }
        public void accessPostgre(){
            //构造方法
            PostgreUtil postgreUtil =new PostgreUtil(dbUserName,dbPassWord,dbIpAddress,dbDatabaseName,dbPort+"");
            //查询
            Map<String,HashMap<String, Object>> select = postgreUtil.Select("select eti.id as info_id, eti.emr_type as emr_type, eti.\"version\" as \"version\" , ed.id as definition_id, ed.field_info as field_info from emr_type_info eti  join emr_definition ed on eti.id = ed.emr_type_id and eti.\"version\" = ed.\"version\"  where eti.deleted = 0 and eti.usable = 1");
            //关流
            postgreUtil.close();
            JSONArray emrContext = JSONObject.parseArray((String)select.get(dataType).get("field_info"));
            if(emrContext.isEmpty()){
                String message = String.format("未从db获取到相应json结构定义");
                LOG.error(message);
                throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_JSON_DEFINE, message);
            }
            int columnCount =0 ;
            for (Object emrObject : emrContext){
                JSONObject emr = (JSONObject)emrObject;
                if(((String)emr.get("data_type")).equals("object") || ((String)emr.get("data_type")).equals("array")){
                    for (Object patientObject : (JSONArray)emr.get("item")){
                        columnCount++;
                    }
                }else{
                    columnCount++;
                }
            }
            LOG.info("db中json结构:"+emrContext);
            this.originConfig.set(Key.DBCOLUMNCOUNT,columnCount);
            this.originConfig.set(Key.DBJSON,(String)select.get(dataType).get("field_info"));
        }

        @Override
        public void prepare() {}

        @Override
        public List<Configuration> split(int mandatoryNumber) {
            LOG.info("HttpClinicWriter job split() begin...");
            List<Configuration> writerSplitConfigs = new ArrayList<Configuration>();
            for(int i=0;i<mandatoryNumber;i++){
                Configuration splitedConfig = this.originConfig.clone();
                writerSplitConfigs.add(splitedConfig);
            }
            LOG.info("HttpClinicWriter job split() ok and end...");
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
        private int buffer;
        private String fieldDelimiter;
        private int dbColumnCount;
        private JSONArray emrContext;
        private String lasturl;
        @Override
        public void init() {
            this.writerSliceConfig = this.getPluginJobConf();
            this.host = writerSliceConfig.getString(Key.HOST,"127.0.0.1");
            this.port = writerSliceConfig.getInt(Key.PORT,0);
            this.path = writerSliceConfig.getString(Key.PATH);
            this.connectTimeout = writerSliceConfig.getInt(Key.CONNECTTIMEOUT,Constant.CONNECT_TIMEOUT);
            this.readTimeout = writerSliceConfig.getInt(Key.READTIMEOUT,Constant.READ_TIMEOUT);
            this.token = writerSliceConfig.getString(Key.TOKEN);

            this.buffer = writerSliceConfig.getInt(Key.BUFFER,Constant.BUFFER);
            this.fieldDelimiter = writerSliceConfig.getString(Key.FIELDDELIMITER,Constant.FIELD_DELIMITER);
            this.dbColumnCount = writerSliceConfig.getInt(Key.DBCOLUMNCOUNT,0);
            this.emrContext = JSONObject.parseArray(writerSliceConfig.getString(Key.DBJSON,""));

            this.lasturl = "http://" + this.host + (this.port==0?"":":"+this.port) + this.path;

            LOG.info("请求的url:"+lasturl);
        }

        @Override
        public void prepare() {}

        @Override
        public void startWrite(RecordReceiver lineReceiver) {
            LOG.info("start write http source ...");
            try {
                Record record;
                int bufferIndex = 0;
                JSONArray emrJsonArray = new JSONArray();
                while ((record = lineReceiver.getFromReader()) != null) {
                    int recordLength = record.getColumnNumber();
                    if (this.dbColumnCount == recordLength) {  //source数据列数必须和db json列个数一致
                        //处理emr数据内容
                        JSONObject emrJsonObject = processEmrContent(record);
                        emrJsonArray.add(emrJsonObject);
                        bufferIndex = bufferIndex + 1;
                        LOG.info("bufferIndex: " + bufferIndex);
                        LOG.info("buffer: " + this.buffer);
                        if (bufferIndex >= this.buffer) { //缓冲一定数据行数,包装在一个json中发送
                            //构造最终结果
                            this.body = processJsonHead(emrJsonArray);
                            emrJsonArray = new JSONArray();
                            bufferIndex = 0;
                            LOG.info("统一头信息，发送请求body数据: " + this.body);
                            //发送http请求
                            httpSendData(this.body);
                        }
                    }else{
                        String message = String.format("数据列数不对");
                        this.getTaskPluginCollector().collectDirtyRecord(record, message);
                        LOG.error(message);
                        throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_COLUMN_NUMBER, message);
                    }
                }
                if(!emrJsonArray.isEmpty()){
                    this.body = processJsonHead(emrJsonArray);
                    LOG.info("统一头信息最后不满足缓存大小部分数据处理，发送请求body数据: "+ this.body);
                    httpSendData(this.body);
                }
            }catch (Exception e){
                if (e instanceof DataXException) {
                    throw (DataXException) e;
                }
                String message = String.format("http startWrite异常");
                LOG.error(message);
                throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_HTTP_WRITE, message, e);
            }
            LOG.info("end write http source ...");
        }

        private JSONObject processEmrContent(Record record) {
            int columnIndex = 0;
            JSONObject emrJsonObject = new JSONObject();
            for (Object emrObject : emrContext){
                JSONObject emr = (JSONObject)emrObject;
                if(((String)emr.get("data_type")).equals("object")){
                    JSONObject emrPatientObject = new JSONObject();
                    for (Object patientObject : (JSONArray)emr.get("item")){
                        JSONObject patient = (JSONObject)patientObject;
                        emrPatientObject.put((String)patient.get("field_name"), record.getColumn(columnIndex).asString());
                        columnIndex = columnIndex + 1;
                    }
                    emrJsonObject.put((String)emr.get("field_name"),emrPatientObject);
                }else if(((String)emr.get("data_type")).equals("array")){
                    JSONArray resultJsonArray = new JSONArray();
                    JSONObject resultJsonObject;
                    for (Object resultObject : (JSONArray)emr.get("item")){
                        JSONObject result = (JSONObject)resultObject;
                        int resultLength = (record.getColumn(columnIndex).asString()).split(";").length;
                        for(int loop=0;loop<resultLength;loop++){
                            if(loop>= resultJsonArray.size()){
                                resultJsonObject = new JSONObject();
                                resultJsonArray.add(resultJsonObject);
                            }else{
                                resultJsonObject = (JSONObject)resultJsonArray.get(loop);
                            }
                            resultJsonObject.put((String)result.get("field_name"), (record.getColumn(columnIndex).asString()).split(";")[loop]);
                            resultJsonArray.set(loop,resultJsonObject);
                        }
                        columnIndex = columnIndex +1;
                    }
                    emrJsonObject.put((String)emr.get("field_name"),resultJsonArray);
                }else{
                    emrJsonObject.put((String)emr.get("field_name"), record.getColumn(columnIndex).asString());
                    columnIndex = columnIndex + 1;
                }
            }
            return emrJsonObject;
        }

        private String processJsonHead(JSONArray emrJsonArray) {
            JSONObject httpJson = new JSONObject();
            httpJson.put("action","save_emr");
            JSONArray typeJson = new JSONArray();
            httpJson.put("type",typeJson);
            JSONArray paramsJson = new JSONArray();
            httpJson.put("params",paramsJson);
            httpJson.put("emr_contents",emrJsonArray);
            return httpJson.toJSONString();
        }

        private void httpSendData(String body){
            try{
                URL url = new URL(lasturl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //请求头
                conn.setRequestProperty("Accept-Charset", "utf-8");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod(Constant.POST_REQUEST);//POST必须全大写
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
                conn.setRequestProperty("Cookie", "SSID=" + token);
                conn.connect();
                //输入流
                //解决中文乱码
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                os.write(this.body);
                os.flush();
                os.close();
                int code = conn.getResponseCode();//获得响应码
                LOG.info("http返回响应码:"+code);
                if (code == 200) {//响应成功，获得响应的数据
                    LOG.info(" write http success ...return code = 200");
                }else{
                    LOG.info("write http fail  ...return code = "+code);
                }
                conn.disconnect();
            }catch(MalformedURLException e){
                String message = String.format("url转换失败 : [%s]",
                        "message:host =" + host + ",port =" + port + ",path =" + path);
                LOG.error(message);
                throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_URL_TRANSFORMATION, message, e);
            }catch(IOException e){
                String message = String.format("IO异常");
                LOG.error(message);
                throw DataXException.asDataXException(HttpWriterErrorCode.FAIL_IO, message, e);
            }
        }

        @Override
        public void post() {}

        @Override
        public void destroy() {}
    }
}
