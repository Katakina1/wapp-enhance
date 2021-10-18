package com.xforceplus.wapp.annotation;

import com.xforceplus.wapp.common.Const;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
@RequestMapping(EnhanceApi.BASE_PATH)
public @interface EnhanceApi {
    /**
     * API 基础路径
     */
    String BASE_PATH = Const.BASE_PATH;
}
