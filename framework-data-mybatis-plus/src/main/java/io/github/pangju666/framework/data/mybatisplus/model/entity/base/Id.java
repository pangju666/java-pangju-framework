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

package io.github.pangju666.framework.data.mybatisplus.model.entity.base;

/**
 * ID接口
 * <p>
 * 定义实体类ID的getter和setter方法。
 * </p>
 *
 * @param <ID> ID的类型参数
 * @author pangju666
 * @since 1.0.0
 */
public interface Id<ID> {
	ID getId();

	void setId(ID id);
}
