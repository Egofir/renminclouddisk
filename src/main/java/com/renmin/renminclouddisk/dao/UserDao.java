package com.renmin.renminclouddisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.renmin.renminclouddisk.pojo.User;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class UserDao {
    @Resource
    private User user;

    public int insert(User user) {
        boolean flag = user.insert();
        if (!flag) {
            return 0;
        }
        return user.getUid();
    }

    public List<User> findAll() {
        return user.selectAll();
    }

    public User findOneByUserNameAndPasswd(String uname, String passwd) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("uname", uname).eq("passwd", passwd);
        return user.selectOne(wrapper);
    }

    public User findByUid(int uid) {
        user.setUid(uid);
        return user.selectById();
    }

    public User findByUname(String uname) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("uname",uname);
        return user.selectOne(wrapper);
    }

    public boolean update(User user) {
        return user.updateById();
    }
}
