package com.cylee.smarthome.model;

import com.cylee.smarthome.util.GsonBuilderFactory;

/**
 * Created by cylee on 16/12/20.
 */
public class ClientAddress {
    public String appid;
    public int vcl;
    public String vName;
    public String address;

    public static ClientAddress fromJson(String json) {
        return GsonBuilderFactory.createBuilder().fromJson(json, ClientAddress.class);
    }
}
