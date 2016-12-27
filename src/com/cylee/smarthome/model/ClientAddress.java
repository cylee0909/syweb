package com.cylee.smarthome.model;

import com.cylee.smarthome.util.GsonBuilderFactory;

/**
 * Created by cylee on 16/12/20.
 */
public class ClientAddress {
    public String appid;
    public int vcl;
    public String vName;
    public String loginPassd;
    public String loginName;
    public String address;

    public static ClientAddress fromJson(String json) {
        return GsonBuilderFactory.createBuilder().fromJson(json, ClientAddress.class);
    }

    public boolean matchLogin(String name, String passd) {
        if (name != null && passd != null) {
            return name.equals(loginName) && passd.equals(loginPassd);
        }
        return false;
    }
}
