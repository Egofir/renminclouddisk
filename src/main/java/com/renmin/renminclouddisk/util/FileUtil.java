package com.renmin.renminclouddisk.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    public String checkFileName(String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            return null;
        }
        String fileName = file.getName();
        String suffix = "";
        if (fileName.contains(".")) {
            suffix = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return this.buildFileName(parentFile.getPath() + "/", fileName, suffix, 0).getName();
    }

    private File buildFileName(String path, String fileName, String suffix, Integer index) {
        File file;
        if (index != 0) {
            file = new File(path + fileName + "(" + index + ")" + suffix);
        } else {
            file = new File(path + fileName + suffix);
        }
        if (file.exists()) {
            file = buildFileName(path, fileName, suffix, ++index);
        }
        return file;
    }

    public boolean moveFile(String path, String targetPath) {
        File file = new File(path);
        File targetFile = new File(targetPath);
        if (file.isDirectory()) {
            return this.copyFolder(file, targetFile) && this.delFile(file);
        }
        return file.renameTo(targetFile);
    }

    private boolean copyFolder(File src, File dest) {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String[] files = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(src);
                outputStream = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean delFile(List<File> files) {
        for (File file : files) {
            if (!this.delFile(file)) {
                return false;
            }
            if (file.exists()) {
                file.delete();
            }
        }
        return true;
    }

    private boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return true;
        }
        for (File f : files) {
            if (!this.delFile(f)) {
                return false;
            }
            if (f.isDirectory()) {
                if (!f.delete()) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public File buildZip(String[] paths, String targetPath) throws FileNotFoundException {
        String zipName = UUID.randomUUID().toString();
        File targetDir = new File(targetPath, zipName);
        targetDir.mkdir();
        for (String path : paths) {
            File file = new File(path);
            File f = new File(targetDir.getAbsolutePath(), file.getName());
            if (file.isDirectory()) {
                f.mkdir();
            }
            if (!this.copyFolder(file, f)) {
                return null;
            }
        }
        File targetFile = new File(targetPath, zipName + ".zip");
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        this.toZip(targetDir.getAbsolutePath(), fileOutputStream, true);
        if (!targetFile.exists()) {
            return null;
        }
        ArrayList<File> list = new ArrayList<>();
        list.add(new File(targetDir.getAbsolutePath()));
        if (!this.delFile(list)) {
            return null;
        }
        return targetFile;
    }

    private void toZip(String srcDir, OutputStream outputStream, boolean keepDirStructure) {
        long start = System.currentTimeMillis();
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(outputStream);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zipOutputStream, sourceFile.getName(), keepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) + " ms");
        } catch (IOException e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void compress(File sourceFile, ZipOutputStream zipOutputStream, String name,
                          boolean keepDirStructure) throws IOException {
        byte[] buf = new byte[2048];
        if (sourceFile.isFile()) {
            zipOutputStream.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            while ((len = fileInputStream.read(buf)) != -1) {
                zipOutputStream.write(buf, 0, len);
            }
            zipOutputStream.closeEntry();
            fileInputStream.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (keepDirStructure) {
                    zipOutputStream.putNextEntry(new ZipEntry(name + "/"));
                    zipOutputStream.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (keepDirStructure) {
                        compress(file, zipOutputStream, name + "/" + file.getName(),
                                true);
                    } else {
                        compress(file, zipOutputStream, file.getName(), false);
                    }
                }
            }
        }
    }
}
