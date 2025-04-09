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

package io.github.pangju666.framework.data.mybatisplus.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import io.github.pangju666.framework.data.mybatisplus.injector.methods.Delete;
import io.github.pangju666.framework.data.mybatisplus.injector.methods.DeleteById;
import io.github.pangju666.framework.data.mybatisplus.injector.methods.DeleteByIds;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

public class TableLogicFillSqlInjector extends DefaultSqlInjector {
	@Override
	public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
		List<AbstractMethod> oldMethods = super.getMethodList(configuration, mapperClass, tableInfo);
		// 重写的注入要先于父类的注入，否则会导致注入失败
		List<AbstractMethod> newMethods = new ArrayList<>(oldMethods.size() + 3);
		newMethods.add(new Delete());
		newMethods.add(new DeleteByIds());
		newMethods.add(new DeleteById());
		newMethods.addAll(oldMethods);
		return newMethods;
	}
}
