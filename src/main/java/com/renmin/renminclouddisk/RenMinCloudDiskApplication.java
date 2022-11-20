package com.renmin.renminclouddisk;

import com.renmin.renminclouddisk.dao.UserDao;
import com.renmin.renminclouddisk.pojo.User;
import com.renmin.renminclouddisk.util.PropertyUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@MapperScan("com.renmin.renminclouddisk.mapper")
public class RenMinCloudDiskApplication implements CommandLineRunner {
    @Resource
    private PropertyUtil propertyUtil;
    @Resource
    private UserDao userDao;

    public static void main(String[] args) {
        SpringApplication.run(RenMinCloudDiskApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        this.initDir();
    }

    private void initDir() {
        String baseDir = propertyUtil.getBaseDir();
        File baseFile = new File(baseDir);
        if (!baseFile.exists()) {
            baseFile.mkdir();
        }
        File tempFile = new File(baseDir, "temp");
        if (!tempFile.exists()) {
            tempFile.mkdir();
        }
        propertyUtil.setTempDir(baseDir + "temp/");
        List<User> users = userDao.findAll();
        for (User user : users) {
            new File(baseDir + user.getUname()).mkdir();
        }
        propertyUtil.setUserDir(null);
    }
}
