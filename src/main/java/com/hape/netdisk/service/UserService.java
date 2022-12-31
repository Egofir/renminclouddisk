package com.hape.netdisk.service;

import com.hape.netdisk.dao.UserDao;
import com.hape.netdisk.pojo.User;
import com.hape.netdisk.util.LogUtil;
import com.hape.netdisk.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.math.BigDecimal;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PropertyUtil propertyUtil;
    @Autowired
    private LogUtil logUtil;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * 检查用户名重名
     * @param uname
     * @return
     */
    public boolean checkUname(String uname){
        return userDao.findByUname(uname)==null;
    }

    /**
     * 注册
     * @param user
     * @return
     */
    public boolean register(User user){
        user.setTotal(propertyUtil.getUserMaxSize());
        user.setRemain(propertyUtil.getUserMaxSize());
        user.setRole(1);
        boolean flag = userDao.insert(user)!=0;
        if(!flag)return false;
        return new File(propertyUtil.getBaseDir()+user.getUname()).mkdir();
    }

    /**
     * 登录
     * @param user
     * @return
     */
    public boolean login(User user){
        boolean flag = userDao.findOneByUserNameAndPasswd(user.getUname(), user.getPasswd())!=null;
        if(!flag)return false;
        propertyUtil.setUserDir(propertyUtil.getBaseDir()+user.getUname()+"/");
        return true;
    }

    /**
     * 根据uid查询
     * @param user
     * @return
     */
    public User findUser(User user){
        return userDao.findOneByUserNameAndPasswd(user.getUname(), user.getPasswd());
    }

    /**
     * 查询用户网盘容量
     * @param uid
     * @return
     */
    public User findVolume(int uid){
        User user = userDao.findByUid(uid);
        user.setUname("");
        user.setPasswd("");
        user.setGender(0);
        user.setBirthday("");
        user.setCity("");
        return user;
    }

    /**
     * 查询用户信息
     * @param uid
     * @return
     */
    public User findInfo(int uid){
        return userDao.findByUid(uid);
    }

    /**
     * 修改用户网盘剩余容量
     * @param uid 用户id
     * @param mode 是增加容量(mode=0) 还是 减少容量(mode=1)
     * @param fileSize 改变的文件大小(B)
     * @return 修改是否成功
     */
    public boolean changeUserRemain(int uid,int mode,double fileSize){
        //设置用户网盘剩余容量
        fileSize = fileSize/1024/1024/1024;
        BigDecimal bigDecimal = new BigDecimal(fileSize);
        fileSize = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        User user = userDao.findByUid(uid);
        String logMsg = "减少容量";
        if(mode==1){
            user.setRemain(user.getRemain()-fileSize);
        }else {
            user.setRemain(user.getRemain()+fileSize);
            logMsg = "增加容量";
        }

        if(!userDao.update(user))return false;
        logUtil.outPutLog(user.getUname(),logMsg,+fileSize+"G",logger,0);
        return true;
    }

    /**
     * 修改密码
     * @param uid
     * @param passwd
     * @return
     */
    public boolean changePasswd(int uid,String passwd){
        User user = userDao.findByUid(uid);
        user.setPasswd(passwd);
        return userDao.update(user);
    }
}
