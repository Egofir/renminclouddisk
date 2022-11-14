package com.renmin.renminclouddisk.controller;

import com.renmin.renminclouddisk.common.ApiRestResponse;
import com.renmin.renminclouddisk.exception.RenMinCloudDiskExceptionEnum;
import com.renmin.renminclouddisk.service.UserService;
import com.renmin.renminclouddisk.util.PasswordUtil;
import com.renmin.renminclouddisk.util.SensitiveWordUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserController {
    @Resource
    UserService userService;

    public ApiRestResponse register(@RequestParam("username") String username,
                                    @RequestParam("password") String password) {
        if (StringUtils.hasText(username)) {
            return ApiRestResponse.error(RenMinCloudDiskExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.hasText(password)) {
            return ApiRestResponse.error(RenMinCloudDiskExceptionEnum.NEED_PASSWORD);
        }
        userService.setSensitiveWord();
        if (SensitiveWordUtil.mathWord(username)) {
            return ApiRestResponse.error(RenMinCloudDiskExceptionEnum.SENSITIVEWORD_EXISTED);
        }
        if (!PasswordUtil.checkPassword(password)) {
            return ApiRestResponse.error(RenMinCloudDiskExceptionEnum.PASSWORD_NOT_CONFORM);
        }
    }
}
