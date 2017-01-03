package com.cylee.web;

import org.apache.log4j.Logger;

/**
 * Created by cylee on 16/10/22.
 */
public class Log {
    static Logger logger = Logger.getLogger("smarthome");
    public static void d(String msg) {
        logger.debug(msg);
    }
}
