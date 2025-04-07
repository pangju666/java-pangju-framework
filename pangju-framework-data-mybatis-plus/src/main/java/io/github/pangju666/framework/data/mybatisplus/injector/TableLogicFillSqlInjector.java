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
