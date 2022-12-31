package com.hape.netdisk.controller;

import com.hape.netdisk.pojo.Info;
import com.hape.netdisk.pojo.User;
import com.hape.netdisk.service.UserService;
import com.hape.netdisk.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private Info info;
    @Autowired
    private LogUtil logUtil;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * 检查用户名是否重复
     * @param uname
     * @return
     */
    @GetMapping("checkUname")
    public boolean checkUname(String uname){
        return userService.checkUname(uname);
    }

    /**
     * 校验验证码
     * @param session
     * @param code
     * @return
     */
    private boolean checkCode(HttpSession session,String code){
        String sysCode = (String)session.getAttribute("code");
        return sysCode.equalsIgnoreCase(code);
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @PostMapping("register")
    public Info register(User user, String code, HttpSession session){
        info.setStatus(false);
        if(!this.checkCode(session, code)){
            info.setMsg("验证码错误");
            return info;
        }
        //判断用户是否已存在
        if(userService.login(user)){
            info.setMsg("已有账号,请登录");
            return info;
        }
        if(!userService.register(user)){
            info.setMsg("注册失败,请稍后再试");
            return info;
        }
        //输出日志
        logUtil.outPutLog(user.getUname(),"注册","",logger,0);
        info.setStatus(true);
        info.setMsg("");
        return info;
    }

    /**
     * 登录
     * @param user
     * @param code
     * @param session
     * @return
     */
    @PostMapping("login")
    public Info login(User user,String code,HttpSession session){
        info.setStatus(false);
        //校验验证码
        if(!this.checkCode(session, code)){
            info.setMsg("验证码错误");
            return info;
        }
        //判断用户名和密码
        if(!userService.login(user)){
            info.setMsg("用户名或密码错误");
            return info;
        }
        //存储用户信息到session
        if(!this.saveSession(userService.findUser(user),session)){
            info.setMsg("服务器出现故障,请联系管理员");
            return info;
        }
        //输出日志
        logUtil.outPutLog(user.getUname(), "登录","",logger,0);
        info.setStatus(true);
        info.setMsg("");
        return info;
    }

    /**
     * 存储用户信息到session
     * @param user
     * @param session
     * @return
     */
    private boolean saveSession(User user,HttpSession session){
        session.setAttribute("user",user);
        return session.getAttribute("user")!=null;
    }

    /**
     * 获取session中user信息
     * @param session
     * @return
     */
    @GetMapping("/getUser")
    public User getUserInfo(HttpSession session){
        return (User) session.getAttribute("user");
    }

    /**
     * 获取用户网盘容量
     * @param session
     * @return
     */
    @GetMapping("/getVolume")
    public User getUserVolume(HttpSession session){
        User user = this.getUserInfo(session);
        if(user==null)return null;
        return userService.findVolume(user.getUid());
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @GetMapping("/getInfo")
    public User getUser(HttpSession session){
        User user = this.getUserInfo(session);
        if(user==null)return null;
        return userService.findInfo(user.getUid());
    }

    /**
     * 退出
     * @param session
     */
    @GetMapping("logOut")
    public void LogOut(HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user!=null){
            session.removeAttribute("user");
        }
    }

    /**
     * 修改密码
     * @param session
     * @param passwd 密码
     * @return
     */
    @PutMapping("changePasswd")
    public Info changePasswd(HttpSession session,String passwd,String code){
        info.setStatus(false);
        User user = this.getUserInfo(session);
        if(user==null){
            info.setMsg("未登录!");
            return info;
        }
        if(!this.checkCode(session,code)){
            info.setMsg("验证码错误");
            return info;
        }
        boolean flag = userService.changePasswd(user.getUid(), passwd);
        if(!flag){
            info.setMsg("系统繁忙,请稍候再试");
            return info;
        }
        session.removeAttribute("user");
        flag = this.saveSession(userService.findInfo(user.getUid()), session);
        if(!flag){
            info.setMsg("系统繁忙,请稍候再试");
            return info;
        }
        info.setMsg("");
        info.setStatus(true);
        return info;
    }
}
