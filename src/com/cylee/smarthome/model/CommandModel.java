package com.cylee.smarthome.model;

import com.cylee.smarthome.util.GsonBuilderFactory;

/**
 * Created by cylee on 17/3/11.
 */
public class CommandModel {
    public String command;
    public String _params;

    public static String create(String command, String params) {
        CommandModel model = new CommandModel();
        model.command = command;
        model._params = params;
        return GsonBuilderFactory.createBuilder().toJson(model);
    }
}
