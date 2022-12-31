package com.hape.netdisk.service;

import com.hape.netdisk.controller.FileController;
import com.hape.netdisk.dao.*;
import com.hape.netdisk.pojo.*;
import com.hape.netdisk.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FileService {
    @Autowired
    private FileDao fileDao;
    @Autowired
    private FormatDao formatDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private CodeService codeService;
    @Autowired
    private CodeDao codeDao;
    @Autowired
    private UserService userService;
    @Autowired
    private org.springframework.beans.factory.BeanFactory factory;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private PropertyUtil propertyUtil;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private LogUtil logUtil;
    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    /**
     * 添加文件
     * @param files
     * @param parentId
     * @param uid
     * @return
     */
    public boolean createFiles(MultipartFile[] files,int parentId,int uid){
        ArrayList<java.io.File> saveFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            java.io.File f = this.createSingleFile(file,parentId, uid);
            if(f==null){
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            saveFiles.add(f);
        }
        //存储物理文件
        for (int i = 0; i < files.length; i++) {
            try {
                files[i].transferTo(saveFiles.get(i));
            } catch (IOException e) {
                e.printStackTrace();
                //回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        }
        //减少用户剩余容量
        if(!userService.changeUserRemain(uid,1,this.getFileRemain(saveFiles))){
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            fileUtil.delFile(saveFiles);
            return false;
        }
        return true;
    }

    /**
     * 添加单文件
     * @param file
     * @param parentId
     * @param uid
     * @return
     */
    private java.io.File createSingleFile(MultipartFile file, int parentId, int uid){
        File f = factory.getBean(File.class);
        String name = file.getOriginalFilename();

        String parentDir = propertyUtil.getUserDir();//父目录
        if(parentDir==null)return null;
        if(parentId!=0){
            parentDir = fileDao.findByFid(parentId).getPath();
        }
        if(parentId==0){
            name = fileUtil.checkFileName(parentDir+name);//检查后的文件名
        }else {
            name = fileUtil.checkFileName(propertyUtil.getUserDir()+parentDir+name);
        }

        if(name==null)return null;
        String filePath = parentDir+name+"/";//上传的文件要存储的路径
        if(parentId!=0){
            filePath = propertyUtil.getUserDir()+filePath;
        }

        //设置属性
        f.setUid(uid);//用户id
        f.setParentId(parentId);//父目录
        f.setFName(name);//文件名
        f.setFileSize(file.getSize());//文件大小
        //文件路径
        if(parentId==0){
            f.setPath(f.getFName()+"/");
        }else {
            f.setPath(parentDir+f.getFName()+"/");
        }

        java.io.File saveFile = new java.io.File(filePath);
        if(!saveFile.isDirectory()){
            f.setIsDir(0);//是否是文件夹
            this.setFormat(f);//设置格式 种类
        }

        String currentTime = timeUtil.getCurrentTime();

        f.setUploadTime(currentTime);//上传时间
        f.setUpdateTime(currentTime);//更新时间

        if(fileDao.insert(f)==0)return null;
        //输出日志
        logUtil.outPutLog(userDao.findByUid(f.getUid()).getUname(),"上传了文件",f.getFName(),logger,0);
        return saveFile;
    }

    /**
     * 创建文件夹
     * @param dirName
     * @param parentId
     * @param uid
     * @return
     */
    public boolean createDir(String dirName,int parentId,int uid){
        File f = factory.getBean(File.class);
        String name = dirName;

        String parentDir = propertyUtil.getUserDir();//父目录
        if(parentDir==null)return false;
        if(parentId!=0){
            parentDir = fileDao.findByFid(parentId).getPath();
        }

        if(parentId==0){
            name = fileUtil.checkFileName(parentDir+name);//检查后的文件名
        }else {
            name = fileUtil.checkFileName(propertyUtil.getUserDir()+parentDir+name);
        }
        String filePath = parentDir+name+"/";//上传的文件要存储的路径
        if(parentId!=0){
            filePath = propertyUtil.getUserDir()+filePath;
        }

        //设置属性
        f.setUid(uid);//用户id
        f.setParentId(parentId);//父目录
        f.setFName(name);//文件名
        f.setIsDir(1);
        //文件路径
        if(parentId==0){
            f.setPath(f.getFName()+"/");
        }else {
            f.setPath(parentDir+f.getFName()+"/");
        }

        String currentTime = timeUtil.getCurrentTime();

        f.setUploadTime(currentTime);//上传时间
        f.setUpdateTime(currentTime);//更新时间

        if(fileDao.insert(f)==0)return false;

        java.io.File saveFile = new java.io.File(filePath);
        boolean flag = saveFile.mkdir();
        if(!flag)return false;
        //输出日志
        logUtil.outPutLog(userDao.findByUid(f.getUid()).getUname(),"新建了文件夹",f.getFName(),logger,0);
        return true;
    }

    /**
     * 给文件设置 格式 和 种类
     * @param file
     */
    private void setFormat(File file){
        String fileName = file.getFName();
        Format format = formatDao.findByFName(fileName.substring(fileName.lastIndexOf(".")+1));
        if(format==null){
            List<Category> categories = categoryDao.findAll();
            int otherCid = categories.get(categories.size()-1).getCid();
            file.setCid(otherCid);
            file.setFormatId(0);
            return;
        }
        file.setFormatId(format.getFid());
        file.setCid(format.getCid());
    }

    /**
     * 根据当前目录id查询所有文件
     * @param parentId
     * @return
     */
    public List<File> findFile(int parentId,int cid,String search,int uid){
        List<File> files = fileDao.findByParentIdCidAndFName(parentId, cid, search,uid);
        return this.setFile(files);
    }

    /**
     * 设置返回的file属性
     * @param files
     * @return
     */
    private List<File> setFile(List<File> files){
        for (File file : files) {
            Format format = formatDao.findByFid(file.getFormatId());
            if(format!=null){
                file.setFormat(format);
                file.setCategory(categoryDao.findByCid(format.getCid()));
            }
            User user = userDao.findByUid(file.getUid());
            file.setUser(user);
        }
        return files;
    }

    /**
     * 构建压缩包
     * @param fids 需要构建的文件id
     * @return 压缩后的文件名
     */
    public String buildZip(int[] fids){
        String[] paths = new String[fids.length];
        for (int i = 0; i < fids.length; i++) {
            File f = fileDao.findByFid(fids[i]);
            if(f==null)return null;
            paths[i] = propertyUtil.getBaseDir()+userDao.findByUid(f.getUid()).getUname()+"/"+f.getPath();
        }
        java.io.File zip = null;
        try {
            zip = fileUtil.buildZip(paths, propertyUtil.getTempDir());
            if(zip==null)return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        //输出日志
        logUtil.outPutLog("unknown","构建了压缩包",zip.getName(),logger,0);
        return zip.getName();
    }

    /**
     * 查询所有文件的目录结构
     * @return
     */
    public File findFileStructure(int uid){
        File file = factory.getBean(File.class);
        file.setFid(0);
        file.setFName("root");
        file.setFile(this.findSonsByRoot(0,uid));
        return file;
    }

    /**
     * 查询根目录下所有子文件
     * @param parentId
     * @return
     */
    public List<File> findSonsByRoot(int parentId,int uid){
        List<File> sons = fileDao.findByParentId(parentId,uid);
        if(sons==null || sons.size()==0)return null;
        for (File son : sons) {
            son.setFile(this.findSonsByRoot(son.getFid(),uid));
        }
        return sons;
    }

    /**
     * 移动文件（文件夹）
     * @param fid
     * @param targetId
     * @return
     */
    public boolean moveFile(int fid,int targetId,int uid){
        String targetPath = propertyUtil.getUserDir();
        if(targetId!=0) targetPath = fileDao.findByFid(targetId).getPath();
        File file = fileDao.findByFid(fid);
        String fName = null;
        if(targetId!=0){
            fName = fileUtil.checkFileName(propertyUtil.getUserDir()+targetPath+file.getFName());
        }else {
            fName = fileUtil.checkFileName(targetPath+file.getFName());
        }
        String savePath = targetPath+fName+"/";//要移动到的路径
        if(targetId!=0){
            savePath = propertyUtil.getUserDir()+savePath;
        }
        String oldPath = propertyUtil.getUserDir()+file.getPath();//源文件路径
        String oldName = file.getFName();//源文件名
        if(fName==null)return false;

        file.setParentId(targetId);
        file.setFName(fName);
        file.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if(targetId==0){
            file.setPath(file.getFName()+"/");
        }else {
            file.setPath(targetPath+file.getFName()+"/");
        }

        if(file.getIsDir()==1 && !this.moveFileInDir(fid,file.getPath(),uid)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        //更新数据库中的数据
        if(!fileDao.update(file)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        //移动物理文件
        if(!fileUtil.moveFile(oldPath,savePath)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        //输出日志
        logUtil.outPutLog("unknown","移动了文件"+oldName,oldPath+" ---> "+savePath,logger,0);
        return true;
    }

    /**
     * 移动文件夹内文件
     * @param parentId
     * @param parentPath
     * @return
     */
    public boolean moveFileInDir(int parentId,String parentPath,int uid){
        List<File> sons = fileDao.findByParentId(parentId,uid);
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if(sons==null)return true;
        for (File son : sons) {
            if(parentId==0){
                son.setPath(son.getFName()+"/");
            }else {
                son.setPath(parentPath+son.getFName()+"/");
            }
            son.setUpdateTime(currentTime);
            if(!fileDao.update(son))return false;
            if(!this.moveFileInDir(son.getFid(),son.getPath(),uid))return false;
        }
        return true;
    }

    /**
     * 删除文件
     * @param fids
     * @return
     */
    public boolean delFile(int[] fids,int uid){
        List<java.io.File> files = new ArrayList<>();
        for (int fid : fids) {
            java.io.File f = new java.io.File(propertyUtil.getUserDir(),fileDao.findByFid(fid).getPath());
            files.add(f);

            if(!this.delSingleFile(fid,uid)){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }

            //检查是否完全删除了激活码
            List<Code> codes = codeDao.findByFid(fid);
            if(codes!=null && codes.size()!=0){
                for (Code c : codes) {
                    if(!codeDao.del(c.getCid()))return false;
                }
            }

            if(fileDao.findByFid(fid)!=null && !fileDao.del(fid)){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        }
        //增加用户剩余容量
        if(!userService.changeUserRemain(uid,0,this.getFileRemain(files))){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        if(!fileUtil.delFile(files)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    private boolean delSingleFile(int fid,int uid){
        if(!codeService.cancelShare(fid))return false;//删除文件对应激活码
        File file = fileDao.findByFid(fid);
        if(file.getIsDir()==0)return fileDao.del(fid);
        List<File> sons = fileDao.findByParentId(fid,uid);
        if(sons==null || sons.size()==0)
            return fileDao.del(fid);
        for (File son : sons) {
            if(!this.delSingleFile(son.getFid(),uid))
                return false;
            if(son.getIsDir()==1){
                if(fileDao.findByFid(son.getFid())!=null && !fileDao.del(son.getFid()))return false;
            }
        }
        //输出日志
        logUtil.outPutLog("unknown","删除了文件",file.getFName(),logger,0);
        return true;
    }

    /**
     * 获取文件总大小(B)
     * @param files  要统计的文件
     * @return
     */
    public double getFileRemain(List<java.io.File> files){
        if(files==null || files.size()==0)return 0;
        double a = 0;
        for (java.io.File f : files) {
            a+=this.getFileRemain(f);
        }
        return a;
    }

    private double getFileRemain(java.io.File file){
        if(!file.exists())return 0;
        if(!file.isDirectory())return file.length();
        java.io.File[] files = file.listFiles();
        if(files==null || files.length==0)return file.length();
        int a = 0;
        for (java.io.File f : files) {
            a+=this.getFileRemain(f);
        }
        return file.length()+a;
    }
}