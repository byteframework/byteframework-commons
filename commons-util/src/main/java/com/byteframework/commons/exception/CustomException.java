package com.byteframework.commons.exception;

/**
 * 自定义异常
 */
public class CustomException extends Exception{
    private static final long serialVersionUID = 1L;

    //异常编码
    private String code;

    private Object[] args;

    public CustomException() {}
    public CustomException(String code) {
        super(code);
        this.code = code;
    }

    public CustomException(String code, Object ... args) {
        this.code = code;
        this.args = args;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }
}
