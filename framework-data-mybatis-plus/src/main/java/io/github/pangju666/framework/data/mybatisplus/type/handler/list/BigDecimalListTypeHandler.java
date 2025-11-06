/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.data.mybatisplus.type.handler.list;

import io.github.pangju666.framework.data.mybatisplus.type.handler.GenericsListTypeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.util.List;

/**
 * BigDecimal类型的VARCHAR转List类型处理器
 * <p>
 * 用于处理数据库VARCHAR类型与List<BigDecimal>类型之间的转换。
 * 将以分隔符分隔的字符串转换为BigDecimal列表，或将BigDecimal列表转换为分隔的字符串。
 * </p>
 *
 * @author pangju666
 * @since 1.0.0
 */
@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public final class BigDecimalListTypeHandler extends GenericsListTypeHandler<BigDecimal> {
	public BigDecimalListTypeHandler() {
		super((value) -> StringUtils.isBlank(value) ? null : new BigDecimal(value));
	}
}
