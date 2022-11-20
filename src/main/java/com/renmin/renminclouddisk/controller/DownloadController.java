package com.renmin.renminclouddisk.controller;

import com.renmin.renminclouddisk.service.DownloadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/download")
public class DownloadController {
    @Resource
    private DownloadService downloadService;

    @GetMapping("f/{fid}")
    public void download(@PathVariable("fid") int fid, HttpServletResponse response) {
        this.downloadService.downloadByFid(fid, response);
    }

    @GetMapping("/code/{code}")
    public void downloadWithCode(HttpServletResponse response, @PathVariable("code") String code) {
        this.downloadService.downloadWithCode(response, code);
    }

    @GetMapping("/zip")
    public void downloadZip(String zipName, HttpServletResponse response) {
        this.downloadService.downloadZip(zipName, response);
    }
}
