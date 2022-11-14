package com.renmin.renminclouddisk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordUtil {
    private static final String REG_NUMBER = ".*\\d+.*";
    private static final String REG_UPPERCASE = ".*[A-Z]+.*";
    private static final String REG_LOWERCASE = ".*[a-z]+.*";
    private static final String REG_SYMBOL = "[ _`~!@#$%^&*()+=|{}':;',\\[\\]" +
            ".<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";

    public static boolean checkPassword(String password) {
        Pattern pattern = Pattern.compile(REG_SYMBOL);
        Matcher matcher = pattern.matcher(password);
        if (password.length() < 6 || password.length() >16 || matcher.find()) {
            return false;
        }
        int i = 0;
        if (password.matches(REG_NUMBER)) {
            i++;
        }
        if (password.matches(REG_UPPERCASE)) {
            i++;
        }
        if (password.matches(REG_LOWERCASE)) {
            i++;
        }
        if (i < 2) {
            return false;
        }
        return true;
    }
}
