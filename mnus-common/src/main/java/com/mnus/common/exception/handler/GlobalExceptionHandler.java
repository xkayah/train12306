package com.mnus.common.exception.handler;

import com.mnus.common.enums.BaseErrorCodeEnum;
import com.mnus.common.exception.BizException;
import com.mnus.common.exception.ResponseCode;
import com.mnus.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author: <a href="https://github.com/xkayah">xkayah</a>
 * @date: 2024/3/9 9:54:00
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Throwable.class)
    public CommonResp<?> runtimeExceptionHandler(Throwable e) {
        LOG.error("引发的异常的堆栈信息：", e);
        return CommonResp.failed(
                BaseErrorCodeEnum.INTERNAL_SERVER_ERROR.getCode(), BaseErrorCodeEnum.INTERNAL_SERVER_ERROR.getMsg());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BizException.class)
    public CommonResp<?> bizExceptionHandler(BizException e) {
        LOG.error("业务异常：{}", e.toString());
        return CommonResp.failed(
                e.getResponseBody().getCode(), e.getResponseBody().getMsg());
    }

    /**
     * 处理校验异常
     */
    @ExceptionHandler(BindException.class)
    public CommonResp<?> bindExceptionHandler(BindException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        LOG.error("校验异常：{}", message);
        return CommonResp.failed(ResponseCode.BAD_REQUEST, message);
    }

}
