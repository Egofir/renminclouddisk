package com.hape.netdisk.controller;

import com.hape.netdisk.pojo.Code;
import com.hape.netdisk.pojo.File;
import com.hape.netdisk.pojo.Info;
import com.hape.netdisk.pojo.User;
import com.hape.netdisk.service.CodeService;
import com.hape.netdisk.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private UserController userController;
    @Autowired
    private Info info;

    /**
     * 上传文件
     * @param files
     * @param parentId
     * @return
     */
    @PostMapping("/upload/{parentId}")
    public boolean upload(MultipartFile[] files, String dirName, @PathVariable("parentId") int parentId, HttpSession session){
        User user = userController.getUserInfo(session);
        if(user==null || (files==null && dirName==null))return false;
        if(files!=null)return fileService.createFiles(files, parentId, user.getUid());
        else return fileService.createDir(dirName,parentId, user.getUid());
    }

    /**
     * 查询文件
     * @param parentId
     * @param cid
     * @param search
     * @return
     */
    @GetMapping("find/{parentId}/{cid}")
    public List<File> findFile(
            @PathVariable("parentId") int parentId,
            @PathVariable("cid") int cid,
            String search,
            HttpSession session){
        User user = userController.getUserInfo(session);
        if(user==null)return null;
        return fileService.findFile(parentId, cid, search, user.getUid());
    }

    /**
     * 移动文件
     * @param fid 待移动文件id
     * @param targetId 目标目录id
     * @return
     */
    @PutMapping("/move/{fid}/{targetId}")
    public boolean moveFile(@PathVariable("fid") int fid,@PathVariable("targetId") int targetId,HttpSession session){
        User user = userController.getUserInfo(session);
        if(user==null)return false;
        return fileService.moveFile(fid, targetId, user.getUid());
    }

    /**
     * 分享文件的下载链接
     * @param fid 文件id
     * @param time 链接有效时间（ms）
     * @return 文件的下载链接
     */
    @PutMapping("/share/{fid}")
    public Info shareLink(@PathVariable("fid") int fid, long time){
        info.setStatus(true);
        String code = codeService.shareFile(fid, time);
        if(code==null){
            info.setStatus(false);
            return info;
        }
        info.setMsg(code);
        return info;
    }

    /**
     * 根据激活码查询
     * @param code
     * @return
     */
    @GetMapping("/getCode")
    public Code findByCode(String code){
        return codeService.findFileByCode(code);
    }

    /**
     * 获取所有文件目录结构
     * @return
     */
    @GetMapping("/getStructure")
    public File getFileStructure(HttpSession session){
        User user = userController.getUserInfo(session);
        if(user==null)return null;
        return fileService.findFileStructure(user.getUid());
    }

    /**
     * 取消分享文件
     * @param fid 文件id
     * @return
     */
    @DeleteMapping("/cancel/{fid}")
    public boolean cancelShare(@PathVariable("fid") int fid){
        return codeService.cancelShare(fid);
    }

    /**
     * 构建压缩包
     * @param fids 需要构建的文件id
     * @return 压缩包名
     */
    @GetMapping("/buildZip")
    public String buildZip(int[] fids){
        String zipName = fileService.buildZip(fids);
        if(zipName==null)
            return "";
        return zipName;
    }

    /**
     * 删除文件
     * @param fids
     * @return
     */
    @DeleteMapping("del")
    public boolean delFile(int[] fids,HttpSession session){
        User user = userController.getUserInfo(session);
        if(user==null)return false;
        return fileService.delFile(fids,user.getUid());
    }
}
