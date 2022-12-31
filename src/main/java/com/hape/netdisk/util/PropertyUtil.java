package com.hape.netdisk.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class PropertyUtil {
//    @Value("${path.baseDir}")
    @Setter
    private String baseDir;//文件存储根目录
    @Setter
    private String tempDir;//缓存目录
    @Setter
    private String userDir;//用户存储目录
    private final String foreverTime = "2099-12-31 23:59:59";//永久日期
    @Value("${maxSize.default}")
    private double userMaxSize;//用户默认网盘最大空间
}
