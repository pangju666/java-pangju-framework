package io.github.pangju666.framework.data.mybatisplus.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import io.github.pangju666.framework.data.mybatisplus.injector.methods.Delete;
import io.github.pangju666.framework.data.mybatisplus.injector.methods.DeleteBatchByIds;
import io.github.pangju666.framework.data.mybatisplus.injector.methods.DeleteById;
import org.apache.ibatis.session.Configuration;

import java.util.LinkedList;
import java.util.List;

public class DeleteInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        // 重写的注入要先于父类的注入，否则会导致注入失败
        List<AbstractMethod> list = new LinkedList<>();
        list.add(new Delete());
        list.add(new DeleteBatchByIds());
        list.add(new DeleteById());
        list.addAll(super.getMethodList(configuration, mapperClass, tableInfo));
        return list;
    }
}
