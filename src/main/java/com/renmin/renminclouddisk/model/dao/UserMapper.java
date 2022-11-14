package com.renmin.renminclouddisk.model.dao;

import com.renmin.renminclouddisk.model.pojo.User;

public interface UserMapper {
    User queryUserByUsername(String username);
}
