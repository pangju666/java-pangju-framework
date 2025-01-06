package io.github.pangju666.framework.data.mybatisplus.validator;

import io.github.pangju666.commons.validation.utils.ConstraintValidatorUtils;
import io.github.pangju666.framework.data.mybatisplus.annotation.validation.SnowflakeIds;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class SnowflakeIdsValidator implements ConstraintValidator<SnowflakeIds, Collection<Long>> {
	private boolean allMatch;
	private boolean notEmpty;

	@Override
	public void initialize(SnowflakeIds constraintAnnotation) {
		this.allMatch = constraintAnnotation.allMatch();
		this.notEmpty = constraintAnnotation.notEmpty();
	}

	@Override
	public boolean isValid(Collection<Long> value, ConstraintValidatorContext context) {
		return ConstraintValidatorUtils.validate(value, allMatch, notEmpty,
			id -> {
				// 转换为二进制字符串
				String binaryString = Long.toBinaryString(id);
				// 检查长度是否为64位
				if (binaryString.length() != 64) {
					return false;
				}
				// 检查最高位是否为0（因为雪花算法的最高位为0）
				return binaryString.charAt(0) == '0';
			});
	}
}
