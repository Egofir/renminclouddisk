package com.renmin.renminclouddisk.config;

import com.renmin.renminclouddisk.util.PropertyUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private PropertyUtil propertyUtil;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        urlPathHelper.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/view/**")
                .addResourceLocations("file:" + propertyUtil.getBaseDir());
        System.out.println("hh" + propertyUtil.getBaseDir());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .maxAge(3600);
    }
}
