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

package io.github.pangju666.framework.web.model.common;

import io.github.pangju666.framework.web.utils.HttpServletResponseUtils;
import org.springframework.util.Assert;

/**
 * HTTP范围请求模型
 * <p>
 * 封装HTTP范围请求(Range Requests)的数据模型，用于表示资源的部分内容请求。
 * 主要应用于大文件下载、视频流媒体、断点续传等场景，支持RFC 7233规范定义的字节范围规格。
 * </p>
 *
 * <p>
 * HTTP范围请求允许客户端请求资源的部分内容，常见格式包括：
 * <ul>
 *     <li><code>bytes=0-499</code> - 请求前500字节</li>
 *     <li><code>bytes=500-999</code> - 请求第500-999字节</li>
 *     <li><code>bytes=500-</code> - 请求第500字节到结尾</li>
 * </ul>
 * </p>
 *
 * <p>
 * 该类是不可变类，一旦创建不可修改其内部状态，确保线程安全。
 * 封装了范围的起始位置、结束位置、长度、资源总大小等核心属性，并提供判断是否为完整资源请求的能力。
 * </p>
 *
 * @author pangju666
 * @see HttpServletResponseUtils
 * @since 1.0.0
 */
public final class Range {
	/**
	 * 范围起始位置
	 * <p>
	 * 表示请求范围的起始字节索引（包含此位置），从0开始计数。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final long start;
	/**
	 * 范围结束位置
	 * <p>
	 * 表示请求范围的结束字节索引（包含此位置），从0开始计数。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final long end;
	/**
	 * 范围长度
	 * <p>
	 * 表示请求范围的字节数量，计算公式：end - start + 1。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final long length;
	/**
	 * 资源总大小
	 * <p>
	 * 表示目标资源的总字节数，用于生成Content-Range响应头和判断是否为完整资源请求。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final long total;
	/**
	 * 完整资源标记
	 * <p>
	 * 当请求范围等于资源总大小时为true，表示请求的是完整资源而非部分范围。
	 * 此时服务器通常会返回200 OK状态码而非206 Partial Content。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private final boolean complete;

	/**
	 * 构造范围请求实例
	 * <p>
	 * 根据指定的起始位置、结束位置和资源总大小创建一个范围对象。
	 * 会自动计算范围长度并判断是否为完整资源请求。
	 * </p>
	 *
	 * @param start 范围起始字节索引（包含）
	 * @param end   范围结束字节索引（包含）
	 * @param total 资源总字节数
	 * @throws IllegalArgumentException 当参数不符合要求时抛出，包括：
	 *                                  <ul>
	 *                                      <li>start为负数</li>
	 *                                      <li>end小于start</li>
	 *                                      <li>total不大于0</li>
	 *                                  </ul>
	 * @since 1.0.0
	 */
	public Range(long start, long end, long total) {
		Assert.notNull(start >= 0, "start 必须为非负数");
		Assert.notNull(end >= start, "end 必须大于等于end");
		Assert.isTrue(total > 0, "total 必须大于0");

		this.start = start;
		this.end = end;
		this.total = total;
		this.length = end - start + 1;
		this.complete = this.length == this.total;
	}

	/**
	 * 获取范围起始位置
	 *
	 * @return 范围的起始字节索引（包含）
	 * @since 1.0.0
	 */
	public long getStart() {
		return start;
	}

	/**
	 * 获取范围结束位置
	 *
	 * @return 范围的结束字节索引（包含）
	 * @since 1.0.0
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * 获取范围长度
	 *
	 * @return 范围覆盖的字节数量
	 * @since 1.0.0
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 获取资源总大小
	 *
	 * @return 资源的总字节数
	 * @since 1.0.0
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 创建完整范围对象
	 * <p>
	 * 创建一个表示资源完整内容的范围对象，范围从0开始到资源末尾。
	 * 这是一个便捷方法，用于表示请求或响应中的完整资源，而不是部分内容。
	 * </p>
	 *
	 * @param totalLength 资源的总长度（字节数）
	 * @return 表示完整资源范围的Range对象，起始位置为0，结束位置为totalLength-1
	 * @since 1.0.0
	 */
	public static Range complete(long totalLength) {
		return new Range(0, totalLength - 1, totalLength);
	}

	/**
	 * 判断是否为完整资源请求
	 *
	 * @return 如果请求范围等于资源总大小则返回true，否则返回false
	 * @since 1.0.0
	 */
	public boolean isComplete() {
		return complete;
	}
}
