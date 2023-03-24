package com.alibaba.datax.plugin.writer.httpclinicwriter;

import com.alibaba.datax.common.spi.ErrorCode;

/**
 * Created by haiwei.luo on 14-9-20.
 */
public enum HttpWriterErrorCode implements ErrorCode {

	FAIL_LOGIN("HttpWriter-01", "登录失败,无法与http服务器建立连接."),
	FAIL_GET_OUTPUT_STREAM("HttpWriter-02","获得getOutputStream()异常"),
	FAIL_HTTP_WRITE("HttpWriter-03","http startWrite异常"),
	FAIL_URL_TRANSFORMATION("HttpWriter-04","URL转换异常"),
	FAIL_IO("HttpWriter-05","IO异常"),
	FAIL_COLUMN_NUMBER("HttpWriter—06","数据列数不对"),
	FAIL_GET_PROPERTY("HttpWriter-07","获取数据库属性异常"),
	FAIL_JSON_DEFINE("HttpWriter-08","获取数据库属性异常"),;


	private final String code;
	private final String description;

	private HttpWriterErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		return String.format("Code:[%s], Description:[%s].", this.code,
				this.description);
	}
}
