package com.renmin.renminclouddisk.service;

import com.renmin.renminclouddisk.pojo.Code;

public interface CodeService {
    String shareFile(int fid, long time);
    Code findFileByCode(String code);
    boolean cancelShare(int fid);
}
