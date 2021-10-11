package com.xforceplus.generator;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 项目名称:  file-transfer-service
 * 模块名称:
 * 说明:
 * JDK 版本: JDK1.8
 *
 * @author 作者：chenqiguang
 * 创建日期：2019-07-03
 */
public class ComponentAnnotationPlugin extends PluginAdapter {
    public ComponentAnnotationPlugin() {
    }

    @Override
    public boolean validate(List<String> warnings){
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Component"));
            interfaze.addAnnotation("@Component");
        }
        return true;
    }
}
