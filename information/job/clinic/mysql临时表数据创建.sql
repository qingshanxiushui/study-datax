
create table th_test_record_detail(
	ITEM_NAME varchar(20) not null, 
	ITEM_ENG_NAME varchar(20) not null, 
	VALUE varchar(20) not null, 
	ITEM__UNIT varchar(20) not null, 
	LIS_RESULT varchar(20) not null, 
	TEST_CODE  varchar(20) not null
);

insert into th_test_record_detail(ITEM_NAME, ITEM_ENG_NAME, VALUE, ITEM__UNIT, LIS_RESULT, TEST_CODE) 
	values ('血红蛋白', 'Hb', '153', 'g/L', '高','sd2313213123213'),
		('血蛋白', 'albumin', '70', 'g/dl', '高','sd2313213123213'),
		('其它', 'albumin', '70', 'g/dl', '高','sd3325354323434');

select * from th_test_record_detail;


create table th_test_record(
	TREATMENT_CODE varchar(20) not null, 
	OPER_TIME datetime not null, 
	TEST_CODE varchar(20) not null, 
	PATIENT_ID varchar(20) not null, 
	ORDER_NAME varchar(20) not null, 
	SAMPLETYPE  varchar(20) not null,
	SAMPLE_ID  varchar(20) not null,
	TESET_DATE  datetime not null
);

insert into th_test_record(TREATMENT_CODE, OPER_TIME, TEST_CODE, PATIENT_ID, ORDER_NAME, SAMPLETYPE, SAMPLE_ID, TESET_DATE) 
	values ('ZY1232132131', '2022-12-22 11:12:09', 'sd2313213123213', '2313123213', '血常规','血液','23213','2022-12-22 11:12:09'),
	('ZY1232132131', '2021-10-20 11:12:09', 'sd3325354323434', '2313123213', '其它','其它','23213','2021-10-20 11:12:09');
	
select * from th_test_record;


create table vhis_register(
	CLINIC_CODE varchar(20) not null, 
	CARD_NO varchar(20) not null, 
	SEX_CODE varchar(20) not null, 
	BIRTHDAY date not null, 
	dept_name varchar(20) not null
);

insert into vhis_register(CLINIC_CODE, CARD_NO, SEX_CODE, BIRTHDAY, dept_name)
	values('ZY1232132131', '874635233656', '1', '2008-11-1', '心血管内科'),
	('ZY1232132131', '234343534542', '1', '2008-11-1', '心血管内科');



select * from vhis_register;

--------------------------------------------------------------

-----仅一条结果,打平
( 仅一条结果,打平
	select 
		ttr.TEST_CODE as record_id,
		'检验记录' as record_type,
		'' as medical_id,
		ttr.TREATMENT_CODE as visit_id,
		ttr.PATIENT_ID as patient_id,
		'' as age,
		p.SEX_CODE as gender,
		p.BIRTHDAY as birth_date,
		'广州医学院附属第一医院' as hospital,
		ttr.OPER_TIME as record_time,
		p.dept_name as dept,
		'' as apply_dept,
		ttr.ORDER_NAME as exam_name,
		'' as exam_method,
		ttr.SAMPLETYPE as sample_category,
		'' as sample_status,
		ttr.SAMPLE_ID as sample_id,
		'' as sample_time,
		'' as receive_time,
		ttr.TESET_DATE as exam_time,
		'' as report_time,
		ttrd.ITEM_NAME as item_name,
		ttrd.ITEM_ENG_NAME as item_abbr,
		ttrd.VALUE as item_result,
		ttrd.ITEM__UNIT as item_unit,
		ttrd.LIS_RESULT as item_hint,
		ttrd.TEST_CODE as record_id
	from th_test_record ttr, vhis_register p, th_test_record_detail ttrd
	where p.CLINIC_CODE = ttr.TREATMENT_CODE
	and ttr.TEST_CODE = ttrd.TEST_CODE
	and ttr.OPER_TIME < '2022-01-01 00:00:00'
	and p.CARD_NO = '874635233656'
	and p.CLINIC_CODE = 'ZY1232132131'
)


