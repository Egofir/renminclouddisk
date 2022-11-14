package com.renmin.renminclouddisk.service;

import com.renmin.renminclouddisk.exception.RenMinCloudDiskException;

public interface UserService {
    void setSensitiveWord();
    void register(String username, String password) throws RenMinCloudDiskException;
}
