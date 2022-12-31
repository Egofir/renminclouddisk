package com.hape.netdisk.util;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class LogUtil {
    /**
     * 输出日志（谁 做了 什么事）
     * @param uname 用户名
     * @param motion 动作
     * @param thing 事情
     * @param logger 日志对象
     * @param level 输出级别 0为 info, 1为 error
     */
    public void outPutLog(String uname, String motion, String thing, Logger logger, int level){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(uname);
        sb.append("] ");
        sb.append(motion);
        sb.append(" ");
        sb.append(thing);
        switch (level){
            case 0:logger.info(sb.toString());break;
            case 1:logger.error(sb.toString());break;
        }
    }
}
