package com.renmin.renminclouddisk.service.impl;

import com.renmin.renminclouddisk.dao.CodeDao;
import com.renmin.renminclouddisk.dao.FileDao;
import com.renmin.renminclouddisk.dao.UserDao;
import com.renmin.renminclouddisk.pojo.Code;
import com.renmin.renminclouddisk.pojo.File;
import com.renmin.renminclouddisk.pojo.User;
import com.renmin.renminclouddisk.service.CodeService;
import com.renmin.renminclouddisk.util.CodeTimeTask;
import com.renmin.renminclouddisk.util.PropertyUtil;
import com.renmin.renminclouddisk.util.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class CodeServiceImpl implements CodeService {
    @Resource
    private Code code;
    @Resource
    private CodeDao codeDao;
    @Resource
    private FileDao fileDao;
    @Resource
    private UserDao userDao;
    @Resource
    private CodeTimeTask codeTimeTask;
    @Resource
    private TimeUtil timeUtil;
    @Resource
    private PropertyUtil propertyUtil;

    public String shareFile(int fid, long time) {
        String activeCode = UUID.randomUUID().toString();
        String endTime = propertyUtil.getForeverTime();
        if (time != 0) {
            endTime = timeUtil.getTimeByMilliSecond(time + System.currentTimeMillis());
        }
        code.setFid(fid);
        code.setActiveCode(activeCode);
        code.setEndTime(endTime);
        int cid = codeDao.insert(code);
        if (cid == 0) {
            return null;
        }
        if (time != 0) {
            codeTimeTask.delCode(cid, time);
        }
        return activeCode;
    }

    public Code findFileByCode(String code) {
        Code c = codeDao.findByCode(code);
        if (c == null) {
            return null;
        }
        File file = fileDao.findByFid(c.getFid());
        User user = userDao.findByUid(file.getUid());
        file.setUser(user);
        c.setFile(file);
        return c;
    }

    public boolean cancelShare(int fid) {
        List<Code> list = codeDao.findByFid(fid);
        if (list == null || list.size() == 0) {
            return true;
        }
        for (Code c : list) {
            if (!codeDao.del(c.getCid())) {
                return false;
            }
        }
        return true;
    }
}
