package com.xforceplus.wapp.advice;

import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description 异常统一处理器
 * @create 2021-10-09 15:04
 **/

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(EnhanceRuntimeException.class)
    public ResponseEntity handleEnhanceRuntimeException(EnhanceRuntimeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.fail(e.getMessage(),e.getCode()));
    }

}
