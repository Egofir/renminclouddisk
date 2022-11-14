package com.renmin.renminclouddisk.service.impl;

import com.renmin.renminclouddisk.exception.RenMinCloudDiskException;
import com.renmin.renminclouddisk.exception.RenMinCloudDiskExceptionEnum;
import com.renmin.renminclouddisk.model.dao.SensitiveWordMapper;
import com.renmin.renminclouddisk.model.dao.UserMapper;
import com.renmin.renminclouddisk.model.pojo.SensitiveWord;
import com.renmin.renminclouddisk.model.pojo.User;
import com.renmin.renminclouddisk.service.UserService;
import com.renmin.renminclouddisk.util.SensitiveWordUtil;
import com.renmin.renminclouddisk.util.SimpleRedisLock;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {
    @Resource
    private SensitiveWordMapper sensitiveWordMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void setSensitiveWord() {
        List<String> list = new ArrayList<>();
        List<SensitiveWord> sensitiveWordList = sensitiveWordMapper.querySensitiveWordList();
        for (int i = 0; i < sensitiveWordList.size(); i++) {
            SensitiveWord sensitiveWord = sensitiveWordList.get(i);
            list.add(sensitiveWord.getValue());
        }
        SensitiveWordUtil.initMap(list);
    }

    @Override
    public void register(String username, String password) throws RenMinCloudDiskException {
        SimpleRedisLock lock = new SimpleRedisLock("register:" + username, stringRedisTemplate);
        boolean isLock = lock.tryLock(5);
        if (!isLock) {
            throw new RenMinCloudDiskException(RenMinCloudDiskExceptionEnum.NAME_EXISTED);
        }
        try {
            UserService proxy = (UserService) AopContext.currentProxy();
            proxy.
        }
    }

    @Transactional
    public synchronized void extracted(String username, String password) throws RenMinCloudDiskException {
        User result = userMapper.queryUserByUsername(username);
        if (result != null) {
            throw new RenMinCloudDiskException(RenMinCloudDiskExceptionEnum.NAME_EXISTED);
        }
    }
}
