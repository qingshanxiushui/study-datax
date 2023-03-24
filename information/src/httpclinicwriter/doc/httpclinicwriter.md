# DataX httpclinicwriter 说明


------------

## 1 快速介绍

httpclinicwriter提供调用http rest接口, 传入json数据请求。

## 2 功能与限制

(1)、将行列格式数据转化为固定json结构，并调用http rest接口发送请求。
(2)、支持action、type、param_visit_id、param_patient_id统一处理和分开处理。
(3)、请求json数据格式统一为字符串格式

## 3 功能说明


### 3.1 配置样例

```json
{
  "job": {
    "content": [
      {
        "reader": {
          "name": "mysqlreader",
          "parameter": {
            "column": [],
            "connection": [
              {
                "querySql":  ["
                  select
                  'sd2313213123213&qa2345698' as record_id,
                  '检验记录&住院记录' as record_type,
                  'da324141&sd12234' as medical_id,
                  'ZY1232132131&QA234567' as visit_id,
                  '2313123213&9576534' as patient_id,
                  '13岁&30岁' as age,
                  '1&9' as gender,
                  '2008-11-1&2012-12-10' as birth_date,
                  '2021-10-23 11:11:11&2022-12-11 12:12:12' as record_time,
                  '2021-10-23 08-23-25&2023-10-23 09-23-25' as admission_time,
                  '广医附一&盛京附一' as hospital,
                  '心血管内科&皮肤外科' as dept,
                  '心血管内科&皮肤外科' as apply_dept,
                  '血常规&皮肤病' as exam_name,
                  '血液标本&组织和标本' as exam_method,
                  '血液&血液' as sample_category,
                  '合格&合格' as sample_status,
                  '23213&97988' as sample_id,
                  '2021-10-23 11:05:09&2023-10-23 11:05:09' as sample_time,
                  '2021-10-23 11:10:09&2023-10-23 11:10:09' as receive_time,
                  '2021-10-23 11:12:09&2023-10-23 11:12:09' as exam_time,
                  '2021-10-23 11:15:09&2023-10-23 11:15:09' as report_time,
                  '血红蛋白;白蛋白&血清;血脂' as item_name,
                  'Hb;albumin&KD;dada' as item_abbr,
                  '153;70&173;85' as item_result,
                  'g/L;g/dl&g/L;g/dl' as item_unit,
                  '高;高&低;低' as item_hint

                  union all

                  select
                  '呃呃呃呃' as record_id,
                  '呃呃呃呃' as record_type,
                  '呃呃呃呃' as medical_id,
                  '呃呃呃呃' as visit_id,
                  '呃呃呃呃' as patient_id,
                  '呃呃呃呃' as age,
                  '呃呃呃呃' as gender,
                  '呃呃呃呃' as birth_date,
                  '呃呃呃呃' as record_time,
                  '呃呃呃呃' as admission_time,
                  '呃呃呃呃' as hospital,
                  '呃呃呃呃' as dept,
                  '呃呃呃呃' as apply_dept,
                  '呃呃呃呃' as exam_name,
                  '呃呃呃呃' as exam_method,
                  '呃呃呃呃' as sample_category,
                  '呃呃呃呃' as sample_status,
                  '呃呃呃呃' as sample_id,
                  '呃呃呃呃' as sample_time,
                  '呃呃呃呃' as receive_time,
                  '呃呃呃呃' as exam_time,
                  '呃呃呃呃' as report_time,
                  '呃呃呃呃' as item_name,
                  '呃呃呃呃' as item_abbr,
                  '呃呃呃呃' as item_result,
                  '呃呃呃呃' as item_unit,
                  '呃呃呃呃' as item_hint
                  "],
                  "jdbcUrl": ["jdbc:mysql://127.0.0.1:3306/clinic?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC"]
                  }
                ],
                "password": "123456",
                "username": "root",
                "where": ""
              }
            },
              "writer": {
                "name": "httpclinicwriter",
                "parameter": {
                  "connectTimeout": 20000,
                  "encoding": "UTF-8",
                  "host": "127.0.0.1",
                  "path": "/hello/json",
                  "port": 8088,
                  "readTimeout": 20000,
                  "token": "",
                  "generateMethod": 2,
                  "buffer": 2,
                  "fieldDelimiter": ";",
                  "columnDelimiter": "&",
                  "action": "save_emr",
                  "type": "auxiliary_exam_recommend;clinical_pathway_recommend",
                  "param_visit_id": "23",
                  "param_patient_id": "123456789",
                  "column":[
                    "record_id",
                    "record_type",
                    "medical_id",
                    "visit_id",
                    "patient_info_id",
                    "patient_info_age",
                    "patient_info_gender",
                    "patient_info_birth_date",
                    "record_time",
                    "admission_time",
                    "hospital",
                    "dept",
                    "apply_dept",
                    "exam_name",
                    "exam_method",
                    "sample_category",
                    "sample_status",
                    "sample_id",
                    "sample_time",
                    "receive_time",
                    "exam_time",
                    "report_time",
                    "item_name",
                    "item_abbr",
                    "item_result",
                    "item_unit",
                    "item_hint"
                  ]
                }
              }
              }
            ],
            "setting": {
              "speed": {
                "channel": "2"
              }
            }
          }
        }
```

### 3.2 参数说明

* **host**

    * 描述：ip地址。格式：hdfs://ip:端口；例如：127.0.0.1<br />

    * 必选：是 <br />

    * 默认值：127.0.0.1 <br />

* **port**

    * 描述：端口号。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **path**

    * 描述：请求路径。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **token**

    * 描述：token。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **token**

    * 描述：token。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **connectTimeout**

    * 描述：链接超时。 <br />

    * 必选：否 <br />

    * 默认值：20*1000毫秒 <br />

* **readTimeout**

    * 描述：读取数据超时。 <br />

    * 必选：否 <br />

    * 默认值：20*1000毫秒 <br />

* **encoding**

    * 描述：编码方式。 <br />

    * 必选：否 <br />

    * 默认值：UTF-8 <br />

* **generateMethod**

    * 描述：json生成方式。1 每行数据生成1个json并发送请求， 2 多行数据生成1个json并发送请求，使用于json中action、type、param参数相同情况。 <br />

    * 必选：否 <br />

    * 默认值：1 <br />

* **buffer**

    * 描述：仅generateMethod=2生效，控制多少行数据生成一个json。 <br />

    * 必选：否 <br />

    * 默认值：10 <br />

* **fieldDelimiter**

    * 描述：分割符。将列数据中的值分割成多个值，存在json list中，如type，result_info。 <br />

    * 必选：否 <br />

    * 默认值：';' <br />

* **columnDelimiter**

    * 描述：分割符。将列数据中的值分割成多个值，存在json emr_contents list中。<br />

    * 必选：否 <br />

    * 默认值：'&' <br />

* **action**

    * 描述：json结构action值，仅generateMethod=2生效。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **type**

    * 描述：json结构type值，仅generateMethod=2生效。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **param_visit_id**

    * 描述：json结构param内visit_id值，仅generateMethod=2生效。  <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **param_patient_id**

    * 描述：json结构param内patient_id值，仅generateMethod=2生效。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

* **column**

    * 描述：json结构字段与 行数中字段对应关系。行列数据第一列，对应column中第一个字段。 <br />

    * 必选：否 <br />

    * 默认值：无 <br />

### 3.3 类型转换

全部转为string类型

## 4 性能报告


## 5 约束限制

略

## 6 FAQ

略

