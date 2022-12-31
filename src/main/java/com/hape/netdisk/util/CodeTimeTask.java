package com.hape.netdisk.util;

import com.hape.netdisk.dao.CodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CodeTimeTask {
    @Autowired
    private CodeDao codeDao;

    @Async
    public void delCode(int cid,long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        codeDao.del(cid);
    }
}
