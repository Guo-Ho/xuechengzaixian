package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * 和前端约定返回的异常信息模型
 * 和前端约定返回的异常信息都为json，json的属性为errMessage
 */
public class RestErrorResponse implements Serializable {

    private String errMessage; //给前端的错误信息

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
