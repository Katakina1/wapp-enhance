package com.xforceplus.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class TableIdPlugin extends PluginAdapter {
    public TableIdPlugin() {
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        for(IntrospectedColumn column:introspectedTable.getPrimaryKeyColumns()){
            column.setFullyQualifiedJavaType(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.TableId"));
        }
        return true;
    }
}
