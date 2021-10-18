package com.xforceplus.wapp.advice;

import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description 异常统一处理器
 * @create 2021-10-09 15:04
 **/

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(EnhanceRuntimeException.class)
    public ResponseEntity<R<?>> handleEnhanceRuntimeException(EnhanceRuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.fail(e.getMessage(), e.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<?>> handleEnhanceRuntimeException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<?>> handleBindException(BindException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.fail(e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<?>> handleEnhanceRuntimeException(Exception e) {
        log.error("系统异常。", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(R.fail(e.getMessage()));
    }

}
