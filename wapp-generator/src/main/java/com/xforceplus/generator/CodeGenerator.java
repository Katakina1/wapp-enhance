package com.xforceplus.generator;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.SqlServerTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.util.Collections;


/**
 * @version 1.0
 * @author: malong@xforceplus.com
 * @date: 2020/2/12 09:48
 */
public class CodeGenerator {


    public static void main(String[] args) {
        generateCode("com.xforceplus.wapp.repository.entity.BaseEntity"

              ,"t_pur_invoice_main"


                );
//         generateMainCode();
        // generateItemCode();
//        String url="https://hljgswsbs.gov.cn:9015/web-reader/reader/image?_b=3.2.0&_d=023001900111_04347400_20200514_G1I7G3D5&_i=0&_v=0&_p=192";
//        System.out.println(Base64.getEncoder().encodeToString(url.getBytes()));
    }


    private static void generateCode(String superEntityClass,String... tablesName) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        if (projectPath.contains("wapp-generator")){
            projectPath=projectPath.substring(0,projectPath.indexOf(File.separator+"wapp-generator"));
        }
        gc.setOutputDir(projectPath + "/wapp-domain/src/main/java/");
        gc.setAuthor("malong@xforceplus.com");
        gc.setOpen(false);
        gc.setEntityName("%sEntity");
        gc.setMapperName("%sDao");
        gc.setDateType(DateType.ONLY_DATE);
        gc.setIdType(IdType.ASSIGN_ID);
        gc.setFileOverride(true);
        gc.setBaseResultMap(true);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:sqlserver://10.100.20.5:1433;DatabaseName=testdb01");
        dsc.setUsername("testuser01");
        dsc.setPassword("aGSsdsJFWd_w_1");
        dsc.setDriverName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dsc.setTypeConvert(new SqlServerTypeConvert() {
            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                if ( fieldType.toLowerCase().contains( "decimal" ) ) {
                    return DbColumnType.BIG_DECIMAL;
                }
                return super.processTypeConvert(globalConfig, fieldType);
            }
        });

        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.xforceplus.wapp.repository");
        pc.setEntity("entity");
        pc.setMapper("dao");
        mpg.setPackageInfo(pc);


        // 自定义配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        templateConfig.setController(null);
        templateConfig.setService(null);
        templateConfig.setServiceImpl(null);
        templateConfig.setEntity("/template/entity.java");
//        templateConfig.setEntity(entityPath);
        templateConfig.setMapper("/template/mapper.java");
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setSkipView(true);
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setEntityBuilderModel(false);
        strategy.setRestControllerStyle(true);
//            strategy.setInclude("invoice_seller_main", "invoice_seller_item", "invoice_purchaser_main", "invoice_purchaser_item");
        strategy.setInclude(tablesName);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setEntityColumnConstant(true);
        // strategy.setSuperEntityClass(superClassName);
        strategy.setSuperEntityClass(superEntityClass);
//        strategy.setSuperMapperClass("com.xforceplus.receipt.repository.AbstractBillDao");
        strategy.setTableFillList(Collections.singletonList(new TableFill("updateTime", FieldFill.UPDATE)));
//        strategy.setSuperEntityColumns("business_extend", "invoice_extend", "channel_source",
//                "synchronize_time", "hash_value", "create_time",
//                "create_user_id", "create_user_name", "update_time",
//                "update_user_id", "update_user_name", "invoice_no", "generate_channel", "invoice_code", "id"
//        );
        strategy.setEnableSqlFilter(false);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
