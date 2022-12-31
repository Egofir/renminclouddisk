package com.hape.netdisk.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TimeUtil {
    private final String timeFormat = "yyyy-MM-dd HH:mm:ss";
    /**
     * 获取现在时间
     * @return
     */
    public String getCurrentTime(){
        return new SimpleDateFormat(timeFormat).format(new Date());
    }

    /**
     * 根据指定毫秒值获取时间
     * @param m 毫秒值
     * @return
     */
    public String getTimeByMilliSecond(long m){
        return new SimpleDateFormat(timeFormat).format(new Date(m));
    }
}