-----仅两条结果,打平
( 两条结果,打平
	select 
		ttr.TEST_CODE as record_id,
		'检验记录' as record_type,
		'' as medical_id,
		ttr.TREATMENT_CODE as visit_id,
		ttr.PATIENT_ID as patient_id,
		'' as age,
		p.SEX_CODE as gender,
		p.BIRTHDAY as birth_date,
		'广州医学院附属第一医院' as hospital,
		ttr.OPER_TIME as record_time,
		p.dept_name as dept,
		'' as apply_dept,
		ttr.ORDER_NAME as exam_name,
		'' as exam_method,
		ttr.SAMPLETYPE as sample_category,
		'' as sample_status,
		ttr.SAMPLE_ID as sample_id,
		'' as sample_time,
		'' as receive_time,
		ttr.TESET_DATE as exam_time,
		'' as report_time,
		ttrd.ITEM_NAME as item_name,
		ttrd.ITEM_ENG_NAME as item_abbr,
		ttrd.VALUE as item_result,
		ttrd.ITEM__UNIT as item_unit,
		ttrd.LIS_RESULT as item_hint,
		ttrd.TEST_CODE as record_id
	from th_test_record ttr, vhis_register p, th_test_record_detail ttrd
	where p.CLINIC_CODE = ttr.TREATMENT_CODE
	and ttr.TEST_CODE = ttrd.TEST_CODE
	and ttr.OPER_TIME > '2022-01-01 00:00:00'
	and p.CARD_NO = '874635233656'
	and p.CLINIC_CODE = 'ZY1232132131'
)


-----聚合
(
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
		and ttr.OPER_TIME > '2022-01-01 00:00:00'
		and p.CARD_NO = '874635233656'
		and p.CLINIC_CODE = 'ZY1232132131'
	)
	select 
		t2.record_id,
		t2.record_type,
		t2.medical_id,
		t2.visit_id,
		t2.patient_id,
		t2.age,
		t2.gender,
		t2.birth_date,
		t2.record_time,
		t2.admission_time,
		t2.hospital,
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
)

-------------------------------

select 
	ttr.TEST_CODE as record_id,
	'检验记录' as record_type,
	'da324141' as medical_id,
	ttr.TREATMENT_CODE as visit_id,
	ttr.PATIENT_ID as patient_id,
	'13岁' as age,
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
	
------------------------------

( json样例
	{
		"action": "save_emr",
		"type":[],
		"params": [],
		"emr_contents": [
			{"record_id":"sd2313213123213",
			"record_type":"检验记录",
			"medical_id":"da324141",
			"visit_id":"ZY1232132131",
			"patient_info":{
				"id":"2313123213",
				"age":"13岁",
				"gender":"1",
				"birth_date":"2008-11-1"
			},
			"record_time":"2021-10-23 11:11:11",
			"admission_time":"2021-10-23 08-23-25",
			"hospital":"广医附一",
			"dept":"心血管内科",
			"apply_dept":"心血管内科",
			"exam_name":"血常规",
			"exam_method":"血液标本",
			"sample_category":"血液",
			"sample_status":"合格",
			"sample_id":"23213",
			"sample_time":"2021-10-23 11:05:09",
			"receive_time":"2021-10-23 11:10:09",
			"exam_time":"2021-10-23 11:12:09",
			"report_time":"2021-10-23 11:15:09",
			"result_info":[{
				"item_name":"血红蛋白",
				"item_abbr":"Hb",
				"item_result":"153",
				"item_unit":"g/L",
				"item_hint":"高"
			},{
				"item_name":"白蛋白",
				"item_abbr":"albumin",
				"item_result":"70",
				"item_unit":"g/dl",
				"item_hint":"高"
			}]}
		]
	}
)

( json结构
@JsonProperty("action")
    String action;

    @JsonProperty("type")
    List<String> type;

    @JsonProperty("params")
    List<Map<String, Object>> params;

    @JsonProperty("emr_contents")
    List<JSONObject> emrContents;
	
	
	"params": [{
        "name":"visit_id",
        "value":"23"
    },{
        "name":"patient_id",
        "value":"123456789"
    }],
)


( sql列
		'save_emr' as action,
		'auxiliary_exam_recommend;clinical_pathway_recommend' as type,
		23 as param_visit_id,
		123456789 as param_patient_id,
		t2.record_id,
		t2.record_type,
		t2.medical_id,
		t2.visit_id,
		t2.patient_id,
		t2.age,
		t2.gender,
		t2.birth_date,
		t2.record_time,
		t2.admission_time,
		t2.hospital,
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
)
		
		