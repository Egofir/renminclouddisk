package com.renmin.renminclouddisk.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

public class VerifyCodeUtil {
    public enum SecurityCodeLevel {
        Simple, Medium, Hard
    }

    ;

    public static String getSecurityCode() {
        return getSecurityCode(4, SecurityCodeLevel.Medium, false);
    }

    public static String getSecurityCode(int length, SecurityCodeLevel level, boolean isCanRepeat) {
        int len = length;
        char[] codes = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };
        if (level == SecurityCodeLevel.Simple) {
            codes = Arrays.copyOfRange(codes, 0, 10);
        } else if (level == SecurityCodeLevel.Medium) {
            codes = Arrays.copyOfRange(codes, 0, 36);
        }
        int n = codes.length;
        if (len > n && !isCanRepeat) {
            throw new RuntimeException(String.format("调用SecurityCode.getSecurityCode(%1$s,%2$s,%3$s)" +
                            "出现异常，" + "当isCanRepeat为%3$s时，传入参数%1$s不能大于%4$s", len, level,
                    false, n));
        }
        char[] result = new char[len];
        if (isCanRepeat) {
            for (int i = 0; i < result.length; i++) {
                int r = (int) (Math.random() * n);
                result[i] = codes[r];
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                int r = (int) (Math.random() * n);
                result[i] = codes[r];
                codes[r] = codes[n - 1];
                n--;
            }
        }
        return String.valueOf(result);
    }

    public static BufferedImage createImage(String securityCode) {
        int codeLength = securityCode.length();
        int fontSize = 18;
        int fontWidth = fontSize + 1;
        int width = codeLength * fontWidth + 6;
        int height = fontSize * 2 + 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, height - 2));
        g.drawRect(0, 0, width - 1, height - 1);
        Random random = new Random();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < codeLength * 6; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.drawRect(x, y, 1, 1);
        }
        int codeY = height - 10;
        g.setColor(new Color(19, 148, 246));
        g.setFont(new Font("宋体", Font.BOLD, fontSize));
        for (int i = 0; i < codeLength; i++) {
            double deg = new Random().nextDouble() * 20;
            g.rotate(Math.toRadians(deg), i * 16 + 13, codeY - 7.5);
            g.drawString(String.valueOf(securityCode.charAt(i)), i * 16 + 5, codeY);
            g.rotate(Math.toRadians(-deg), i * 16 + 13, codeY - 7.5);
        }
        g.dispose();
        return image;
    }
}
