package com.hape.netdisk.service;

import com.hape.netdisk.dao.CodeDao;
import com.hape.netdisk.dao.FileDao;
import com.hape.netdisk.dao.UserDao;
import com.hape.netdisk.pojo.Code;
import com.hape.netdisk.pojo.File;
import com.hape.netdisk.pojo.User;
import com.hape.netdisk.util.CodeTimeTask;
import com.hape.netdisk.util.PropertyUtil;
import com.hape.netdisk.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CodeService {
    @Autowired
    private CodeDao codeDao;
    @Autowired
    private Code code;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private CodeTimeTask codeTimeTask;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * 分享文件
     * @param fid 文件id
     * @param time 链接保存多久（ms）
     * @return
     */
    public String shareFile(int fid,long time){
        String activeCode = UUID.randomUUID().toString();
        String endTime = propertyUtil.getForeverTime();//永久时间
        if(time!=0)endTime = timeUtil.getTimeByMilliSecond(time+System.currentTimeMillis());
        code.setFid(fid);
        code.setActiveCode(activeCode);
        code.setEndTime(endTime);
        int cid = codeDao.insert(code);
        if(cid==0)return null;
        if(time!=0)codeTimeTask.delCode(cid,time);
        return activeCode;
    }

    /**
     * 根据激活码查询
     * @param code
     * @return
     */
    public Code findFileByCode(String code){
        Code c = codeDao.findByCode(code);
        if(c==null)return null;
        File file = fileDao.findByFid(c.getFid());
        User user = userDao.findByUid(file.getUid());
        file.setUser(user);
        c.setFile(file);
        return c;
    }

    /**
     * 取消分享文件
     * @param fid
     * @return
     */
    public boolean cancelShare(int fid){
        List<Code> list = codeDao.findByFid(fid);
        if(list==null || list.size()==0)return true;
        for (Code c : list) {
            if(!codeDao.del(c.getCid()))return false;
        }
        return true;
    }
}
