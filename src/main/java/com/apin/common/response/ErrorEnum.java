package com.apin.common.response;

/**
 * Created by Administrator on 2016/8/14.
 */
public enum ErrorEnum {

    //common error通用错误
    ERROR_SUCCESS("00000000","操作成功"),
    ERROR_INTERNAL_SERVER_ERROR("00000005","服务器内部错误"),
    ERROR_PARAM_ISBLANK("00000002","存在为空的参数"),
    ERROR_FAILED_VERIFIED("000000004","验证失败"),
    ERROR_FAILED_LOGIN("00000001","未登录"),
    ;

    ErrorEnum(String errorCode,String errorMsg){
        this.errorCode=errorCode;
        this.errorMsg=errorMsg;
    }

    private String errorCode;
    private String errorMsg;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsgWithParam(String param){
        return errorMsg+":["+param+"]";
    }
}
