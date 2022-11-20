package com.renmin.renminclouddisk.controller;

import com.renmin.renminclouddisk.pojo.Code;
import com.renmin.renminclouddisk.pojo.File;
import com.renmin.renminclouddisk.pojo.Info;
import com.renmin.renminclouddisk.pojo.User;
import com.renmin.renminclouddisk.service.CodeService;
import com.renmin.renminclouddisk.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    private FileService fileService;
    @Resource
    private CodeService codeService;
    @Resource
    private UserController userController;
    @Resource
    private Info info;

    @PostMapping("/upload/{parentId}")
    public boolean upload(MultipartFile[] files, String dirName, @PathVariable("parentId") int parentId,
                          HttpSession session) {
        User user = userController.getUserInfo(session);
        if (user == null || (files == null && dirName == null)) {
            return false;
        }
        if (files != null) {
            return fileService.createFiles(files, parentId, user.getUid());
        } else {
            return fileService.createDir(dirName, parentId, user.getUid());
        }
    }

    @GetMapping("find/{parentId}/{cid}")
    public List<File> findFile(
            @PathVariable("parentId") int parentId,
            @PathVariable("cid") int cid,
            String search,
            HttpSession session) {
        User user = userController.getUserInfo(session);
        if (user == null) {
            return null;
        }
        return fileService.findFile(parentId, cid, search, user.getUid());
    }

    @PutMapping("/move/{fid}/{targetId}")
    public boolean moveFile(@PathVariable("fid") int fid, @PathVariable("targetId") int targetId,
                            HttpSession session) {
        User user = userController.getUserInfo(session);
        if (user == null) {
            return false;
        }
        return fileService.moveFile(fid, targetId, user.getUid());
    }

    @PutMapping("/share/{fid}")
    public Info shareLink(@PathVariable("fid") int fid, long time) {
        info.setStatus(true);
        String code = codeService.shareFile(fid, time);
        if (code == null) {
            info.setStatus(false);
            return info;
        }
        info.setMsg(code);
        return info;
    }

    @GetMapping("/getCode")
    public Code findByCode(String code) {
        return codeService.findFileByCode(code);
    }

    @GetMapping("/getStructure")
    public File getFileStructure(HttpSession session) {
        User user = userController.getUserInfo(session);
        if (user == null) {
            return null;
        }
        return fileService.findFileStructure(user.getUid());
    }

    @DeleteMapping("/cancel/{fid}")
    public boolean cancelShare(@PathVariable("fid") int fid) {
        return codeService.cancelShare(fid);
    }

    @GetMapping("/buildZip")
    public String buildZip(int[] fids) {
        String zipName = fileService.buildZip(fids);
        if (zipName == null) {
            return "";
        }
        return zipName;
    }

    @DeleteMapping("del")
    public boolean delFile(int[] fids, HttpSession session) {
        User user = userController.getUserInfo(session);
        if (user == null) {
            return false;
        }
        return fileService.delFile(fids, user.getUid());
    }
}
