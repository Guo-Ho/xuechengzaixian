package com.xuecheng.base.exception;
/**
 * @description 本项目的自定义异常类型
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(String errMessage){ // 抛出我们定义的异常类型
        throw new XueChengPlusException(errMessage);
    }

    public static void cast(CommonError error){ // 抛出我们定义的异常类型
        throw new XueChengPlusException(error.getErrMessage());
    }
}
