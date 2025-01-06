package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SnowflakeIdValidator implements ConstraintValidator<SnowflakeId, Long> {
	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		// 转换为二进制字符串
		String binaryString = Long.toBinaryString(value);
		// 检查长度是否为64位
		if (binaryString.length() != 64) {
			return false;
		}
		// 检查最高位是否为0（因为雪花算法的最高位为0）
		return binaryString.charAt(0) == '0';
	}
}
