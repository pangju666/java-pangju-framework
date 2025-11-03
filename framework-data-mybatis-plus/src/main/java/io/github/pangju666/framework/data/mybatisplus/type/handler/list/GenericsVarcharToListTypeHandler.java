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

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.core.convert.converter.Converter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 泛型VARCHAR转List类型处理器
 * <p>
 * 用于处理数据库VARCHAR类型与Java List类型之间的转换。
 * 支持自定义分隔符和元素转换器，可以将以分隔符分隔的字符串转换为指定类型的List，反之亦然。
 * </p>
 *
 * @param <T> List中元素的类型
 * @author pangju666
 * @since 1.0.0
 */
public abstract class GenericsVarcharToListTypeHandler<T> extends BaseTypeHandler<List<T>> {
	/**
	 * 字符串到目标类型的转换器
	 *
	 * @since 1.0.0
	 */
	private final Converter<String, T> converter;
	/**
	 * 列表元素之间的分隔符
	 *
	 * @since 1.0.0
	 */
	private final String delimiter;

	/**
	 * 构造函数
	 * <p>
	 * 使用默认分隔符","初始化类型处理器
	 * </p>
	 *
	 * @param converter 字符串到目标类型的转换器
	 * @since 1.0.0
	 */
	public GenericsVarcharToListTypeHandler(Converter<String, T> converter) {
		this(",", converter);
	}

	/**
	 * 构造函数
	 * <p>
	 * 使用指定分隔符初始化类型处理器
	 * </p>
	 *
	 * @param delimiter 分隔符
	 * @param converter 字符串到目标类型的转换器
	 * @since 1.0.0
	 */
	public GenericsVarcharToListTypeHandler(String delimiter, Converter<String, T> converter) {
		this.delimiter = delimiter;
		this.converter = converter;
	}

	/**
	 * 设置非空参数
	 * <p>
	 * 将List转换为以分隔符分隔的字符串，并设置到PreparedStatement中。
	 * 对于List中的元素值的处理规则如下：
	 * <ul>
	 *     <li>null元素：转换为空字符串("")</li>
	 *     <li>非null元素：调用其toString()方法获取字符串表示</li>
	 * </ul>
	 * 例如，对于列表[1, null, 3]，使用逗号作为分隔符，
	 * 最终生成的字符串为"1,,3"
	 * </p>
	 *
	 * @param ps PreparedStatement对象
	 * @param i 参数位置
	 * @param parameter List参数，整个List不能为null，但其中的元素可以为null
	 * @param jdbcType JDBC类型
	 * @throws SQLException 如果设置参数时发生错误
	 * @since 1.0.0
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
		List<String> values = parameter.stream()
			.map(element -> Objects.isNull(element) ? StringUtils.EMPTY : element.toString())
			.toList();
		ps.setString(i, String.join(delimiter, values));
	}

	/**
	 * 获取可为空的结果（通过列名）
	 * <p>
	 * 从ResultSet中获取指定列名的值，并转换为List
	 * </p>
	 *
	 * @param rs 结果集
	 * @param columnName 列名
	 * @return 转换后的List
	 * @throws SQLException 如果获取结果时发生错误
	 */
	@Override
	public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return getListResult(rs.getString(columnName));
	}

	/**
	 * 获取可为空的结果（通过列索引）
	 * <p>
	 * 从ResultSet中获取指定列索引的值，并转换为List
	 * </p>
	 *
	 * @param rs 结果集
	 * @param columnIndex 列索引
	 * @return 转换后的List
	 * @throws SQLException 如果获取结果时发生错误
	 */
	@Override
	public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return getListResult(rs.getString(columnIndex));
	}

	/**
	 * 获取可为空的结果（从存储过程）
	 * <p>
	 * 从CallableStatement中获取指定列索引的值，并转换为List
	 * </p>
	 *
	 * @param cs CallableStatement对象
	 * @param columnIndex 列索引
	 * @return 转换后的List
	 * @throws SQLException 如果获取结果时发生错误
	 */
	@Override
	public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getListResult(cs.getString(columnIndex));
	}

	/**
	 * 将字符串转换为List
	 * <p>
	 * 将以分隔符分隔的字符串转换为指定类型的List。
	 * 如果输入为空或空白字符串，则返回空List
	 * </p>
	 *
	 * @param result 要转换的字符串
	 * @return 转换后的List
	 * @since 1.0.0
	 */
	protected List<T> getListResult(String result) {
		if (StringUtils.isBlank(result)) {
			return Collections.emptyList();
		}
		return Arrays.stream(result.split(delimiter))
			.map(converter::convert)
			.toList();
	}
}