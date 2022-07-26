package com.devops.project.business.vo;

import java.io.Serializable;

public class QingtingQiaoResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private String code;
    private String msg;
    private T result;

    public QingtingQiaoResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
