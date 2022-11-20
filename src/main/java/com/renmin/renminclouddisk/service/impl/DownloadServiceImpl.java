package com.renmin.renminclouddisk.service.impl;

import com.renmin.renminclouddisk.dao.CodeDao;
import com.renmin.renminclouddisk.dao.FileDao;
import com.renmin.renminclouddisk.dao.UserDao;
import com.renmin.renminclouddisk.pojo.Code;
import com.renmin.renminclouddisk.service.DownloadService;
import com.renmin.renminclouddisk.service.FileService;
import com.renmin.renminclouddisk.util.LogUtil;
import com.renmin.renminclouddisk.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Service
public class DownloadServiceImpl implements DownloadService {
    @Resource
    private FileDao fileDao;
    @Resource
    private CodeDao codeDao;
    @Resource
    private UserDao userDao;
    @Resource
    private FileService fileService;
    @Resource
    private PropertyUtil propertyUtil;
    @Resource
    private LogUtil logUtil;
    private final Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Override
    public void download(File file, HttpServletResponse response) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            String filename = file.getName();
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(filename, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logUtil.outputLog("unknown", "下载了文件", file.getName(), logger, 1);
        }
        logUtil.outputLog("unknown", "下载了文件", file.getName(), logger, 0);
    }

    @Override
    public void downloadByFid(int fid, HttpServletResponse response) {
        com.renmin.renminclouddisk.pojo.File f = fileDao.findByFid(fid);
        if (f == null) {
            return;
        }
        if (f.getIsDir() == 0) {
            this.download(new File(propertyUtil.getBaseDir() +
                    userDao.findByUid(f.getUid()).getUname() + "/" + f.getPath()), response);
        } else {
            int[] fids = new int[1];
            fids[0] = f.getFid();
            String zipName = this.fileService.buildZip(fids);
            this.downloadZip(zipName, response);
        }
    }

    @Override
    public void downloadWithCode(HttpServletResponse response, String code) {
        Code c = codeDao.findByCode(code);
        if (c == null) {
            return;
        }
        this.downloadByFid(c.getFid(), response);
    }

    @Override
    public void downloadZip(String zipName, HttpServletResponse response) {
        this.download(new File(propertyUtil.getTempDir(), zipName), response);
    }
}
