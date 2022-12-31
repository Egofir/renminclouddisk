package com.hape.netdisk.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class FileUtil {
    /**
     * 检查文件（文件夹）名称 在其父目录是否重名 如重名则在文件名后 + (重复次数)
     * @param filePath
     * @return
     */
    public String checkFileName(String filePath){
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if(!parentFile.exists())return null;
        String fileName = file.getName();
        String suffix = "";
        if(fileName.contains(".")){
            suffix = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0,fileName.lastIndexOf("."));
        }
        return this.buildFileName(parentFile.getPath()+"/",fileName,suffix,0).getName();
    }

    /**
     * 如果附件存在则在原文件名基础上加1 例 1（1）.txt
     *
     * @param path     文件存放路径 末尾要有 /
     * @param fileName 文件名
     * @param suffix 文件后缀
     * @param index    当前下标 初次调用传入0
     * @return 返回file
     */
    private File buildFileName(String path, String fileName, String suffix, Integer index) {
        File file;
        //下标不等于0开始拼后缀
        if (index != 0) {
            file = new File(path + fileName + "(" + index + ")" + suffix);
        } else {
            file = new File(path + fileName + suffix);
        }
        //判断文件是否存在 文件不存在退出递归
        if (file.exists()) {
            //每次递归给下标加1
            file = buildFileName(path, fileName, suffix, ++index);
        }
        return file;
    }

    /**
     * 移动文件
     * @param path 源文件路径
     * @param targetPath 目标目录
     * @return
     */
    public boolean moveFile(String path,String targetPath){
        File file = new File(path);
        File targetFile = new File(targetPath);
        if(file.isDirectory()){
            return this.copyFolder(file,targetFile) && this.delFile(file);
        }
        return file.renameTo(targetFile);
    }

    /**
     * 复制一个目录的子目录和文件到另外一个目录
     * @param src
     * @param dest
     */
    private boolean copyFolder(File src, File dest){
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dest);

                byte[] buffer = new byte[1024];

                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 删除多文件
     * @param files
     * @return
     */
    public boolean delFile(List<File> files){
        for (File file : files) {
            if(!this.delFile(file))return false;
            if(file.exists())file.delete();
        }
        return true;
    }

    /**
     * 递归删除文件
     * @param file
     * @return
     */
    private boolean delFile(File file){
        if(!file.exists())return false;
        if(!file.isDirectory())return file.delete();
        File[] files = file.listFiles();
        if(files==null || files.length==0)return true;
        for (File f : files) {
            if(!this.delFile(f))return false;
            if(f.isDirectory()){
                if(!f.delete())return false;
            }
        }
        return file.delete();
    }

    /**
     * 构建压缩包
     * @param paths 要构建的源文件路径数组
     * @param targetPath 目标路径
     * @return
     * @throws FileNotFoundException
     */
    public File buildZip(String[] paths,String targetPath) throws FileNotFoundException {
        String zipName = UUID.randomUUID().toString();//压缩包名
        File targetDir = new File(targetPath, zipName);//新文件夹用于存放所有要压缩的文件
        targetDir.mkdir();
        for (String path : paths) {
            File file = new File(path);//源文件
            File f = new File(targetDir.getAbsolutePath(),file.getName());;//目标目录
            if(file.isDirectory())
                f.mkdir();//如果源文件是文件夹 则在目标目录下创建
            //将要压缩的文件或文件夹复制到新的文件夹中
            if(!this.copyFolder(file, f))return null;
        }
        File targetFile = new File(targetPath,zipName+".zip");
        FileOutputStream fos = new FileOutputStream(targetFile);
        //构建压缩包
        this.toZip(targetDir.getAbsolutePath(), fos,true);
        if(!targetFile.exists())return null;
        //删除新建的目录
        ArrayList<File> list = new ArrayList<>();
        list.add(new File(targetDir.getAbsolutePath()));
        if(!this.delFile(list))return null;
        return targetFile;
    }

    /**
     * 压缩成ZIP 方法1
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    private void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException{

        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos		 zip输出流
     * @param name		 压缩后的名称
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * 							false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[2048];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }

            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }

                }
            }
        }
    }
}
