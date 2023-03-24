package com.alibaba.datax.plugin.reader.httpreader;

import com.alibaba.datax.common.spi.ErrorCode;

/**
 * Created by haiwei.luo on 14-9-20.
 */
public enum HttpReaderErrorCode implements ErrorCode {

	FAIL_LOGIN("HttpReader-01", "登录失败,无法与http服务器建立连接."),
	FAIL_GET_OUTPUT_STREAM("HttpReader-02","获得getOutputStream()异常"),
	FAIL_HTTP_READ("HttpReader-03","http startRead异常"),;


	private final String code;
	private final String description;

	private HttpReaderErrorCode(String code, String description) {
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
