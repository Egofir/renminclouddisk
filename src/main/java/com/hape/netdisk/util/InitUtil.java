package com.hape.netdisk.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class InitUtil {
    @Autowired
    private PropertyUtil propertyUtil;

    @PostConstruct
    private void setBasePath(){
        propertyUtil.setBaseDir(this.getRootPath());
    }

    private String getRootPath(){
        String s;
        s = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        if (System.getProperty("os.name").contains("dows")) {
            s = s.substring(1);
        }
        if (s.contains("jar")) {
            s = s.substring(0, s.lastIndexOf("."));
            s =  s.substring(0, s.lastIndexOf("/"));
            s = s.substring(5);
            s+="/";
        }else {
            s = s.replace("netdisk/target/classes/","");
        }
        s+="data/";
        return s;
    }
}
