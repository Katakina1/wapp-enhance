package com.xforceplus.wapp.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
@RequestMapping(EnhanceApiV1.BASE_PATH + "/v1")
public @interface EnhanceApiV1 {
    String BASE_PATH=EnhanceApi.BASE_PATH+"/v1";
    @AliasFor(annotation = RequestMapping.class, value = "value")
    String value();
}
