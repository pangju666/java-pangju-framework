package io.github.pangju666.framework.core.exception.remote.model;

import org.apache.commons.lang3.StringUtils;

public record RemoteServiceError(String service,
								 String api,
								 String path,
								 String message,
								 Integer code,
								 Integer httpStatus) {
	public String getRemoteServiceInfo() {
		StringBuilder builder = new StringBuilder()
			.append("服务：")
			.append(this.service)
			.append(" 接口：")
			.append(this.api);
		if (StringUtils.isNotBlank(this.path)) {
			builder.append(" 路径：").append(this.path);
		}
		return builder.toString();
	}
}
