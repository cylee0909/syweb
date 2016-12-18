package com.cylee.smarthome.model;

import com.cylee.smarthome.util.GsonBuilderFactory;

/**
 * Created by cylee on 16/12/18.
 */
public class BaseModel {
    /** 请求参数无效 */
    public static final int CODE_PARAM_INVALID = -1;
    private int errno;
    private String desc = "";

    private Object data;

    public BaseModel(int errno, String desc, Object data) {
        this.errno = errno;
        this.desc = desc;
        this.data = data;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static BaseModel buildModel(int errno, String desc, Object data) {
        return new BaseModel(errno, desc, data);
    }

    public static BaseModel buildSuccess(Object data) {
        return new BaseModel(0, "ok", data);
    }

    public static BaseModel buildInvalidInput() {
        return new BaseModel(CODE_PARAM_INVALID, "invalid input", null);
    }

    public String toJson() {
        return GsonBuilderFactory.createBuilder().toJson(this);
    }
}
