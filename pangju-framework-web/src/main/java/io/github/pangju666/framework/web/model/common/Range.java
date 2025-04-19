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

/**
 * HTTP Range 请求范围类
 * <p>
 * 用于表示 HTTP 范围请求中的数据范围，支持 HTTP 协议中 Range 头部字段所定义的字节范围。
 * 该类主要用于处理分块传输、断点续传等场景中的数据范围表示。
 * </p>
 *
 * <p>
 * 在 HTTP Range 请求中，客户端可以请求资源的部分内容，通过指定起始位置和结束位置来定义所需的字节范围。
 * 例如：
 * <ul>
 *     <li>Range: bytes=0-499 表示请求资源的前 500 个字节</li>
 *     <li>Range: bytes=500-999 表示请求资源的第 500-999 字节</li>
 *     <li>Range: bytes=-500 表示请求资源的最后 500 个字节</li>
 *     <li>Range: bytes=500- 表示请求从第 500 字节开始到文件结束的所有内容</li>
 * </ul>
 * </p>
 *
 * <p>
 * 该类封装了范围请求的起始位置、结束位置、长度、总大小等信息，并提供了判断是否为完整资源请求的标记。
 * </p>
 *
 * @author pangju666
 * @see io.github.pangju666.framework.web.utils.ResponseUtils 包含处理范围请求的工具方法
 * @since 1.0.0
 */

public final class Range {
	/**
	 * 范围起始位置
	 * <p>
	 * 表示请求范围的起始字节索引（包含），从 0 开始计数。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private long start;
	/**
	 * 范围结束位置
	 * <p>
	 * 表示请求范围的结束字节索引（包含），从 0 开始计数。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private long end;
	/**
	 * 范围长度
	 * <p>
	 * 表示请求范围的字节长度，计算公式为：end - start + 1。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private long length;
	/**
	 * 资源总大小
	 * <p>
	 * 表示目标资源的总字节大小，用于生成 Content-Range 响应头。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private long total;
	/**
	 * 是否为完整资源请求
	 * <p>
	 * 当该值为 true 时，表示请求的是完整资源而非部分范围。
	 * 在这种情况下，通常会直接返回完整资源而非使用 206 Partial Content 状态码。
	 * </p>
	 *
	 * @since 1.0.0
	 */
	private boolean full;

	/**
	 * 创建一个新的范围实例
	 * <p>
	 * 根据指定的起始位置、结束位置和资源总大小创建一个范围对象。
	 * 范围长度将根据起始和结束位置自动计算为 end - start + 1。
	 * 默认情况下，范围不被视为完整资源请求（full = false）。
	 * </p>
	 *
	 * @param start 范围的起始字节索引（包含）
	 * @param end   范围的结束字节索引（包含）
	 * @param total 资源的总字节大小
	 * @since 1.0.0
	 */
	public Range(long start, long end, long total) {
		this.start = start;
		this.end = end;
		this.length = end - start + 1;
		this.total = total;
		this.full = false;
	}

	/**
	 * 获取范围的起始位置
	 * <p>
	 * 返回该范围的起始字节索引（包含）。
	 * </p>
	 *
	 * @return 范围的起始字节索引
	 * @since 1.0.0
	 */
	public long getStart() {
		return start;
	}

	/**
	 * 设置范围的起始位置
	 * <p>
	 * 设置该范围的起始字节索引（包含）。
	 * 注意：更改起始位置后，可能需要同时更新长度值以保持一致性。
	 * </p>
	 *
	 * @param start 新的起始字节索引
	 * @since 1.0.0
	 */
	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * 获取范围的结束位置
	 * <p>
	 * 返回该范围的结束字节索引（包含）。
	 * </p>
	 *
	 * @return 范围的结束字节索引
	 * @since 1.0.0
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * 设置范围的结束位置
	 * <p>
	 * 设置该范围的结束字节索引（包含）。
	 * 注意：更改结束位置后，可能需要同时更新长度值以保持一致性。
	 * </p>
	 *
	 * @param end 新的结束字节索引
	 * @since 1.0.0
	 */
	public void setEnd(long end) {
		this.end = end;
	}

	/**
	 * 获取范围的长度
	 * <p>
	 * 返回该范围所覆盖的字节数量。
	 * </p>
	 *
	 * @return 范围的字节长度
	 * @since 1.0.0
	 */
	public long getLength() {
		return length;
	}

	/**
	 * 设置范围的长度
	 * <p>
	 * 设置该范围所覆盖的字节数量。
	 * 注意：更改长度后，可能需要同时更新结束位置以保持一致性。
	 * </p>
	 *
	 * @param length 新的字节长度
	 * @since 1.0.0
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * 获取资源总大小
	 * <p>
	 * 返回目标资源的总字节大小。
	 * </p>
	 *
	 * @return 资源的总字节大小
	 * @since 1.0.0
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 设置资源总大小
	 * <p>
	 * 设置目标资源的总字节大小。
	 * </p>
	 *
	 * @param total 新的资源总字节大小
	 * @since 1.0.0
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	/**
	 * 检查是否为完整资源请求
	 * <p>
	 * 如果该范围表示完整资源而非部分范围，则返回 true。
	 * </p>
	 *
	 * @return 如果范围涵盖整个资源则返回 true，否则返回 false
	 * @since 1.0.0
	 */
	public boolean isFull() {
		return full;
	}

	/**
	 * 设置是否为完整资源请求
	 * <p>
	 * 设置该范围是否表示完整资源而非部分范围。
	 * 当设置为 true 时，通常意味着应当返回 200 OK 而非 206 Partial Content 状态码。
	 * </p>
	 *
	 * @param full 如果范围涵盖整个资源则设为 true，否则设为 false
	 * @since 1.0.0
	 */
	public void setFull(boolean full) {
		this.full = full;
	}
}
