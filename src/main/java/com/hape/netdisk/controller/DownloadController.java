package com.hape.netdisk.controller;

import com.hape.netdisk.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/download")
public class DownloadController {
    @Autowired
    private DownloadService downloadService;

    /**
     * 下载
     * @param fid 要下载的文件id
     * @param response
     * @return 下载是否成功进行
     */
    @GetMapping("f/{fid}")
    public void download(@PathVariable("fid") int fid, HttpServletResponse response){
        this.downloadService.downloadByFid(fid,response);
    }

    /**
     * 带激活码下载
     * @param response
     * @param code 激活码
     * @return 下载是否成功进行
     */
    @GetMapping("/code/{code}")
    public void downloadWithCode(HttpServletResponse response,@PathVariable("code") String code){
        this.downloadService.downloadWithCode(response, code);
    }

    /**
     * 下载压缩包
     * @param zipName 压缩包名
     * @param response
     * @return 下载是否成功进行
     */
    @GetMapping("/zip")
    public void downloadZip(String zipName,HttpServletResponse response){
        this.downloadService.downloadZip(zipName,response);
    }
}
