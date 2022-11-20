package com.renmin.renminclouddisk.controller;

import com.renmin.renminclouddisk.util.VerifyCodeUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@CrossOrigin
public class CheckCodeController {
    @GetMapping("/checkCode")
    public void getImage(HttpSession session, HttpServletResponse response) throws IOException {
        String securityCode = VerifyCodeUtil.getSecurityCode();
        session.setAttribute("code", securityCode);
        BufferedImage image = VerifyCodeUtil.createImage(securityCode);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "png", outputStream);
    }
}
