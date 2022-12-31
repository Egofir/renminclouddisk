package com.hape.netdisk.service;

import com.hape.netdisk.controller.UserController;
import com.hape.netdisk.dao.CodeDao;
import com.hape.netdisk.dao.FileDao;
import com.hape.netdisk.dao.UserDao;
import com.hape.netdisk.pojo.Code;
import com.hape.netdisk.util.LogUtil;
import com.hape.netdisk.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Service
public class DownloadService {
    @Autowired
    private FileDao fileDao;
    @Autowired
    private CodeDao codeDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private PropertyUtil propertyUtil;
    @Autowired
    private LogUtil logUtil;
    private final Logger logger = LoggerFactory.getLogger(DownloadService.class);

    /**
     * 下载文件
     * @param file 要下载的文件
     * @param response
     * @return 下载是否正常进行
     */
    public void download(File file,HttpServletResponse response){
        if(file==null || !file.exists())return;
        try {
            // 获取文件名
            String filename = file.getName();
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

            // 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logUtil.outPutLog("unknown","下载了文件",file.getName(),logger,1);
        }
        logUtil.outPutLog("unknown","下载了文件",file.getName(),logger,0);
    }

    /**
     * 根据文件id下载文件
     * @param fid
     * @param response
     */
    public void downloadByFid(int fid, HttpServletResponse response){
        com.hape.netdisk.pojo.File f = fileDao.findByFid(fid);
        if(f==null)return;
        if(f.getIsDir()==0){
            //如果是文件,直接下载
            this.download(new File(propertyUtil.getBaseDir()+userDao.findByUid(f.getUid()).getUname()+"/"+f.getPath()),response);
        }else {
            //如果是文件夹,先构建压缩包,再加载压缩包
            int[] fids = new int[1];
            fids[0] = f.getFid();
            String zipName = this.fileService.buildZip(fids);
            this.downloadZip(zipName,response);
        }
    }

    /**
     * 下载文件（带激活码）
     * @param response
     * @param code
     */
    public void downloadWithCode(HttpServletResponse response,String code){
        Code c = codeDao.findByCode(code);
        if(c==null)return;
        this.downloadByFid(c.getFid(),response);
    }

    /**
     * 下载压缩包
     * @param zipName 压缩包名
     * @param response
     * @return
     */
    public void downloadZip(String zipName,HttpServletResponse response){
        this.download(new File(propertyUtil.getTempDir(),zipName),response);
    }
}