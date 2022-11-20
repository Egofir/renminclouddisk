package com.renmin.renminclouddisk.util;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class LogUtil {
    public void outputLog(String uname, String motion, String thing, Logger logger, int level) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(uname);
        stringBuilder.append("]");
        stringBuilder.append(motion);
        stringBuilder.append(" ");
        stringBuilder.append(thing);
        switch (level) {
            case 0: {
                logger.info(stringBuilder.toString());
                break;
            }
            case 1: {
                logger.error(stringBuilder.toString());
                break;
            }
        }
    }
}
