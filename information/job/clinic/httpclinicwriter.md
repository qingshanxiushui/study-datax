# DataX httpclinicwriter 说明


------------

## 1 快速介绍

httpclinicwriter提供调用http rest接口, 传入json数据请求。

## 2 功能与限制

(1)、将行列格式数据转化为固定json结构，并调用http rest接口发送请求。
(2)、reader数据列顺序，必须与json定义字段顺序一致
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
	with t1 as (
		select 
			ttrd.TEST_CODE as record_id,
			group_concat(ttrd.ITEM_NAME SEPARATOR ';') as item_name,
			group_concat(ttrd.ITEM_ENG_NAME SEPARATOR ';') as item_abbr,
			group_concat(ttrd.VALUE SEPARATOR ';') as item_result,
			group_concat(ttrd.ITEM__UNIT SEPARATOR ';') as item_unit,
			group_concat(ttrd.LIS_RESULT SEPARATOR ';') as item_hint
		from th_test_record_detail ttrd 
		group by TEST_CODE
	), t2 as (
		select 
			ttr.TEST_CODE as record_id,
			'检验记录' as record_type,
			'da324141' as medical_id,
			ttr.TREATMENT_CODE as visit_id,
			ttr.PATIENT_ID as patient_id,
			'13岁' as age,
			'岁' as unit,
			p.SEX_CODE as gender,
			p.BIRTHDAY as birth_date,
			ttr.OPER_TIME as record_time,
			'2021-10-23 08-23-25' as admission_time,
			'广医附一' as hospital,
			p.dept_name as dept,
			'心血管内科' as apply_dept,
			ttr.ORDER_NAME as exam_name,
			'血液标本' as exam_method,
			ttr.SAMPLETYPE as sample_category,
			'合格' as sample_status,
			ttr.SAMPLE_ID as sample_id,
			'2021-10-23 11:05:09' as sample_time,
			'2021-10-23 11:10:09' as receive_time,
			ttr.TESET_DATE as exam_time,
			'2021-10-23 11:15:09' as report_time
		from th_test_record ttr, vhis_register p
		where p.CLINIC_CODE = ttr.TREATMENT_CODE
		and ttr.OPER_TIME > ${start_time} 
		and ttr.OPER_TIME < ${end_time}
		and p.CARD_NO = ${card_no}
		and p.CLINIC_CODE = ${clinic_code}
	)
	select 
		t2.record_id,
		t2.record_type,
		t2.medical_id,
		t2.visit_id,
		t2.patient_id,
		t2.age,
		t2.unit,
		t2.gender,
		t2.birth_date,
		t2.hospital,
		t2.record_time,
		t2.dept,
		t2.apply_dept,
		t2.exam_name,
		t2.exam_method,
		t2.sample_category,
		t2.sample_status,
		t2.sample_id,
		t2.sample_time,
		t2.receive_time,
		t2.exam_time,
		t2.report_time,
		t1.item_name,
		t1.item_abbr,
		t1.item_result,
		t1.item_unit,
		t1.item_hint
	from t1, t2
	where t1.record_id = t2.record_id
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
                        "host": "10.33.19.214",
                        "path": "/medkb-api/recommend",
                        "readTimeout": 20000,
                        "token": "",
						"buffer": 10,
						"fieldDelimiter": ";",
						"dataType":"检验记录",
						"dbIpAddress": "10.33.19.214",
						"dbPort": 5432,
						"dbDatabaseName": "carerec",
                        "dbUserName": "postgres",
                        "dbPassWord": "CareRec@2021"
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

* **buffer**

    * 描述：仅generateMethod=2生效，控制多少行数据生成一个json。 <br />

    * 必选：否 <br />

    * 默认值：10 <br />

* **fieldDelimiter**

    * 描述：分割符。将列数据中的值分割成多个值，存在json list中，如type，result_info。 <br />

    * 必选：否 <br />

    * 默认值：';' <br />

* **dataType**

    * 描述：传输数据类型，用于从数据获取json结构。例"检验记录" <br />

    * 必选：是 <br />

    * 默认值：无 <br />

* **dbIpAddress**

    * 描述：获取json结构数据库ip <br />

    * 必选：是 <br />

    * 默认值：无 <br />

* **dbPort**

    * 描述：获取json结构数据库端口号 <br />

    * 必选：是 <br />

    * 默认值：无 <br />

* **dbDatabaseName**

    * 描述：获取json结构数据库名 <br />

    * 必选：是 <br />

    * 默认值：无 <br />
	
* **dbUserName**

    * 描述：获取json结构数据库用户名 <br />

    * 必选：是 <br />

    * 默认值：无 <br />


* **dbPassWord**

    * 描述：获取json结构数据库密码 <br />

    * 必选：是 <br />

    * 默认值：无 <br />


### 3.3 类型转换

全部转为string类型

## 4 使用方式

python datax.py -r mysqlreader -w httpclinicwriter  查看输入输出job配置格式信息

### 一次行使用

手动执行命令 

python datax.py D:\DataX\datax\job\clinic_mysql2http_param.json -p "-Dstart_time=\"'2022-01-01 00:00:00'\" -Dend_time=\"'2032-01-01 00:00:00'\" -Dcard_no='874635233656' -Dclinic_code='ZY1232132131'" 

JAVA代码调用执行

使用Java Runtime.getRuntime().exec(command)类方法，详情参见dataxParamsOnlineTest.java代码

### 定时使用

使用python脚本，配置crontab。

python脚本参考clinic_syn.py。

crontab配置如下：

10 0 * * * python clinic_syn.py D:\DataX\datax\job\clinic_mysql2http_param.json D:\DataX\script\log\test_log.log 874635233656 ZY1232132131

Linux之crontab配置参考如下链接：

https://blog.csdn.net/weixin_41831919/article/details/108542764 


以上使用方式，需修改相关目录。

## 4 性能报告


## 5 约束限制

略

## 6 FAQ

略

