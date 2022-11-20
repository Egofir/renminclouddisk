package com.renmin.renminclouddisk.service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface DownloadService {
    void download(File file, HttpServletResponse response);

    void downloadByFid(int fid, HttpServletResponse response);

    void downloadWithCode(HttpServletResponse response, String code);

    void downloadZip(String zipName, HttpServletResponse response);
}
