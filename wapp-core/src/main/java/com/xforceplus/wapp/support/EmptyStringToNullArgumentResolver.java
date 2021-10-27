package com.xforceplus.wapp.support;

import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-27 11:51
 **/
public class EmptyStringToNullArgumentResolver extends ServletModelAttributeMethodProcessor {
    /**
     * Class constructor.
     *
     * @param annotationNotRequired if "true", non-simple method arguments and
     *                              return values are considered model attributes with or without a
     *                              {@code @ModelAttribute} annotation
     */
    public EmptyStringToNullArgumentResolver(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }



}
