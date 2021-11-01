package com.xforceplus.wapp.modules.rednotification.log;

import java.lang.annotation.*;

@Documented // 定义注解的保留策略
@Inherited // 说明子类可以继承父类中的该注解
@Retention(RetentionPolicy.RUNTIME) // 定义注解的保留策略
@Target(ElementType.METHOD) // 定义注解的作用目标
public @interface LogOperate {

    String value() default "";

    LogOperateType type() default LogOperateType.QUERY;

}

