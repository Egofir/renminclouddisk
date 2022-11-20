package com.renmin.renminclouddisk.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TimeUtil {
    private final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    public String getCurrentTime() {
        return new SimpleDateFormat(timeFormat).format(new Date());
    }

    public String getTimeByMilliSecond(long m) {
        return new SimpleDateFormat(timeFormat).format(new Date(m));
    }
}
