package com.renmin.renminclouddisk.controller;

import com.renmin.renminclouddisk.pojo.Info;
import com.renmin.renminclouddisk.pojo.User;
import com.renmin.renminclouddisk.service.UserService;
import com.renmin.renminclouddisk.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    UserService userService;
    @Resource
    private Info info;
    @Resource
    private LogUtil logUtil;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("checkUname")
    public boolean checkUname(String uname) {
        return userService.checkUname(uname);
    }

    private boolean checkCode(HttpSession session, String code) {
        String sysCode = (String) session.getAttribute("code");
        return sysCode.equalsIgnoreCase(code);
    }

    @PostMapping("register")
    public Info register(User user, String code, HttpSession session) {
        info.setStatus(false);
        if (!this.checkCode(session, code)) {
            info.setMsg("验证码错误");
            return info;
        }
        if (userService.login(user)) {
            info.setMsg("已有账号,请登录");
            return info;
        }
        if (!userService.register(user)) {
            info.setMsg("注册失败,请稍后再试");
            return info;
        }
        logUtil.outputLog(user.getUname(), "注册", "", logger, 0);
        info.setStatus(true);
        info.setMsg("");
        return info;
    }

    @PostMapping("login")
    public Info login(User user, String code, HttpSession session) {
        info.setStatus(false);
        if (!this.checkCode(session, code)) {
            info.setMsg("验证码错误");
            return info;
        }
        if (!userService.login(user)) {
            info.setMsg("用户名或密码错误");
            return info;
        }
        if (!this.saveSession(userService.findUser(user), session)) {
            info.setMsg("服务器出现故障,请联系管理员");
            return info;
        }
        logUtil.outputLog(user.getUname(), "登录", "", logger, 0);
        info.setStatus(true);
        info.setMsg("");
        return info;
    }

    private boolean saveSession(User user, HttpSession session) {
        session.setAttribute("user", user);
        return session.getAttribute("user") != null;
    }

    @GetMapping("/getUser")
    public User getUserInfo(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping("/getVolume")
    public User getUserVolume(HttpSession session) {
        User user = this.getUserInfo(session);
        if (user == null) {
            return null;
        }
        return userService.findVolume(user.getUid());
    }

    @GetMapping("/getInfo")
    public User getUser(HttpSession session) {
        User user = this.getUserInfo(session);
        if (user == null) {
            return null;
        }
        return userService.findInfo(user.getUid());
    }

    @GetMapping("logOut")
    public void LogOut(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            session.removeAttribute("user");
        }
    }

    @PutMapping("changePasswd")
    public Info changePasswd(HttpSession session, String passwd, String code) {
        info.setStatus(false);
        User user = this.getUserInfo(session);
        if (user == null) {
            info.setMsg("未登录!");
            return info;
        }
        if (!this.checkCode(session, code)) {
            info.setMsg("验证码错误");
            return info;
        }
        boolean flag = userService.changePasswd(user.getUid(), passwd);
        if (!flag) {
            info.setMsg("系统繁忙,请稍候再试");
            return info;
        }
        session.removeAttribute("user");
        flag = this.saveSession(userService.findInfo(user.getUid()), session);
        if (!flag) {
            info.setMsg("系统繁忙,请稍候再试");
            return info;
        }
        info.setMsg("");
        info.setStatus(true);
        return info;
    }
}
