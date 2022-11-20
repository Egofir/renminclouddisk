package com.renmin.renminclouddisk.service.impl;

import com.renmin.renminclouddisk.controller.FileController;
import com.renmin.renminclouddisk.dao.*;
import com.renmin.renminclouddisk.pojo.*;
import com.renmin.renminclouddisk.service.CodeService;
import com.renmin.renminclouddisk.service.FileService;
import com.renmin.renminclouddisk.service.UserService;
import com.renmin.renminclouddisk.util.FileUtil;
import com.renmin.renminclouddisk.util.LogUtil;
import com.renmin.renminclouddisk.util.PropertyUtil;
import com.renmin.renminclouddisk.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Resource
    private BeanFactory factory;
    @Resource
    private PropertyUtil propertyUtil;
    @Resource
    private FileUtil fileUtil;
    @Resource
    private TimeUtil timeUtil;
    @Resource
    private LogUtil logUtil;
    @Resource
    private FileDao fileDao;
    @Resource
    private FormatDao formatDao;
    @Resource
    private UserDao userDao;
    @Resource
    private CodeDao codeDao;
    @Resource
    private CategoryDao categoryDao;
    @Resource
    private UserService userService;
    @Resource
    private CodeService codeService;
    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Override
    public boolean createFiles(MultipartFile[] files, int parentId, int uid) {
        ArrayList<java.io.File> saveFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            java.io.File f = this.createSingleFile(file, parentId, uid);
            if (f == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            saveFiles.add(f);
        }
        for (int i = 0; i < files.length; i++) {
            try {
                files[i].transferTo(saveFiles.get(i));
            } catch (IOException e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        }
        if (!userService.changeUserRemain(uid, 1, this.getFileRemain(saveFiles))) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            fileUtil.delFile(saveFiles);
            return false;
        }
        return true;
    }

    @Override
    public java.io.File createSingleFile(MultipartFile file, int parentId, int uid) {
        File f = factory.getBean(File.class);
        String name = file.getOriginalFilename();
        String parentDir = propertyUtil.getUserDir();
        if (parentDir == null) {
            return null;
        }
        if (parentId != 0) {
            parentDir = fileDao.findByFid(parentId).getPath();
        }
        if (parentId == 0) {
            name = fileUtil.checkFileName(parentDir + name);
        } else {
            name = fileUtil.checkFileName(propertyUtil.getUserDir() + parentDir + name);
        }
        if (name == null) {
            return null;
        }
        String filePath = parentDir + name + "/";
        if (parentId != 0) {
            filePath = propertyUtil.getUserDir() + filePath;
        }
        f.setUid(uid);
        f.setParentId(parentId);
        f.setFName(name);
        f.setFileSize(file.getSize());
        if (parentId == 0) {
            f.setPath(f.getFName() + "/");
        } else {
            f.setPath(parentDir + f.getFName() + "/");
        }
        java.io.File saveFile = new java.io.File(filePath);
        if (!saveFile.isDirectory()) {
            f.setIsDir(0);
            this.setFormat(f);
        }
        String currentTime = timeUtil.getCurrentTime();
        f.setUploadTime(currentTime);
        f.setUpdateTime(currentTime);
        if (fileDao.insert(f) == 0) {
            return null;
        }
        logUtil.outputLog(userDao.findByUid(f.getUid()).getUname(), "上传了文件", f.getFName(),
                logger, 0);
        return saveFile;
    }

    @Override
    public boolean createDir(String dirName, int parentId, int uid) {
        File file = factory.getBean(File.class);
        String name = dirName;
        String parentDir = propertyUtil.getUserDir();
        if (parentDir == null) {
            return false;
        }
        if (parentId != 0) {
            parentDir = fileDao.findByFid(parentId).getPath();
        }
        if (parentId == 0) {
            name = fileUtil.checkFileName(parentDir + name);
        } else {
            name = fileUtil.checkFileName(propertyUtil.getUserDir() + parentDir + name);
        }
        String filePath = parentDir + name + "/";
        if (parentId != 0) {
            filePath = propertyUtil.getUserDir() + filePath;
        }
        file.setUid(uid);
        file.setParentId(parentId);
        file.setFName(name);
        file.setIsDir(1);
        if (parentId == 0) {
            file.setPath(file.getFName() + "/");
        } else {
            file.setPath(parentDir + file.getFName() + "/");
        }
        String currentTime = timeUtil.getCurrentTime();
        file.setUploadTime(currentTime);
        file.setUpdateTime(currentTime);
        if (fileDao.insert(file) == 0) {
            return false;
        }
        java.io.File saveFile = new java.io.File(filePath);
        boolean flag = saveFile.mkdir();
        if (!flag) {
            return false;
        }
        logUtil.outputLog(userDao.findByUid(file.getUid()).getUname(), "新建了文件夹",
                file.getFName(), logger, 0);
        return true;
    }

    @Override
    public void setFormat(File file) {
        String fileName = file.getFName();
        Format format = formatDao.findByFName(fileName.substring(fileName.lastIndexOf(".") + 1));
        if (format == null) {
            List<Category> categories = categoryDao.findAll();
            int otherCid = categories.get(categories.size() - 1).getCid();
            file.setCid(otherCid);
            file.setFormatId(0);
            return;
        }
        file.setFormatId(format.getFid());
        file.setCid(format.getCid());
    }

    @Override
    public List<File> findFile(int parentId, int cid, String search, int uid) {
        List<File> files = fileDao.findByParentIdCidAndFName(parentId, cid, search, uid);
        return this.setFile(files);
    }

    @Override
    public List<File> setFile(List<File> files) {
        for (File file : files) {
            Format format = formatDao.findByFid(file.getFormatId());
            if (format != null) {
                file.setFormat(format);
                file.setCategory(categoryDao.findByCid(format.getCid()));
            }
            User user = userDao.findByUid(file.getUid());
            file.setUser(user);
        }
        return files;
    }

    @Override
    public String buildZip(int[] fids) {
        String[] paths = new String[fids.length];
        for (int i = 0; i < fids.length; i++) {
            File f = fileDao.findByFid(fids[i]);
            if (f == null) {
                return null;
            }
            paths[i] = propertyUtil.getBaseDir() + userDao.findByUid(f.getUid()).getUname() + "/" +
                    f.getPath();
        }
        java.io.File zip = null;
        try {
            zip = fileUtil.buildZip(paths, propertyUtil.getTempDir());
            if (zip == null) return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        logUtil.outputLog("unknown", "构建了压缩包", zip.getName(), logger, 0);
        return zip.getName();
    }

    @Override
    public File findFileStructure(int uid) {
        File file = factory.getBean(File.class);
        file.setFid(0);
        file.setFName("root");
        file.setFile(this.findSonsByRoot(0, uid));
        return file;
    }

    @Override
    public List<File> findSonsByRoot(int parentId, int uid) {
        List<File> sons = fileDao.findByParentId(parentId, uid);
        if (sons == null || sons.size() == 0) {
            return null;
        }
        for (File son : sons) {
            son.setFile(this.findSonsByRoot(son.getFid(), uid));
        }
        return sons;
    }

    @Override
    public boolean moveFile(int fid, int targetId, int uid) {
        String targetPath = propertyUtil.getUserDir();
        if (targetId != 0) {
            targetPath = fileDao.findByFid(targetId).getPath();
        }
        File file = fileDao.findByFid(fid);
        String fName = null;
        if (targetId != 0) {
            fName = fileUtil.checkFileName(propertyUtil.getUserDir() + targetPath +
                    file.getFName());
        } else {
            fName = fileUtil.checkFileName(targetPath + file.getFName());
        }
        String savePath = targetPath + fName + "/";
        if (targetId != 0) {
            savePath = propertyUtil.getUserDir() + savePath;
        }
        String oldPath = propertyUtil.getUserDir() + file.getPath();
        String oldName = file.getFName();
        if (fName == null) return false;

        file.setParentId(targetId);
        file.setFName(fName);
        file.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (targetId == 0) {
            file.setPath(file.getFName() + "/");
        } else {
            file.setPath(targetPath + file.getFName() + "/");
        }

        if (file.getIsDir() == 1 && !this.moveFileInDir(fid, file.getPath(), uid)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        if (!fileDao.update(file)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        if (!fileUtil.moveFile(oldPath, savePath)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        logUtil.outputLog("unknown", "移动了文件" + oldName, oldPath + " ---> " +
                savePath, logger, 0);
        return true;
    }

    @Override
    public boolean moveFileInDir(int parentId, String parentPath, int uid) {
        List<File> sons = fileDao.findByParentId(parentId, uid);
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (sons == null) {
            return true;
        }
        for (File son : sons) {
            if (parentId == 0) {
                son.setPath(son.getFName() + "/");
            } else {
                son.setPath(parentPath + son.getFName() + "/");
            }
            son.setUpdateTime(currentTime);
            if (!fileDao.update(son)) {
                return false;
            }
            if (!this.moveFileInDir(son.getFid(), son.getPath(), uid)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean delFile(int[] fids, int uid) {
        List<java.io.File> files = new ArrayList<>();
        for (int fid : fids) {
            java.io.File f = new java.io.File(propertyUtil.getUserDir(),
                    fileDao.findByFid(fid).getPath());
            files.add(f);
            if (!this.delSingleFile(fid, uid)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            List<Code> codes = codeDao.findByFid(fid);
            if (codes != null && codes.size() != 0) {
                for (Code c : codes) {
                    if (!codeDao.del(c.getCid())) {
                        return false;
                    }
                }
            }
            if (fileDao.findByFid(fid) != null && !fileDao.del(fid)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        }
        if (!userService.changeUserRemain(uid, 0, this.getFileRemain(files))) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        if (!fileUtil.delFile(files)) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public boolean delSingleFile(int fid, int uid) {
        if (!codeService.cancelShare(fid)) {
            return false;
        }
        File file = fileDao.findByFid(fid);
        if (file.getIsDir() == 0) {
            return fileDao.del(fid);
        }
        List<File> sons = fileDao.findByParentId(fid, uid);
        if (sons == null || sons.size() == 0) {
            return fileDao.del(fid);
        }
        for (File son : sons) {
            if (!this.delSingleFile(son.getFid(), uid))
                return false;
            if (son.getIsDir() == 1) {
                if (fileDao.findByFid(son.getFid()) != null && !fileDao.del(son.getFid())) {
                    return false;
                }
            }
        }
        logUtil.outputLog("unknown", "删除了文件", file.getFName(), logger, 0);
        return true;
    }

    @Override
    public double getFileRemain(List<java.io.File> files) {
        if (files == null || files.size() == 0) {
            return 0;
        }
        double a = 0;
        for (java.io.File file : files) {
            a += this.getFileRemain(file);
        }
        return a;
    }

    @Override
    public double getFileRemain(java.io.File file) {
        if (!file.exists()) {
            return 0;
        }
        if (!file.isDirectory()) {
            return file.length();
        }
        java.io.File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return file.length();
        }
        int a = 0;
        for (java.io.File f : files) {
            a += this.getFileRemain(f);
        }
        return a + file.length();
    }
}
