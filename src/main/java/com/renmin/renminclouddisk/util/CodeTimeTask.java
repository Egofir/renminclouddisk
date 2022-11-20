package com.renmin.renminclouddisk.util;

import com.renmin.renminclouddisk.dao.CodeDao;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CodeTimeTask {
    @Resource
    private CodeDao codeDao;

    @Async
    public void delCode(int cid, long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        codeDao.del(cid);
    }
}
