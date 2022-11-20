package com.renmin.renminclouddisk.service;

import com.renmin.renminclouddisk.pojo.User;

public interface UserService {
    boolean checkUname(String uname);

    boolean register(User user);

    boolean login(User user);

    User findUser(User user);

    User findVolume(int uid);

    User findInfo(int uid);

    boolean changeUserRemain(int uid, int mode, double fileSize);

    boolean changePasswd(int uid, String passwd);
}
