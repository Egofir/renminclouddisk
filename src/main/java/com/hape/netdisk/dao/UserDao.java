package com.hape.netdisk.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hape.netdisk.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UserDao {
    @Autowired
    private User user;

    /**
     * 插入
     * @param user
     * @return
     */
    public int insert(User user) {
        boolean flag = user.insert();
        if(!flag){
            return 0;
        }
        return user.getUid();
    }

    /**
     * 查询所有用户
     * @return
     */
    public List<User> findAll(){
        return user.selectAll();
    }

    /**
     * 根据用户名和密码查询
     * @param uname
     * @param passwd
     * @return
     */
    public User findOneByUserNameAndPasswd(String uname,String passwd) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("uname",uname).eq("passwd",passwd);
        return user.selectOne(wrapper);
    }

    /**
     * 根据id查询
     * @param uid
     * @return
     */
    public User findByUid(int uid) {
        user.setUid(uid);
        return user.selectById();
    }

    /**
     * 根据用户名查询
     * @param uname
     * @return
     */
    public User findByUname(String uname) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("uname",uname);
        return user.selectOne(wrapper);
    }

    /**
     * 修改
     * @param user
     * @return
     */
    public boolean update(User user) {
        return user.updateById();
    }
}
