package com.renmin.renminclouddisk.service;

import com.renmin.renminclouddisk.pojo.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    boolean createFiles(MultipartFile[] files, int parentId, int uid);

    java.io.File createSingleFile(MultipartFile file, int parentId, int uid);

    boolean createDir(String dirName, int parentId, int uid);

    void setFormat(File file);

    List<File> findFile(int parentId, int cid, String search, int uid);

    List<File> setFile(List<File> files);

    String buildZip(int[] fids);

    File findFileStructure(int uid);

    List<File> findSonsByRoot(int parentId, int uid);

    boolean moveFile(int fid, int targetId, int uid);

    boolean moveFileInDir(int parentId, String parentPath, int uid);

    boolean delFile(int[] fids, int uid);

    boolean delSingleFile(int fid, int uid);

    double getFileRemain(List<java.io.File> files);

    double getFileRemain(java.io.File file);
}
