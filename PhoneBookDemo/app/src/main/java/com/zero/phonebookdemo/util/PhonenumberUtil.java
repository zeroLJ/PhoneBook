package com.zero.phonebookdemo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by watchvoice on 2016/12/25.
 */

public class PhonenumberUtil {

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNumber) {
        boolean flag = false;
        try {
            /*
            *中国手机电话号码都是1开头，总共有11位
            *
             */
            String telRegex = "[1]\\d{10}";
            Pattern regex = Pattern
                    .compile(telRegex);
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;

    }

    public static boolean isCallAbleNum(String mobileNumber){
        boolean flag = false;
        if (isMobileNO(mobileNumber)){
            return true;
        }else {
            try {
            /*
            *中国固话区号都是0开头，区号占3-4位，但总共都是11位
            *
             */
                String telRegex = "[0]\\d{10}";
                Pattern regex = Pattern
                        .compile(telRegex);
                Matcher matcher = regex.matcher(mobileNumber);
                flag = matcher.matches();
            } catch (Exception e) {
                flag = false;
            }
            return flag;
        }
//        return flag;
    }
}
