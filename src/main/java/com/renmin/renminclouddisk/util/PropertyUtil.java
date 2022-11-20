package com.renmin.renminclouddisk.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class PropertyUtil {
    private String baseDir;
    private String tempDir;
    private String userDir;
    private final String foreverTime = "2099-12-31 23:59:59";
    @Value("1.0")
    private double userMaxSize;
}
