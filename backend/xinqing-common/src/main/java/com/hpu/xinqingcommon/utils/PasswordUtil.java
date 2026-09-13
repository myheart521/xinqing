package com.hpu.xinqingcommon.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import org.springframework.util.StringUtils;

public class PasswordUtil {
    public static String encrypt(String password) {
        // 随机盐值
        String salt = IdUtil.simpleUUID();
        // 密码（md5(随机盐值+密码)）
        String finalPassword = SecureUtil.md5(salt + password);
        return salt + "$" + finalPassword;
    }

    /**
     * 解密
     *
     * @param password       要验证的密码（未加密）
     * @param securePassword 数据库中的加了盐值的密码
     * @return
     */
    public static boolean decrypt(String password, String securePassword) {
        boolean result = false;

        if (StringUtils.hasLength(password) && StringUtils.hasLength(securePassword)) {
            if (securePassword.length() == 65 && securePassword.contains("$")) {
                String[] securePasswordArr = securePassword.split("\\$");
                // 盐值
                String slat = securePasswordArr[0];
                String finalPassword = securePasswordArr[1];
                // 使用同样的加密算法和随机盐值生成最终加密的密码
                password = SecureUtil.md5(slat + password);
                if (finalPassword.equals(password)) {
                    result = true;
                }
            }
            if(password.equals(securePassword)){
                result = true;
            }
        }
        return result;
    }
}
