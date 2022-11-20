package com.renmin.renminclouddisk.service.impl;

import com.renmin.renminclouddisk.dao.UserDao;
import com.renmin.renminclouddisk.pojo.User;
import com.renmin.renminclouddisk.service.UserService;
import com.renmin.renminclouddisk.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private PropertyUtil propertyUtil;
    @Resource
    private LogUtil logUtil;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public boolean checkUname(String uname) {
        return userDao.findByUname(uname) == null;
    }

    @Override
    public boolean register(User user) {
        user.setTotal(propertyUtil.getUserMaxSize());
        user.setRemain(propertyUtil.getUserMaxSize());
        user.setRole(1);
        boolean flag = userDao.insert(user) != 0;
        if (!flag) {
            return false;
        }
        return new File(propertyUtil.getBaseDir() + user.getUname()).mkdir();
    }

    @Override
    public boolean login(User user) {
        boolean flag = userDao.findOneByUserNameAndPasswd(user.getUname(), user.getPasswd()) != null;
        if (!flag) {
            return false;
        }
        propertyUtil.setUserDir(propertyUtil.getBaseDir() + user.getUname() + "/");
        return true;
    }

    @Override
    public User findUser(User user) {
        return userDao.findOneByUserNameAndPasswd(user.getUname(), user.getPasswd());
    }

    @Override
    public User findVolume(int uid) {
        User user = userDao.findByUid(uid);
        user.setUname("");
        user.setPasswd("");
        user.setGender(0);
        user.setBirthday("");
        user.setCity("");
        return user;
    }

    @Override
    public User findInfo(int uid) {
        return userDao.findByUid(uid);
    }

    @Override
    public boolean changeUserRemain(int uid, int mode, double fileSize) {
        fileSize = fileSize / 1024 / 1024 / 1024;
        BigDecimal bigDecimal = new BigDecimal(fileSize);
        fileSize = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        User user = userDao.findByUid(uid);
        String logMsg = "减少容量";
        if (mode == 1) {
            user.setRemain(user.getRemain() - fileSize);
        } else {
            user.setRemain(user.getRemain() + fileSize);
            logMsg = "增加容量";
        }
        if (!userDao.update(user)) {
            return false;
        }
        logUtil.outputLog(user.getUname(), logMsg, fileSize + "G", logger, 0);
        return true;
    }

    @Override
    public boolean changePasswd(int uid, String passwd) {
        User user = userDao.findByUid(uid);
        user.setPasswd(passwd);
        return userDao.update(user);
    }
}
