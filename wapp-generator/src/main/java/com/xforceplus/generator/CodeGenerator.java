package com.xforceplus.generator;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.converts.SqlServerTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;


/**
 * @version 1.0
 * @author: malong@xforceplus.com
 * @date: 2020/2/12 09:48
 */
public class CodeGenerator {


    public static void main(String[] args) {
        generateCode("com.xforceplus.wapp.repository.entity.BaseEntity",
//               "t_dx_record_invoice",
//                "t_dx_record_invoice_detail",
//                "t_xf_bill_deduct",
//                "t_xf_elec_upload_record_detail",
////                 by kenny start
//                 "t_xf_bill_job",
//                 "t_xf_origin_epd_bill",
//                 "t_xf_origin_epd_log_item",
//                "t_xf_origin_agreement_bill",
//                 "t_xf_origin_claim_bill",
//                 "t_xf_origin_claim_item_hyper",
//                 "t_xf_origin_claim_item_sams",
//                "t_dx_excel_exportlog",
//                "t_dx_messagecontrol",
//                "t_xf_blue_relation",
//                "t_xf_bill_deduct_invoice",
//                "t_xf_bill_deduct_item",
//                "t_xf_bill_deduct_item_ref",
//                "t_xf_bill_job",
//                "t_xf_black_white_company",
//                "t_xf_blue_relation",
//                "t_xf_company_info",
//                "t_xf_default_setting",
//                "t_xf_elec_upload_record",
//                "t_xf_elec_upload_record_detail",
                "t_dx_Invoice_details"
//                "t_xf_invoice",
//                "t_xf_invoice_file",
//                "t_xf_invoice_item",
//                "t_xf_match_relation",
//                "t_xf_none_business_upload_detail",
//                "t_xf_origin_agreement_bill",
//                "t_xf_origin_claim_bill",
//                "t_xf_origin_claim_item_hyper",
//                "t_xf_origin_claim_item_sams",
//                "t_xf_origin_epd_bill",
//                "t_xf_origin_epd_log_item",
//                "t_xf_overdue",
//                "t_xf_pre_invoice",
//                "t_xf_pre_invoice_item",
//                "t_xf_red_notification",
//                "t_xf_red_notification_detail",
//                "t_xf_red_notification_log",
//                "t_xf_settlement",
//                "t_xf_settlement_item",
//                "t_xf_tax_code","t_dx_match",
//                "t_ac_org","t_dx_question_paper",
//                "t_dx_record_invoice_detail","t_dx_invoice","t_xf_bill_deduct_item","t_xf_origin_agreement_item"
//                "t_xf_bill_deduct_invoice"
                );
//         generateMainCode();
        // generateItemCode();
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
