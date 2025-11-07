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

package io.github.pangju666.framework.data.redis.lang;

/**
 * Redis相关常量
 *
 * @author pangju666
 * @since 1.0.0
 */
public class RedisConstants {
	/**
	 * Redis路径分隔符
	 *
	 * @since 1.0.0
	 */
	public static final String REDIS_PATH_DELIMITER = ":";
	/**
	 * Redis游标匹配模式通配符
	 * <p>
	 * 用于SCAN、SSCAN、HSCAN、ZSCAN等命令的模式匹配：
	 * <ul>
	 *     <li>前缀匹配：prefix* （例如：user*）</li>
	 *     <li>后缀匹配：*suffix （例如：*_score）</li>
	 *     <li>关键字匹配：*keyword* （例如：*user*）</li>
	 * </ul>
	 * </p>
	 *
	 * @since 1.0.0
	 */
	public static final String CURSOR_PATTERN_SYMBOL = "*";

	protected RedisConstants() {
	}
}