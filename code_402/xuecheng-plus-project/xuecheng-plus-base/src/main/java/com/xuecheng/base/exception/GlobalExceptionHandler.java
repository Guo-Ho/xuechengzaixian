package com.xuecheng.base.execption;

import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.RestErrorResponse;
import com.xuecheng.base.exception.XueChengPlusException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 全局异常处理器
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
// 对项目的自定义异常进行处理(属于系统自定义异常)
    @ResponseBody // 返回json格式
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //遇到异常信息后，响应给前端的状态码
    public RestErrorResponse customException(XueChengPlusException e) {
        //记录异常日志
        log.error("系统异常{}",e.getErrMessage(),e);
        // 解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
//        log.error("【系统异常】{}",e.getErrMessage(),e);
//        return new RestErrorResponse(e.getErrMessage());

    }


// 针对不是系统自定义的异常,比如说网络断了,或者一个分数的分母为0
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        //记录异常日志
        log.error("系统异常{}",e.getMessage(),e);
        // 解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage()); //异常信息： "执行过程异常，请重试。"
        return restErrorResponse;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult(); //拿到校验框架对应的异常
        // 存储错误信息
        List<String> msgList = new ArrayList<>();
        //将错误信息放在msgList
        bindingResult.getFieldErrors().stream().forEach(item->{
                msgList.add(item.getDefaultMessage());
        }); //解析并将错误信息放到msgList中

        String msg = StringUtils.join(msgList, ","); //将 msgList中的错误信息拼接起来
        log.error("【系统异常】{}",msg);
        return new RestErrorResponse(msg);
    }
}