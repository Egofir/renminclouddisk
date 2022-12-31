package com.hape.netdisk;

import com.hape.netdisk.dao.UserDao;
import com.hape.netdisk.pojo.User;
import com.hape.netdisk.util.PropertyUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.util.List;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@MapperScan("com.hape.netdisk.mapper")
public class NetdiskApplication implements CommandLineRunner {
    @Autowired
    private PropertyUtil propertyUtil;
    @Autowired
    private UserDao userDao;

    public static void main(String[] args){
        SpringApplication.run(NetdiskApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        this.initDir();
    }

    /**
     * 查询所有用户,根据其用户名创建目录
     */
    private void initDir(){
        String baseDir = propertyUtil.getBaseDir();//文件存储根目录
        File baseFile = new File(baseDir);
        if(!baseFile.exists()) baseFile.mkdir();
        File tempFile = new File(baseDir,"temp");
        if(!tempFile.exists())tempFile.mkdir();//创建temp目录
        propertyUtil.setTempDir(baseDir+"temp/");//设置temp目录
        List<User> users = userDao.findAll();
        for (User user : users) {
            new File(baseDir+user.getUname()).mkdir();
        }
        propertyUtil.setUserDir(null);
    }

}
