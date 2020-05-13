package com.byteframework.commons.domain;

public class ResponseEntity {

/*    //成功
    public static final String STATUS_SUCCESS = "000";
    //失败
    public static final String STATUS_FAILURE = "001";
    //session过期
    public static final String STATUS_SESSION_TIMEOUT = "002";*/
    //记录数key
    public static final String TOTAL_COUNT_KEY = "count";
    //记录列表key
    public static final String LIST_KEY = "list";

    private String code;

    private String message = "";

    Object data = null;

    public ResponseEntity() {

    }

    public ResponseEntity(String status) {
        this.code = status;
    }

    public ResponseEntity(String status, Object body) {
        this.code = status;
        this.data = body;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
